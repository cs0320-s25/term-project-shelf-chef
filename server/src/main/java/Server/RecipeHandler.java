package Server;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import java.util.stream.Collectors;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import spark.Request;
import spark.Response;
import spark.Route;

public class RecipeHandler implements Route {

  private final MongoCollection<Document> recipeCollection;

  public RecipeHandler(MongoClient mongoClient, String dbName, String collectionName) {
    MongoDatabase database = mongoClient.getDatabase("Recipes");
    this.recipeCollection = database.getCollection("recipes");
  }

  /**
   * Handles HTTP requests to search for recipes based on ingredients and optional dietary restrictions.
   * 
   * @param request The HTTP request containing query parameters:
   *                - "ingredients": comma-separated list of ingredients to search for (required)
   *                - "dietaryRestrictions": comma-separated list of dietary restrictions (optional)
   * @param response The HTTP response object to be returned.
   * @return A JSON-formatted success or failure response serialized as a string.
   * @throws Exception If any error occurs during serialization or processing.
   */
  @Override
  public Object handle(Request request, Response response) {
    String ingredientsParam = request.queryParams("ingredients");
    String dietaryRestrictionsParam = request.queryParams("dietaryRestrictions");
    Map<String, Object> jsonResponse = new HashMap<>();

    if (ingredientsParam == null || ingredientsParam.isEmpty()) {
      jsonResponse.put("response", "error_bad_request");
      jsonResponse.put("message", "Missing required parameter: ingredients");
      return new RecipeFailureResponse(
          (String) jsonResponse.get("response"),
          (String) jsonResponse.get("message")
      ).serialize();
    }

    try {
      // Parse and normalize ingredients
      String[] searchIngredients = ingredientsParam.toLowerCase().split(",\\s*");

      // Create a filter for each ingredient
      List<Bson> filters = new ArrayList<>();
      for (String ingredient : searchIngredients) {
          filters.add(Filters.regex("extendedIngredients", Pattern.compile(ingredient, Pattern.CASE_INSENSITIVE)));
      }

      // Add dietary restriction filters if provided
      if (dietaryRestrictionsParam != null && !dietaryRestrictionsParam.isEmpty()) {
          String[] restrictions = dietaryRestrictionsParam.toLowerCase().split(",\\s*");
          
          for (String restriction : restrictions) {
              switch (restriction.trim()) {
                  case "vegan":
                      filters.add(Filters.eq("vegan", true));
                      break;
                  case "vegetarian":
                      filters.add(Filters.eq("vegetarian", true));
                      break;
                  case "glutenfree":
                      filters.add(Filters.eq("glutenFree", true));
                      break;
                  case "dairyfree":
                      filters.add(Filters.eq("dairyFree", true));
                      break;
                  case "lowfodmap":
                      filters.add(Filters.eq("lowFodmap", true));
                      break;
                  default:
                      // Skip unknown dietary restrictions
                      break;
              }
          }
      }

      // Combine filters with AND
      Bson filter = Filters.and(filters);

      // Query MongoDB
      FindIterable<Document> results = recipeCollection.find(filter);

      Set<String> inputSet = new HashSet<>(List.of(searchIngredients));

      List<Map<String, Object>> matchingRecipes = new ArrayList<>();

      for (Document doc : results) {
        Map<String, Object> recipe = docToMap(doc);

        // Ingredients required by the recipe
        List<String> recipeIngredients = ((List<?>) doc.get("extendedIngredients"))
            .stream()
            .map(Object::toString)
            .map(String::toLowerCase)
            .collect(Collectors.toList());

        // missingCount: ingredients the recipe needs but are NOT in pantry
        long missingCount = recipeIngredients.stream()
            .filter(recipeIng -> inputSet.stream().noneMatch(recipeIng::contains))
            .count();

        // extraCount: pantry ingredients that are NOT used in this recipe
        long extraCount = inputSet.stream()
            .filter(pantryIng -> recipeIngredients.stream().noneMatch(recipeIng -> recipeIng.contains(pantryIng)))
            .count();

        recipe.put("missingCount", missingCount);
        recipe.put("extraCount", extraCount);
        recipe.put("matchScore", missingCount + extraCount); // for sorting

        matchingRecipes.add(recipe);
      }

      // Sort recipes to prioritize simplest ones (least extra ingredients)
      matchingRecipes.sort(Comparator.comparingInt(r -> (int) (long) r.get("matchScore")));

      jsonResponse.put("response", "success");
      jsonResponse.put("recipes", matchingRecipes);
      return new RecipeSuccessResponse("success", matchingRecipes).serialize();

    } catch (Exception e) {
      jsonResponse.put("response", "error_server");
      jsonResponse.put("message", "Server error: " + e.getMessage());
      return new RecipeFailureResponse(
          "error_server",
          "Server error: " + e.getMessage()
      ).serialize();
    }
  }

  private Map<String, Object> docToMap(Document doc) {
    Map<String, Object> map = new HashMap<>();
    for (Map.Entry<String, Object> entry : doc.entrySet()) {
      map.put(entry.getKey(), entry.getValue());
    }
    return map;
  }

  public record RecipeSuccessResponse(String response, List<Map<String, Object>> recipes) {
    String serialize() {
      try {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<RecipeSuccessResponse> adapter = moshi.adapter(RecipeSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

  public record RecipeFailureResponse(String response, String message) {
    String serialize() {
      try {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<RecipeFailureResponse> adapter = moshi.adapter(RecipeFailureResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }
}
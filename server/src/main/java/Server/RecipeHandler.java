package Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;
import CSV.CSVUtilities;

public class RecipeHandler implements Route {

  /**
   * Handles HTTP requests to search for recipes based on ingredients.
   * 
   * @param request The HTTP request containing query parameters:
   *                - "ingredients": comma-separated list of ingredients to search for
   * @param response The HTTP response object to be returned.
   * @return A JSON-formatted success or failure response serialized as a string.
   * @throws Exception If any error occurs during serialization or processing.
   */
  @Override
  public Object handle(Request request, Response response) {
    String ingredientsParam = request.queryParams("ingredients");
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
      // Get the loaded CSV data
      List<List<String>> data = CSVUtilities.getLoadedData();
      if (data == null || data.isEmpty()) {
        jsonResponse.put("response", "error_no_data");
        jsonResponse.put("message", "No recipe data loaded");
        return new RecipeFailureResponse(
          (String) jsonResponse.get("response"),
          (String) jsonResponse.get("message")
      ).serialize();
      }

      // Get the headers
      List<String> headers = CSVUtilities.getParser().getHeaders();
      int ingredientsIndex = -1;
      
      // Find the index of the ingredients column
      for (int i = 0; i < headers.size(); i++) {
        if (headers.get(i).equalsIgnoreCase("ingredients")) {
          ingredientsIndex = i;
          break;
        }
      }

      if (ingredientsIndex == -1) {
        jsonResponse.put("response", "error_bad_request");
        jsonResponse.put("message", "Ingredients column not found in CSV");
        return new RecipeFailureResponse(
          (String) jsonResponse.get("response"),
          (String) jsonResponse.get("message")
      ).serialize();
      }

      // Split the ingredients parameter into individual ingredients
      String[] searchIngredients = ingredientsParam.toLowerCase().split(",\s*");
      List<List<String>> matchingRecipes = new ArrayList<>();

      // Search through each row in the CSV
      for (List<String> row : data) {
        if (row.size() <= ingredientsIndex) continue;

        // Get the ingredients for this recipe
        String recipeIngredients = row.get(ingredientsIndex).toLowerCase();
        
        // Check if all search ingredients are present in the recipe
        boolean allIngredientsMatch = true;
        for (String searchIngredient : searchIngredients) {
          if (!recipeIngredients.contains(searchIngredient)) {
            allIngredientsMatch = false;
            break;
          }
        }

        if (allIngredientsMatch) {
          matchingRecipes.add(row);
        }
      }

      // Return the matching recipes
      jsonResponse.put("response", "success");
      jsonResponse.put("recipes", matchingRecipes);
      return new RecipeSuccessResponse(
          (String) jsonResponse.get("response"),
          (List<List<String>>) jsonResponse.get("recipes")
      ).serialize();

    } catch (Exception e) {
      jsonResponse.put("response", "error_server");
      jsonResponse.put("message", "Server error: " + e.getMessage());
      return new RecipeFailureResponse(
          (String) jsonResponse.get("response"),
          (String) jsonResponse.get("message")
      ).serialize();
    }
  }

  /**
   * Success response for recipe search.
   */
  public record RecipeSuccessResponse(String response, List<List<String>> recipes) {
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

  /**
   * Failure response for recipe search.
   */
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


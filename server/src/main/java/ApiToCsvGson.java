import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ApiToCsvGson {

  public static void main(String[] args) {

    for (int call = 1; call <= 100; call++) {
      System.out.println("Making API call #" + call);

      try {
        String apiUrl = "https://api.spoonacular.com/recipes/random?number=100&apiKey=d021f9f6990e49568386806bdc70abe8"; // <-- replace
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(
            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
        );
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          responseBuilder.append(line);
        }
        reader.close();
        String response = responseBuilder.toString();

        File file = new File("recipe_output.csv");

        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        JsonArray recipes = jsonObject.getAsJsonArray("recipes");

        FileWriter csvWriter = new FileWriter("recipe_output.csv", true);

        // Write header
        if (!file.exists()) {
          csvWriter.append(
              "id,image,imageType,title,readyInMinutes,servings,sourceUrl,vegetarian,vegan,glutenFree,dairyFree,veryHealthy,cheap,veryPopular,sustainable,lowFodmap,weightWatcherSmartPoints,gaps,preparationMinutes,cookingMinutes,aggregateLikes,healthScore,creditsText,license,sourceName,pricePerServing,extendedIngredients,summary,cuisines,dishTypes,diets,occasions,instructions,spoonacularScore,spoonacularSourceUrl\n");
        }

        // Write each recipe
        for (int i = 0; i < recipes.size(); i++) {
          JsonObject r = recipes.get(i).getAsJsonObject();

          csvWriter.append(
              safe(r, "id") + "," +
                  safe(r, "image") + "," +
                  safe(r, "imageType") + "," +
                  sanitize(r.get("title").getAsString()) + "," +
                  safe(r, "readyInMinutes") + "," +
                  safe(r, "servings") + "," +
                  safe(r, "sourceUrl") + "," +
                  safe(r, "vegetarian") + "," +
                  safe(r, "vegan") + "," +
                  safe(r, "glutenFree") + "," +
                  safe(r, "dairyFree") + "," +
                  safe(r, "veryHealthy") + "," +
                  safe(r, "cheap") + "," +
                  safe(r, "veryPopular") + "," +
                  safe(r, "sustainable") + "," +
                  safe(r, "lowFodmap") + "," +
                  safe(r, "weightWatcherSmartPoints") + "," +
                  safe(r, "gaps") + "," +
                  safe(r, "preparationMinutes") + "," +
                  safe(r, "cookingMinutes") + "," +
                  safe(r, "aggregateLikes") + "," +
                  safe(r, "healthScore") + "," +
                  safe(r, "creditsText") + "," +
                  safe(r, "license") + "," +
                  safe(r, "sourceName") + "," +
                  safe(r, "pricePerServing") + "," +
                  flattenExtendedIngredients(r.getAsJsonArray("extendedIngredients")) + "," +
                  sanitize(r.get("summary").getAsString()) + "," +
                  flattenArray(r.getAsJsonArray("cuisines")) + "," +
                  flattenArray(r.getAsJsonArray("dishTypes")) + "," +
                  flattenArray(r.getAsJsonArray("diets")) + "," +
                  flattenArray(r.getAsJsonArray("occasions")) + "," +
                  sanitize(r.get("instructions").getAsString()) + "," +
                  safe(r, "spoonacularScore") + "," +
                  safe(r, "spoonacularSourceUrl")
          );
          csvWriter.append("\n");
        }

        csvWriter.flush();
        csvWriter.close();
        System.out.println("CSV file created successfully with all fields.");

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  // Helper to sanitize commas/newlines
  private static String sanitize(String input) {
    if (input == null) return "";
    return "\"" + input.replace("\"", "'").replace("\n", " ").replace("\r", " ") + "\""; // wrap in quotes
  }

  // Helper to safely get simple fields
  private static String safe(JsonObject obj, String key) {
    if (!obj.has(key) || obj.get(key).isJsonNull()) return "";
    if (obj.get(key).isJsonPrimitive()) {
      return sanitize(obj.get(key).getAsString());
    }
    return sanitize(obj.get(key).toString());
  }

  // Flatten an array of strings (cuisines, diets, dishTypes, occasions)
  private static String flattenArray(JsonArray arr) {
    if (arr == null || arr.size() == 0) return "\"\"";
    StringBuilder sb = new StringBuilder("\"");
    for (int i = 0; i < arr.size(); i++) {
      sb.append(arr.get(i).getAsString());
      if (i < arr.size() - 1) sb.append("; ");
    }
    sb.append("\"");
    return sb.toString();
  }

  // Flatten extendedIngredients into a single string
  private static String flattenExtendedIngredients(JsonArray arr) {
    if (arr == null || arr.size() == 0) return "\"\"";
    StringBuilder sb = new StringBuilder("\"");
    for (int i = 0; i < arr.size(); i++) {
      JsonObject ingredient = arr.get(i).getAsJsonObject();
      sb.append(ingredient.get("name").getAsString());
      if (i < arr.size() - 1) sb.append("; ");
    }
    sb.append("\"");
    return sb.toString();
  }
}

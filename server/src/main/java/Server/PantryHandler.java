package Server;

import com.google.gson.Gson;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.conversions.Bson;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import spark.Request;
import spark.Response;
import spark.Route;

import org.bson.Document;


public class PantryHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
        String userId = request.queryParams("userid");
        String name = request.queryParams("name");
        String expirationString = request.queryParams("expiration");
        String quantityString = request.queryParams("quantity");
        String delete = request.queryParams("delete");
        String fetch = request.queryParams("fetch");
        String update = request.queryParams("update");

        Map<String, Object> jsonResponse = new HashMap<>();

        String connectionString = "mongodb+srv://ryanma1:DsHucS2aJltLkIp9@recipes.otteuip.mongodb.net/?retryWrites=true&w=majority&appName=Recipes";
        ServerApi serverApi = ServerApi.builder()
            .version(ServerApiVersion.V1)
            .build();
        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(connectionString))
            .serverApi(serverApi)
            .build();

        try (MongoClient mongoClient = MongoClients.create(settings)) {
            MongoDatabase database = mongoClient.getDatabase("UserPantries");
            MongoCollection<Document> collection = database.getCollection("pantries");

            Bson idFilter = Filters.eq("userId", userId);

            if (fetch != null && fetch.equalsIgnoreCase("true")) {
                Document userDoc = collection.find(idFilter).first();

                if (userDoc != null && userDoc.containsKey("pantry")) {
                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", "Pantry fetched successfully");
                    jsonResponse.put("pantry", userDoc.get("pantry"));
                } else {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Pantry not found for user");
                }

                return new Document(jsonResponse).toJson();
            }

            if (update != null && update.equalsIgnoreCase("true")) {
                Document userDoc = collection.find(idFilter).first();

                if (userDoc != null && userDoc.containsKey("pantry")) {
                    // Match the pantry item with BOTH name and expirationDate
                    Bson pantryMatch = Filters.elemMatch("pantry", Filters.and(
                        Filters.eq("name", name),
                        Filters.eq("expirationDate", expirationString)
                    ));

                    // Update the quantity of that pantry item
                    Bson updateQuantity = Updates.set("pantry.$.quantity", Integer.parseInt(quantityString));

                    // Perform the update where the user matches and pantry item matches
                    UpdateResult result = collection.updateOne(Filters.and(idFilter, pantryMatch), updateQuantity);

                    if (result.getModifiedCount() > 0) {
                        jsonResponse.put("status", "success");
                        jsonResponse.put("message", "Ingredient quantity updated");
                    } else {
                        jsonResponse.put("status", "error");
                        jsonResponse.put("message", "Matching ingredient not found in pantry");
                    }
                } else {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Pantry not found for user");
                }

                return new Document(jsonResponse).toJson();
            }

            if (delete != null && delete.equalsIgnoreCase("true")) {
                Document userDoc = collection.find(idFilter).first();

                if (userDoc != null && userDoc.containsKey("pantry")) {
                    // Match the pantry item with BOTH name and expirationDate
                    Bson pantryMatch = Filters.elemMatch("pantry", Filters.and(
                        Filters.eq("name", name),
                        Filters.eq("expirationDate", expirationString)
                    ));

                    // Define the exact ingredient to remove
                    Document ingredientToRemove = new Document("name", name)
                        .append("expirationDate", expirationString);

                    // Remove the matching ingredient from the pantry array
                    Bson updated = Updates.pull("pantry", ingredientToRemove);

                    // Perform the update where the user matches and pantry item matches
                    UpdateResult result = collection.updateOne(Filters.and(idFilter, pantryMatch), updated);

                    if (result.getModifiedCount() > 0) {
                        jsonResponse.put("status", "success");
                        jsonResponse.put("message", "Ingredient removed from pantry");
                    } else {
                        jsonResponse.put("status", "error");
                        jsonResponse.put("message", "Matching ingredient not found in pantry");
                    }
                } else {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Pantry not found for user");
                }

                return new Document(jsonResponse).toJson();
            }

            else {
                int quantity = Integer.parseInt(quantityString);

                // Ensure the user document exists with an empty pantry array if needed
                Bson ensurePantryExists = Updates.setOnInsert("pantry", new ArrayList<>());
                UpdateOptions options = new UpdateOptions().upsert(true);
                collection.updateOne(idFilter, ensurePantryExists, options);

                // Try to find and increment quantity if the ingredient already exists
                Bson pantryMatch = Filters.elemMatch("pantry", Filters.and(
                    Filters.eq("name", name),
                    Filters.eq("expirationDate", expirationString)
                ));
                Bson updateQuantity = Updates.inc("pantry.$.quantity", quantity);
                UpdateResult result = collection.updateOne(Filters.and(idFilter, pantryMatch), updateQuantity);

                // If no matching ingredient found, push a new one
                if (result.getModifiedCount() == 0) {
                    Document newIngredient = new Document("name", name)
                        .append("expirationDate", expirationString)
                        .append("quantity", quantity);
                    Bson pushNew = Updates.push("pantry", newIngredient);
                    collection.updateOne(idFilter, pushNew);
                }

                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Ingredient added/updated in pantry");
                Gson gson = new Gson();
                return gson.toJson(jsonResponse);
            }


        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Error processing request: " + e.getMessage());
        }

        return jsonResponse.toString();
    }
}

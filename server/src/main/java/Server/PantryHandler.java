package Server;

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
            
            if (delete != null && delete.equalsIgnoreCase("true")) {
                Bson deleteFilter = Filters.eq("pantry.name", name);
                Bson update = Updates.pull("pantry", new Document("name", name));
                UpdateResult result = collection.updateOne(Filters.and(idFilter, deleteFilter), update);
                
                if (result.getModifiedCount() > 0) {
                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", "Ingredient removed from pantry");
                } else {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Ingredient not found in pantry");
                }
            } else {
                int quantity = Integer.parseInt(quantityString);
                
                Bson update = Updates.setOnInsert("pantry", new ArrayList<>());
                UpdateOptions options = new UpdateOptions().upsert(true);
                
                collection.updateOne(idFilter, update, options);
                
                Bson pantryFilter = Filters.eq("pantry.name", name);
                Bson updateQuant = Updates.inc("pantry.$.quantity", quantity);

                UpdateResult result = collection.updateOne(Filters.and(idFilter, pantryFilter), updateQuant);
                if (result.getModifiedCount() == 0) {
                    Document newIngredient = new Document("name", name)
                        .append("expirationDate", expirationString)
                        .append("quantity", quantity);
                    Bson updatePantry = Updates.push("pantry", newIngredient);
                    collection.updateOne(idFilter, updatePantry);
                }
                
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Ingredient added/updated in pantry");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Error processing request: " + e.getMessage());
        }

        return jsonResponse;
    }
}
    

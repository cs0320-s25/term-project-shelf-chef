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
import org.json.JSONArray;
import org.json.JSONObject;

public class PantryHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
        String userId = request.queryParams("userid");
        String name = request.queryParams("name");
        String expirationString = request.queryParams("expiration");
        String quantityString = request.queryParams("quantity");

        int quantity = Integer.parseInt(quantityString);

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
                    collection.updateOne(
                        Filters.eq("userId", userId),
                        Updates.setOnInsert("pantry", new ArrayList<>()),
                        new UpdateOptions().upsert(true)
                    );

                    UpdateResult result = collection.updateOne(
                    Filters.and(
                        Filters.eq("userId", userId),
                        Filters.eq("pantry.name", name),
                        Filters.eq("pantry.expirationDate", expirationString) // optional if tracking per batch
                    ),
                    Updates.inc("pantry.$.quantity", quantity));
                    if (result.getModifiedCount() == 0) {
                        Document newIngredient = new Document("name", name)
                            .append("expirationDate", expirationString)
                            .append("quantity", quantity);
            
                        collection.updateOne(
                            Filters.eq("userId", userId),
                            Updates.push("pantry", newIngredient)
                        );
                    }

        
                } catch (Exception e) {
                    jsonResponse.put("error", "could not add item to pantry");
                }

        return jsonResponse;
    }
}
    

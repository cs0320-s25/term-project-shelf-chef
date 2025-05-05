package Server;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertManyOptions;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

public class MongoConnection {
    public static void main(String[] args) {
        String connectionString = "mongodb+srv://ryanma1:DsHucS2aJltLkIp9@recipes.otteuip.mongodb.net/?retryWrites=true&w=majority&appName=Recipes";
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
                
        String jsonFilePath = "server/recipes.json";
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            MongoDatabase database = mongoClient.getDatabase("Recipes"); 
            MongoCollection<Document> collection = database.getCollection("recipes");

            // Load JSON file
            String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            JSONArray recipesArray = new JSONArray(content);

            List<Document> documents = new ArrayList<>();
            for (int i = 0; i < recipesArray.length(); i++) {
                JSONObject jsonObject = recipesArray.getJSONObject(i);
                documents.add(Document.parse(jsonObject.toString()));
            }

            collection.insertMany(documents, new InsertManyOptions().ordered(false));
            System.out.println("Successfully inserted recipes into MongoDB.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
    


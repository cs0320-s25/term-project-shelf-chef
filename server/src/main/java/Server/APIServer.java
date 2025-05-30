package Server;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import JSONParser.DataSource;
import spark.Spark;
import static spark.Spark.before;
import static spark.Spark.options;
import static spark.Spark.post;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

public class APIServer {

  public static ACSDataCache acsDataCache = new ACSDataCache.Builder()
      .setMaxSize(2)
      .build();

  public static void main(String[] args) {
    int port = 3600;
    Spark.port(port);

    // Enable CORS
    options("/*", (request, response) -> {
      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }
      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }
      return "OK";
    });

    // Set CORS headers for all requests
    before((request, response) -> {
      response.header("Access-Control-Allow-Origin", "*");
      response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
      response.header("Access-Control-Allow-Headers", "Content-Type, Authorization, Content-Length, X-Requested-With");
    });


    MongoClient mongoClient = MongoClients.create("mongodb+srv://kyle:kylepassword@recipes.otteuip.mongodb.net/?retryWrites=true&w=majority&appName=Recipes");
    RecipeHandler handler = new RecipeHandler(mongoClient, "database", "recipes");


    Spark.get("load", new LoadHandler());
    Spark.get("view", new ViewHandler());
    Spark.get("search", new SearchHandler());
    Spark.get("broadband", new BroadBandHandler(new DataSource()));

    Spark.get("recipes", handler);


    post("/receipt", new ReceiptHandler());

    Spark.get("addPantry", new PantryHandler());

    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }
}
package Server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import JSONParser.DataSource;
import spark.Spark;
import static spark.Spark.after;

public class APIServer {

  public static ACSDataCache acsDataCache = new ACSDataCache.Builder()
      .setMaxSize(2)
      .build();

  public static void main(String[] args) {
    int port = 3600;
    Spark.port(port);

    // Set CORS headers
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    MongoClient mongoClient = MongoClients.create("mongodb+srv://amasthay@cs.brown.edu:Testing!@#$%@recipes.otteuip.mongodb.net/?retryWrites=true&w=majority&appName=Recipes");
    RecipeHandler handler = new RecipeHandler(mongoClient, "database", "recipes");

    Spark.get("load", new LoadHandler());
    Spark.get("view", new ViewHandler());
    Spark.get("search", new SearchHandler());
    Spark.get("broadband", new BroadBandHandler(new DataSource()));
    Spark.get("recipes", handler);
    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }
}

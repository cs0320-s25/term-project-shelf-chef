package Server;

import spark.Spark;
import spark.Route;
import spark.Request;
import spark.Response;
import static spark.Spark.*;
import spark.staticfiles.*;
import JSONParser.IDataSource;
import JSONParser.DataSource;
import Server.ACSDataCache;
import Server.RecipeHandler;
import CSV.CSVUtilities;
import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.options;


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

    Spark.get("load", new LoadHandler());
    Spark.get("view", new ViewHandler());
    Spark.get("search", new SearchHandler());
    Spark.get("broadband", new BroadBandHandler(new DataSource()));
    Spark.get("recipes", new RecipeHandler());
    post("/receipt", new ReceiptHandler());
    
    Spark.get("addPantry", new PantryHandler());
    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }
}

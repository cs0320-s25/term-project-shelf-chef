package Server;

import static spark.Spark.after;

import JSONParser.DataSource;
import spark.Spark;

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

    Spark.get("load", new LoadHandler());
    Spark.get("view", new ViewHandler());
    Spark.get("search", new SearchHandler());
    Spark.get("broadband", new BroadBandHandler(new DataSource()));
    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }
}

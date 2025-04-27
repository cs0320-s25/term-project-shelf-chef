package edu.brown.cs.student;

import static org.junit.jupiter.api.Assertions.assertEquals;


import Server.APIServer;
import Server.BroadBandHandler;
import JSONParser.DataSource;
import Server.LoadHandler;
import Server.SearchHandler;
import Server.ViewHandler;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;


public class TestBroadBand {

  private DataSource dataSource;
  private DataSource DataSource;
  private BroadBandHandler handler;

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  @BeforeEach
  public void setup() {
    DataSource = new DataSource();
    Spark.get("load", new LoadHandler());
    Spark.get("view", new ViewHandler());
    Spark.get("search", new SearchHandler());
    Spark.get("broadband", new BroadBandHandler(DataSource));
    Spark.init();
    Spark.awaitInitialization();

  }

  @AfterEach
  public void teardown() {
    Spark.unmap("load");
    Spark.unmap("search");
    Spark.unmap("view");
    Spark.unmap("broadband");
    Spark.awaitStop();
  }

  /**
   * Helper to start a connection to a specific API endpoint/params
   *
   * @param apiCall the call string, including endpoint (NOTE: this would be better if it had more
   *     structure!)
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  @Test
  public void testNormalBroadbandSearch()
      throws IOException, URISyntaxException, InterruptedException {
    HttpURLConnection clientConnection = tryRequest("broadband?state=Michigan&county=Shiawassee%20County");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    BroadBandHandler.BroadbandSuccessResponse response =
        moshi
            .adapter(BroadBandHandler.BroadbandSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("success", response.response());
    assertEquals("Michigan", response.state());
    assertEquals("Shiawassee County", response.county());
    assertEquals(DataSource.getBroadbandPercentage("26", "155"), response.broadband_percentage());
  }

  @Test
  public void testBadCountry() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband?state=Michiga&county=Shiawassee%20County");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    BroadBandHandler.BroadbandFailureResponse response =
        moshi
            .adapter(BroadBandHandler.BroadbandFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_bad_request", response.response());
    assertEquals("Error: Invalid state name: Michiga", response.message());

  }

  @Test
  public void testBadCounty() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband?state=Michigan&county=Shiawasee%20County");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    BroadBandHandler.BroadbandFailureResponse response =
        moshi
            .adapter(BroadBandHandler.BroadbandFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_bad_request", response.response());
    assertEquals("Error: Invalid county name: Shiawasee County", response.message());
  }

  @Test
  public void testLoadSearchBroadband() throws IOException, URISyntaxException, InterruptedException {

    HttpURLConnection clientConnection = tryRequest("load?filePath=data/small/small_data.csv");
    clientConnection.disconnect();
    HttpURLConnection clientConnection2 = tryRequest("search?query=entry2&column=header2");
    assertEquals(200, clientConnection2.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    SearchHandler.SearchSuccessResponse actualResponse =
        moshi
            .adapter(SearchHandler.SearchSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));

    // Step 4: Create the expected response structure
    Map<String, Object> expectedResponseMap = new HashMap<>();
    expectedResponseMap.put("response", "success");
    expectedResponseMap.put("data", List.of()); // Assuming no matching results
    expectedResponseMap.put("filename", "data/small/small_data.csv");
    expectedResponseMap.put("stringToSearchFor", "entry2");
    expectedResponseMap.put("columnIdentifier", "header2");

    SearchHandler.SearchSuccessResponse expectedResponse =
        new SearchHandler.SearchSuccessResponse(expectedResponseMap);

    // Step 5: Compare the actual response with the expected response
    assertEquals(expectedResponse.responseMap(), actualResponse.responseMap());
    clientConnection2.disconnect();

    HttpURLConnection clientConnection3 = tryRequest("broadband?state=Michigan&county=Shiawassee%20County");
    assertEquals(200, clientConnection3.getResponseCode());
    Moshi moshi3 = new Moshi.Builder().build();
    BroadBandHandler.BroadbandSuccessResponse response =
        moshi3
            .adapter(BroadBandHandler.BroadbandSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection3.getInputStream()));
    assertEquals("success", response.response());
    assertEquals("Michigan", response.state());
    assertEquals("Shiawassee County", response.county());
    assertEquals(DataSource.getBroadbandPercentage("26", "155"), response.broadband_percentage());
    clientConnection3.disconnect();
  }

  @Test
  public void testAPIViewCSV() throws IOException, URISyntaxException, InterruptedException {
    HttpURLConnection clientConnection = tryRequest("load?filePath=data/small/small_data.csv");
    assertEquals(200, clientConnection.getResponseCode());
    clientConnection.disconnect();
    HttpURLConnection clientConnection2 = tryRequest("view");
    assertEquals(200, clientConnection2.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    ViewHandler.ViewSuccessResponse response =
        moshi
            .adapter(ViewHandler.ViewSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));
    assertEquals("success", response.response());
    assertEquals("[[entry3, entry4]]", response.data().toString());
    clientConnection2.disconnect();
    HttpURLConnection clientConnection3 = tryRequest("broadband?state=Michigan&county=Shiawassee%20County");
    assertEquals(200, clientConnection3.getResponseCode());
    Moshi moshi2 = new Moshi.Builder().build();
    BroadBandHandler.BroadbandSuccessResponse response2 =
        moshi2
            .adapter(BroadBandHandler.BroadbandSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection3.getInputStream()));
    assertEquals("success", response2.response());
    assertEquals("Michigan", response2.state());
    assertEquals("Shiawassee County", response2.county());
    assertEquals(DataSource.getBroadbandPercentage("26", "155"), response2.broadband_percentage());
    clientConnection.disconnect();
  }

}
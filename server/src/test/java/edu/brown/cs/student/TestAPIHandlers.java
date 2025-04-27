package edu.brown.cs.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import JSONParser.DataSource;
import Server.BroadBandHandler;
import Server.BroadBandHandler.BroadbandFailureResponse;
import Server.BroadBandHandler.BroadbandSuccessResponse;
import Server.LoadHandler;
import Server.SearchHandler;
import Server.ViewHandler;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.net.HttpURLConnection;
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

public class TestAPIHandlers {

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  @BeforeEach
  public void setup() {
    // Re-initialize state, etc. for _every_ test method run

    // In fact, restart the entire Spark server for every test!
    Spark.get("load", new LoadHandler());
    Spark.get("view", new ViewHandler());
    Spark.get("search", new SearchHandler());
    Spark.get("broadband", new BroadBandHandler(new DataSource()));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints after each test
    // Ensure no data is loaded for consistent tests
    Spark.unmap("load");
    Spark.unmap("search");
    Spark.unmap("view");
    Spark.unmap("broadband");
    Spark.stop();
    Spark.awaitStop(); // don't proceed until the server is stopped
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
  public void testAPILoadCSV() throws IOException {
    HttpURLConnection clientConnection = tryRequest("load?filePath=data/census/income_by_race.csv");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    LoadHandler.LoadSuccessResponse response =
        moshi
            .adapter(LoadHandler.LoadSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    Map<String, Object> result = new HashMap<>();
    result.put("response", "success");
    result.put("filepath", "data/census/income_by_race.csv");
    assertEquals(
        result,
        moshi
            .adapter(Map.class)
            .fromJson(moshi.adapter(LoadHandler.LoadSuccessResponse.class).toJson(response)));
    clientConnection.disconnect();
  }

  @Test
  public void testAPILoadBadCSV() throws IOException {
    HttpURLConnection clientConnection = tryRequest("load?filePath=lkdfjha");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    LoadHandler.LoadFailureResponse response =
        moshi
            .adapter(LoadHandler.LoadFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_bad_json", response.response());
    assertEquals("Error: file path does not exist", response.message());
    clientConnection.disconnect();
  }

  @Test
  public void testAPILoadViewLoadViewCSV() throws IOException {
    HttpURLConnection clientConnection = tryRequest("load?filePath=data/census/income_by_race.csv");
    assertEquals(200, clientConnection.getResponseCode());
    clientConnection.disconnect();

    HttpURLConnection clientConnection2 = tryRequest("view");
    assertEquals(200, clientConnection2.getResponseCode());
    clientConnection2.disconnect();

    HttpURLConnection clientConnection3 = tryRequest("load?filePath=data/census/postsecondary_education.csv");
    assertEquals(200, clientConnection2.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    LoadHandler.LoadSuccessResponse response =
        moshi
            .adapter(LoadHandler.LoadSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection3.getInputStream()));

    Map<String, Object> result = new HashMap<>();
    result.put("response", "success");
    result.put("filepath", "data/census/postsecondary_education.csv");

    assertEquals(
        result,
        moshi
            .adapter(Map.class)
            .fromJson(moshi.adapter(LoadHandler.LoadSuccessResponse.class).toJson(response)));

    clientConnection3.disconnect();
  }


  @Test
  public void testAPISearchCSV() throws IOException, InterruptedException {

    HttpURLConnection clientConnection = tryRequest("load?filePath=data/small/small_data.csv");
    clientConnection.disconnect();
    HttpURLConnection clientConnection2 = tryRequest("search?query=entry1&column=header2");
    assertEquals(200, clientConnection2.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    SearchHandler.SearchSuccessResponse actualResponse =
        moshi
            .adapter(SearchHandler.SearchSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));
    System.out.println(actualResponse);

    // Step 4: Create the expected response structure
    Map<String, Object> expectedResponseMap = new HashMap<>();
    expectedResponseMap.put("response", "success");
    expectedResponseMap.put("data", List.of()); // Assuming no matching results
    expectedResponseMap.put("filename", "data/small/small_data.csv");
    expectedResponseMap.put("stringToSearchFor", "entry1");
    expectedResponseMap.put("columnIdentifier", "header2");

    SearchHandler.SearchSuccessResponse expectedResponse =
        new SearchHandler.SearchSuccessResponse(expectedResponseMap);

    // Step 5: Compare the actual response with the expected response
    assertEquals(expectedResponse.responseMap(), actualResponse.responseMap());
    clientConnection2.disconnect();
  }


  @Test
  public void testAPILoadBadOutsideData() throws IOException {
    HttpURLConnection clientConnection = tryRequest("load?filePath=config");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    LoadHandler.LoadFailureResponse response =
        moshi
            .adapter(LoadHandler.LoadFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_bad_request", response.response());
    assertEquals("Error: file is not within the data directory", response.message());
    clientConnection.disconnect();
  }

  @Test
  public void testAPILoadNoParam() throws IOException {
    HttpURLConnection clientConnection = tryRequest("load");
    assertEquals(200, clientConnection.getResponseCode());
    Moshi moshi = new Moshi.Builder().build();
    LoadHandler.LoadFailureResponse response =
        moshi
            .adapter(LoadHandler.LoadFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_bad_json", response.response());
    assertEquals("Error: file path was not provided", response.message());
    clientConnection.disconnect();
  }

  @Test
  public void testAPIViewCSV() throws IOException {
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
  }

  @Test
  public void testAPIBroadband() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("broadband?state=Michigan&county=Shiawassee%20County");

    // Verify HTTP response code
    assertEquals(200, clientConnection.getResponseCode());

    // Deserialize response using Moshi
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<BroadbandSuccessResponse> adapter =
        moshi.adapter(BroadBandHandler.BroadbandSuccessResponse.class);

    BroadBandHandler.BroadbandSuccessResponse response =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("success", response.response());
    assertEquals("Michigan", response.state());
    assertEquals("Shiawassee County", response.county());
    clientConnection.disconnect();
  }

  @Test
  public void testAPIBroadbandFailure() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband?state=Michigan");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<BroadbandFailureResponse> adapter =
        moshi.adapter(BroadBandHandler.BroadbandFailureResponse.class);

    BroadBandHandler.BroadbandFailureResponse response =
        adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error", response.response());
    assertEquals("Missing state or county parameter", response.message());
    clientConnection.disconnect();
  }

//  @Test
//  public void testAPICSVAndBroadBand() throws IOException {
//    HttpURLConnection clientConnection = tryRequest("load?filePath=data/census/income_by_race.csv");
//    assertEquals(200, clientConnection.getResponseCode());
//
//    Moshi moshi = new Moshi.Builder().build();
//    JsonAdapter<BroadbandSuccessResponse> adapter =
//        moshi.adapter(BroadBandHandler.BroadbandSuccessResponse.class);
//
//
//    BroadbandSuccessResponse response1 = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
//    assertNotNull(response1);
//
//
//    clientConnection.disconnect();
//
//    HttpURLConnection clientConnection2 = tryRequest("broadband?state=Michigan&county=Shiawassee%20County");
//    assertEquals(200, clientConnection2.getResponseCode());
//
//    BroadbandSuccessResponse response2 = adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));
//    assertNotNull(response2); // Make sure the response is not null
//
//
//    assertEquals("success", response2.response());
//    assertEquals("Michigan", response2.state());
//    assertEquals("Shiawassee County", response2.county());
//
//
//    clientConnection2.disconnect();
//  }



}

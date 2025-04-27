package Server;

import CSV.CSVUtilities;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchHandler implements Route {

  /**
   * * Handles HTTP requests to search for specific data in a loaded CSV file. *
   *
   * @param request The HTTP request containing query parameters: - "filename": The name of the file
   *     to search within. - "query": The string to search for. - "column" (optional): The column to
   *     search in (can be null to search all columns).
   * @param response The HTTP response object to be returned.
   * @return A JSON-formatted success or failure response serialized as a string.
   * @throws Exception If any error occurs during serialization or processing.
   */
  @Override
  public Object handle(Request request, Response response) {

    String stringToSearchFor = request.queryParams("query");
    String columnIdentifier = request.queryParams("column");

    Map<String, Object> jsonResponse = new HashMap<>();

    if (stringToSearchFor == null || stringToSearchFor.trim().isEmpty()) {
      jsonResponse.put("response", "error_bad_request");
      jsonResponse.put("message", "Missing query parameter");
      return new SearchFailureResponse(jsonResponse).serialize();
    }

    try {
      List<List<String>> data = CSVUtilities.getLoadedData();

      if (data == null) {
        jsonResponse.put("response", "error_datasource");
        jsonResponse.put("message", "There is no loaded data to search");
        return new SearchFailureResponse(jsonResponse).serialize();
      }

      // Perform search by column identifier or search through all columns
      List<List<String>> searchResults =
          CSVUtilities.searchCSV(stringToSearchFor, columnIdentifier);

      // Construct the response including the additional fields for debugging
      jsonResponse.put("response", "success");
      jsonResponse.put("data", searchResults);
      jsonResponse.put("filename", CSVUtilities.getLoadedFilePath());
      jsonResponse.put("stringToSearchFor", stringToSearchFor);
      jsonResponse.put("columnIdentifier", columnIdentifier);

    } catch (IllegalArgumentException e) {
      jsonResponse.put("response", "error_bad_request");
      jsonResponse.put("message", "Error: " + e.getMessage());
      return new SearchFailureResponse(jsonResponse).serialize();
    } catch (Exception e) {
      jsonResponse.put("response", "error_datasource");
      jsonResponse.put("message", "An unexpected error occurred: " + e.getMessage());
      return new SearchFailureResponse(jsonResponse).serialize();
    }

    // Instead of passing jsonResponse directly, instantiate SearchSuccessResponse with populated
    // map
    return new SearchSuccessResponse(jsonResponse).serialize();
  }

  /**
   * * Represents a successful response for a search request. *
   *
   * @param responseMap A map containing: - "response": Indicates success ("success"). - "data": The
   *     search results as a list of lists. - "filename": The name of the file searched. -
   *     "stringToSearchFor": The query string that was searched for. - "columnIdentifier": The
   *     column that was searched (or null if all columns were searched).
   */
  public record SearchSuccessResponse(Map<String, Object> responseMap) {
    // Constructor initializes responseMap here
    public SearchSuccessResponse(Map<String, Object> responseMap) {
      this.responseMap = responseMap;
    }

    /**
     * * Serializes the success response into a JSON string. *
     *
     * @return The serialized JSON string representation of the success response.
     * @throws Exception If an error occurs during JSON serialization.
     */
    String serialize() {
      try {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<SearchSuccessResponse> adapter = moshi.adapter(SearchSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

  /**
   * * Represents a failure response for a search request. *
   *
   * @param response The response type, typically "error_bad_request" or "error_datasource".
   * @param message The error message explaining the failure.
   */
  public record SearchFailureResponse(String response, String message) {
    // Constructor to directly initialize fields
    public SearchFailureResponse(Map<String, Object> responseMap) {
      this((String) responseMap.get("response"), (String) responseMap.get("message"));
    }
    /**
     * * Serializes the failure response into a JSON string. *
     *
     * @return The serialized JSON string representation of the failure response.
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(SearchHandler.SearchFailureResponse.class).toJson(this);
    }
  }
}

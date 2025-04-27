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

public class ViewHandler implements Route {

  /**
   * * Handles HTTP requests to view the currently loaded CSV data. *
   *
   * @param request The HTTP request. * @param response The HTTP response object to be returned.
   * @return A JSON-formatted success or failure response serialized as a string.
   * @throws Exception If any error occurs during serialization or data processing.
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> jsonResponse = new HashMap<>();
    try {
      List<List<String>> data = CSVUtilities.getLoadedData();

      if (data == null) {
        jsonResponse.put("response", "error_no_data");
        jsonResponse.put("message", "Error: No data has been loaded");
        return new ViewFailureResponse(jsonResponse).serialize();
      }

      // Clean the data (remove unnecessary double quotes)
      List<List<String>> cleanedData =
          data.stream()
              .map(
                  row ->
                      row.stream()
                          .map(s -> s.replace("\"", "")) // Remove unnecessary double quotes
                          .toList())
              .toList();

      // Return the success response with the cleaned data directly as "data"
      return new ViewSuccessResponse("success", cleanedData).serialize();

    } catch (Exception e) {
      jsonResponse.put("response", "error_bad_request");
      jsonResponse.put("message", "Error: " + e.getMessage());
      return new ViewFailureResponse(jsonResponse).serialize();
    }
  }

  /**
   * * Represents a successful response for viewing data. *
   *
   * @param response The response type, typically "success".
   * @param data The cleaned CSV data as a list of lists (rows and columns).
   */
  public record ViewSuccessResponse(String response, List<List<String>> data) {
    public ViewSuccessResponse(String response, List<List<String>> data) {
      this.response = response;
      this.data = data;
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
        JsonAdapter<ViewSuccessResponse> adapter = moshi.adapter(ViewSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

  /**
   * * Represents a failure response for viewing data. *
   *
   * @param response The response type, typically "error_no_data" or "error_bad_request".
   * @param message The error message explaining the failure.
   */
  public record ViewFailureResponse(String response, String message) {
    public ViewFailureResponse(Map<String, Object> responseMap) {
      this((String) responseMap.get("response"), (String) responseMap.get("message"));
    }

    /**
     * * Serializes the failure response into a JSON string. *
     *
     * @return The serialized JSON string representation of the failure response.
     * @throws Exception If an error occurs during JSON serialization.
     */
    String serialize() {
      try {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ViewFailureResponse> adapter = moshi.adapter(ViewFailureResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }
}

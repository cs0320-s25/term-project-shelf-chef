package Server;

import CSV.CSVUtilities;
import Exceptions.FactoryFailureException;
import Server.BroadBandHandler.BroadbandFailureResponse;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadHandler implements Route {

  /**
   * * Handles HTTP requests to load a CSV file from the specified file path. *
   *
   * @param request The HTTP request containing the "filePath" query parameter.
   * @param response The HTTP response object to be returned.
   * @return A JSON-formatted success or failure response serialized as a string.
   * @throws Exception If any error occurs during serialization or processing.
   */
  @Override
  public Object handle(Request request, Response response) {
    String filePath = request.queryParams("filePath");
    Map<String, Object> jsonResponse = new HashMap<>();

    // Check if filePath is valid
    if (filePath == null || filePath.isEmpty()) {
      jsonResponse.put("response", "error_bad_json");
      jsonResponse.put("message", "Error: file path was not provided");
      return new LoadFailureResponse(jsonResponse).serialize();
    }

    if (!Files.exists(Paths.get(filePath))) {
      jsonResponse.put("response", "error_bad_json");
      jsonResponse.put("message", "Error: file path does not exist");
      return new LoadFailureResponse(jsonResponse).serialize();
    }

    if (!filePath.startsWith("data/")) {
      jsonResponse.put("response", "error_bad_request");
      jsonResponse.put("message", "Error: file is not within the data directory");
      return new LoadFailureResponse(jsonResponse).serialize();
    }

    try {
      // Upload the CSV and set the loaded file path
      CSVUtilities.uploadCSV(filePath);
      CSVUtilities.setLoadedFilePath(filePath);

      // Put success and filepath into the jsonResponse
      jsonResponse.put("result", "success");
      jsonResponse.put("filepath", filePath);

      // Return success response with the jsonResponse data
      return new LoadSuccessResponse("success", filePath).serialize();
    } catch (IOException e) {
      jsonResponse.put("response", "error_bad_request");
      jsonResponse.put("message", "Error: " + e.getMessage());
      return new BroadbandFailureResponse(jsonResponse).serialize();
    } catch (FactoryFailureException e) {
      jsonResponse.put("response", "error_bad_request");
      jsonResponse.put("message", "Error: " + e.getMessage());
      return new BroadbandFailureResponse(jsonResponse).serialize();
    }
  }

  /**
   * * Represents a successful response for the file loading operation. *
   *
   * @param response The response type, typically "success".
   * @param filepath The file path of the successfully loaded file.
   */
  public record LoadSuccessResponse(String response, String filepath) {
    public LoadSuccessResponse(String response, String filepath) {
      this.response = response;
      this.filepath = filepath;
    }
    /**
     * * Serializes the success response into a JSON string. *
     *
     * @return The serialized JSON string representation of the success response.
     */
    public String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(LoadSuccessResponse.class).toJson(this);
    }
  }

  /**
   * * Represents a failure response for the file loading operation. *
   *
   * @param response The response type, typically "error_bad_request" or "error_bad_json".
   * @param message The error message explaining the failure.
   */
  public record LoadFailureResponse(String response, String message) {
    // Constructor to directly initialize fields
    public LoadFailureResponse(Map<String, Object> responseMap) {
      this((String) responseMap.get("response"), (String) responseMap.get("message"));
    }
    /**
     * * Serializes the failure response into a JSON string. *
     *
     * @return The serialized JSON string representation of the failure response.
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(LoadHandler.LoadFailureResponse.class).toJson(this);
    }
  }
}

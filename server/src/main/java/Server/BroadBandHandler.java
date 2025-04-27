package Server;

import JSONParser.DataSource;
import JSONParser.IDataSource;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class BroadBandHandler implements Route {

  private final IDataSource dataSource;

  public BroadBandHandler(IDataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * * Handles HTTP requests to retrieve broadband data. *
   *
   * @param request The HTTP request containing query parameters for "state" and "county".
   * @param response The HTTP response object to be returned.
   * @return A JSON-formatted success or failure response serialized as a string.
   * @throws Exception If any error occurs during serialization or processing.
   */
  @Override
  public Object handle(Request request, Response response) {
    String state = request.queryParams("state");
    String county = request.queryParams("county");

    Map<String, Object> jsonResponse = new HashMap<>();

    if (state == null || state.isEmpty() || county == null || county.isEmpty()) {
      // If state or county is missing, return error response
      jsonResponse.put("response", "error");
      jsonResponse.put("message", "Missing state or county parameter");
      return new BroadbandFailureResponse(jsonResponse).serialize();
    }

    try {
      // Get state code
      String stateFIPS = this.dataSource.getStateCode(state);
      System.out.println(stateFIPS);
      if (stateFIPS == null) {
        throw new IllegalArgumentException("Invalid state name: " + state);
      }

      // Get county code
      String countyFIPS = this.dataSource.getCountyCode(stateFIPS, county, state);
      System.out.println(county);
      System.out.println(countyFIPS);
      if (countyFIPS == null) {
        throw new IllegalArgumentException("Invalid county name: " + county);
      }

      // Get broadband percentage
      String cacheKey = "State: " + stateFIPS + " County: " + countyFIPS;
      String broadbandPercentage = APIServer.acsDataCache.get(cacheKey);
      if (broadbandPercentage == null) {
        broadbandPercentage = this.dataSource.getBroadbandPercentage(stateFIPS, countyFIPS);
        APIServer.acsDataCache.put(cacheKey, broadbandPercentage);
        if (broadbandPercentage == null) {
          throw new IllegalArgumentException("Couldn't find data for: " + county + ", " + state);
        }
        // APIServer.acsDataCache.put(cacheKey, broadbandPercentage);
      }

      String dateTime = this.dataSource.getCurrentTime();
      jsonResponse.put("response", "success");
      jsonResponse.put("state", state);
      jsonResponse.put("county", county);
      jsonResponse.put("broadband percentage", broadbandPercentage);
      jsonResponse.put("dateTime", dateTime);

      // In case of an error, populate response with error details
    } catch (IllegalArgumentException e) {
      jsonResponse.put("response", "error_bad_request");
      jsonResponse.put("message", "Error: " + e.getMessage());
      return new BroadbandFailureResponse(jsonResponse).serialize();
    } catch (IndexOutOfBoundsException e) {
      jsonResponse.put("response", "error_bad_request");
      jsonResponse.put("message", "Error: " + e.getMessage());
      return new BroadbandFailureResponse(jsonResponse).serialize();
    } catch (IOException e) {
      jsonResponse.put("response", "error_bad_request");
      jsonResponse.put("message", "Error: " + e.getMessage());
      return new BroadbandFailureResponse(jsonResponse).serialize();
    } catch (InterruptedException e) {
      jsonResponse.put("response", "error_bad_request");
      jsonResponse.put("message", "Error: " + e.getMessage());
      return new BroadbandFailureResponse(jsonResponse).serialize();
    } catch (URISyntaxException e) {
      jsonResponse.put("response", "error_bad_request");
      jsonResponse.put("message", "Error: " + e.getMessage());
      return new BroadbandFailureResponse(jsonResponse).serialize();
    }

    // Return successful response (without nested responseMap)
    return new BroadbandSuccessResponse(
            (String) jsonResponse.get("response"),
            (String) jsonResponse.get("state"),
            (String) jsonResponse.get("county"),
            (String) jsonResponse.get("broadband percentage"),
            (String) jsonResponse.get("dateTime"))
        .serialize();
  }

  /**
   * * Represents a successful broadband response. *
   *
   * @param response The response type, typically "success".
   * @param state The state for which broadband data was requested.
   * @param county The county for which broadband data was requested.
   * @param broadband_percentage The broadband percentage for the specified county and state.
   * @param dateTime The current date and time when the response was generated.
   */
  public record BroadbandSuccessResponse(
      String response, String state, String county, String broadband_percentage, String dateTime) {
    /**
     * * Serializes the success response into a JSON string. *
     *
     * @return The serialized JSON string representation of the success response.
     * @throws Exception If an error occurs during JSON serialization.
     */
    String serialize() {
      try {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<BroadbandSuccessResponse> adapter =
            moshi.adapter(BroadbandSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

  /**
   * * Represents a failure broadband response. *
   *
   * @param response The response type, typically "error_bad_request" or "error_datasource".
   * @param message The error message explaining the failure.
   */
  public record BroadbandFailureResponse(String response, String message) {
    // Constructor to directly initialize fields
    public BroadbandFailureResponse(Map<String, Object> responseMap) {
      this((String) responseMap.get("response"), (String) responseMap.get("message"));
    }

    /**
     * * Serializes the failure response into a JSON string. *
     *
     * @return The serialized JSON string representation of the failure response.
     * @throws Exception If an error occurs during JSON serialization.
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(BroadbandFailureResponse.class).toJson(this);
    }
  }
}

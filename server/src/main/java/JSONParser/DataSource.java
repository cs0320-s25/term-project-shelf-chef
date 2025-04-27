package JSONParser;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class DataSource implements IDataSource{

  private static List<List<String>> data;

  private static final HttpClient httpClient = HttpClient.newHttpClient();
  private static final Moshi moshi = new Moshi.Builder().build();

  /**
   * Fetches the FIPS code for a given state name.
   *
   * @param stateName The name of the state (e.g., "California").
   * @return The FIPS code for the state, or null if not found.
   * @throws IOException If an I/O error occurs.
   * @throws InterruptedException If the request is interrupted.
   * @throws URISyntaxException If the URI is invalid.
   */
  public String getStateCode(String stateName)
      throws IOException, InterruptedException, URISyntaxException {
    String url = "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*";
    System.out.println(url);

    HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    // Parse JSON response using Moshi
    Type type = Types.newParameterizedType(List.class, List.class, String.class);
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(type);
    List<List<String>> jsonArray = adapter.fromJson(response.body());

    // Skip the header row and search for the state name
    for (int i = 1; i < jsonArray.size(); i++) {
      List<String> row = jsonArray.get(i);
      String name = row.get(0);
      String fips = row.get(1);
      if (name.equalsIgnoreCase(stateName)) {
        return fips; // Return state FIPS code
      }
    }
    return null; // State not found
  }

  /**
   * Fetches the FIPS code for a county within a given state.
   *
   * @param stateCode The FIPS code for the state.
   * @param countyName The name of the county (e.g., "Kings County").
   * @return The FIPS code for the county, or null if not found.
   * @throws IOException If an I/O error occurs.
   * @throws InterruptedException If the request is interrupted.
   * @throws URISyntaxException If the URI is invalid.
   */
  public String getCountyCode(String stateCode, String countyName, String state)
      throws IOException, InterruptedException, URISyntaxException {
    String url =
        "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:" + stateCode;

    System.out.println(url);
    HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    // Parse JSON response using Moshi
    Type type = Types.newParameterizedType(List.class, List.class, String.class);
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(type);
    List<List<String>> jsonArray = adapter.fromJson(response.body());
    // Skip the header row and search for the county name
    for (int i = 1; i < jsonArray.size(); i++) {
      List<String> row = jsonArray.get(i);
      String name = row.get(0);
      String fips = row.get(2); // County FIPS code is in the 3rd column
      if (name.equalsIgnoreCase(countyName + ", " + state)) {
        return fips; // Return county FIPS code
      }
    }
    return null;
  }

  /**
   * Fetches the broadband percentage for a given state and county using the ACS dataset.
   *
   * @param stateCode The FIPS code for the state.
   * @param countyCode The FIPS code for the county.
   * @return The broadband percentage as a String, or "No data available" if not found.
   * @throws IOException If an I/O error occurs.
   * @throws InterruptedException If the request is interrupted.
   * @throws URISyntaxException If the URI is invalid.
   */
  public String getBroadbandPercentage(String stateCode, String countyCode)
      throws IOException, InterruptedException, URISyntaxException {
    String url =
        "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
            + countyCode
            + "&in=state:"
            + stateCode;
    HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    // Parse JSON response using Moshi
    Type type = Types.newParameterizedType(List.class, List.class, String.class);
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(type);
    List<List<String>> jsonArray = adapter.fromJson(response.body());

    // Check if there is data
    if (jsonArray.size() > 1) {
      List<String> dataRow = jsonArray.get(1);
      return dataRow.get(1); // Broadband percentage is in the first column
    }
    return "No data available"; // No data found
  }

  /**
   * * Gets the current system time as a formatted string. *
   *
   * @return The current system time in the default date and time format.
   */
  public String getCurrentTime() {
    java.util.Date now = new java.util.Date();
    return now.toString();
  }
}

package JSONParser;

import java.io.IOException;
import java.net.URISyntaxException;

public interface IDataSource {
  String getStateCode(String stateName) throws IOException, InterruptedException, URISyntaxException;
  String getCountyCode(String stateCode, String countyName, String state) throws IOException, InterruptedException, URISyntaxException;
  String getBroadbandPercentage(String stateCode, String countyCode) throws IOException, InterruptedException, URISyntaxException;
  String getCurrentTime();

}

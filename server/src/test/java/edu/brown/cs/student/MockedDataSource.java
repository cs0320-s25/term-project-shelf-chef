package edu.brown.cs.student;

import JSONParser.IDataSource;
import java.util.HashMap;
import java.util.Map;

public class MockedDataSource implements IDataSource {
  private final Map<String, String> stateCodes;
  private final Map<String, String> countyCodes;
  private final Map<String, String> broadbandData;

  public MockedDataSource(Map<String, String> stateCodes, Map<String, String> countyCodes,
      Map<String, String> broadbandData) {
    this.stateCodes = stateCodes;
    this.countyCodes = countyCodes;
    this.broadbandData = broadbandData;

    // Mock data
    stateCodes.put("Michigan", "26");
    countyCodes.put("Shiawassee County", "155");
    broadbandData.put("26-155", "85.3");
    stateCodes.put("California", "06");
    countyCodes.put("Los Angeles County", "91");
    broadbandData.put("06-91", "86.3");
    stateCodes.put("Connecticut", "16");
    countyCodes.put("Middlesex County", "90");
    broadbandData.put("16-90", "100");
  }

  @Override
  public String getStateCode(String stateName) {
    return stateCodes.get(stateName);
  }

  @Override
  public String getCountyCode(String stateCode, String countyName, String state) {
    return countyCodes.get(countyName);
  }

  @Override
  public String getBroadbandPercentage(String stateCode, String countyCode) {
    return broadbandData.getOrDefault(stateCode + "-" + countyCode, "No data available");
  }

  @Override
  public String getCurrentTime() {
    return "Mocked Time: 2025-02-21T12:00:00Z";
  }

}

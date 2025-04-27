package edu.brown.cs.student;

import java.util.HashMap;
import java.util.Map;
import spark.Request;

public class MockRequest extends Request {

  private Map<String, String> queryParams = new HashMap<>();

  public MockRequest(Map<String, String> queryParams) {
    this.queryParams = queryParams;
  }

  public String queryParams(String key) {
    return queryParams.get(key);
  }
}

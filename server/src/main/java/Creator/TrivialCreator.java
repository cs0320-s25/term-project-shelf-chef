package Creator;

import Exceptions.FactoryFailureException;
import java.util.List;

public class TrivialCreator implements CreatorFromRow<List<String>> {

  /**
   * @param row list of string from the line that is read from the CSV
   */
  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    return row;
  }
}

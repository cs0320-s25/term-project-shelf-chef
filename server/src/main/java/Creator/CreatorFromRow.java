package Creator;

import Exceptions.FactoryFailureException;
import java.util.List;

public interface CreatorFromRow<T> {

  /**
   * @param row list of string from the line that is read from the CSV
   */
  T create(List<String> row) throws FactoryFailureException;
}

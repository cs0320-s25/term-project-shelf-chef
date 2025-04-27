package Creator;

import CreatedObjects.Star;
import Exceptions.FactoryFailureException;
import java.util.List;

public class StarCreator implements CreatorFromRow<Star> {

  /**
   * @param row list of string from the line that is read from the CSV
   */
  @Override
  public Star create(List<String> row) throws FactoryFailureException {
    if (row.size() != 5) {
      throw new FactoryFailureException("Row does not have 5 columns", row);
    }

    String ID = row.get(0);
    String name = row.get(1);
    String X = row.get(2);
    String Y = row.get(3);
    String Z = row.get(4);

    return new Star(ID, name, X, Y, Z);
  }
}

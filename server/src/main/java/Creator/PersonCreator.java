package Creator;

import CreatedObjects.Person;
import Exceptions.FactoryFailureException;
import java.util.List;

public class PersonCreator implements CreatorFromRow<Person> {

  /**
   * @param row list of string from the line that is read from the CSV
   */
  @Override
  public Person create(List<String> row) throws FactoryFailureException {
    if (row.size() != 3) {
      throw new FactoryFailureException("Row does not have 3 columns", row);
    }

    String name = row.get(0);
    String age = row.get(1);
    String city = row.get(2);

    return new Person(name, age, city);
  }
}

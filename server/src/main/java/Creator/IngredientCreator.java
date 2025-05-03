package Creator;

import CreatedObjects.Ingredient;
import Exceptions.FactoryFailureException;
import java.util.List;

public class IngredientCreator implements CreatorFromRow<Ingredient> {

  /**
   * @param row list of strings from the line that is read from the CSV
   */
  @Override
  public Ingredient create(List<String> row) throws FactoryFailureException {
    if (row.size() != 3) {
      throw new FactoryFailureException("Row does not have 3 columns", row);
    }

    String name = row.get(0);
    String quantity = row.get(1);
    String expiration = row.get(2);

    return new Ingredient(name, quantity, expiration);
  }
}

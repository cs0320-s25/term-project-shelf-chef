package Creator;

import java.util.List;

import CreatedObjects.Ingredient;
import Exceptions.FactoryFailureException;

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

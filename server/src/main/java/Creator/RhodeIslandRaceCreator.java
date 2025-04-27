package Creator;

import CreatedObjects.RhodeIslandRace;
import Exceptions.FactoryFailureException;
import java.util.List;

public class RhodeIslandRaceCreator implements CreatorFromRow<RhodeIslandRace> {

  /**
   * @param row list of string from the line that is read from the CSV
   */
  @Override
  public RhodeIslandRace create(List<String> row) throws FactoryFailureException {
    if (row.size() != 6) {
      throw new FactoryFailureException("Row does not have 6 columns", row);
    }

    String state = row.get(0);
    String dataType = row.get(1);
    String avgWeeklyEarnings = row.get(2);
    String numWorkers = row.get(3);
    String earningDispar = row.get(4);
    String employeedPercent = row.get(5);

    return new RhodeIslandRace(
        state, dataType, avgWeeklyEarnings, numWorkers, earningDispar, employeedPercent);
  }
}

package CreatedObjects;

public class RhodeIslandRace {

  private String state;
  private String dataType;
  private String avgWeeklyEarnings;
  private String numWorkers;
  private String earningDispar;
  private String employeedPercent;

  public RhodeIslandRace(
      String state,
      String dataType,
      String avgWeeklyEarnings,
      String numWorkers,
      String earningDispar,
      String employeedPercent) {
    this.state = state;
    this.dataType = dataType;
    this.avgWeeklyEarnings = avgWeeklyEarnings;
    this.numWorkers = numWorkers;
    this.earningDispar = earningDispar;
    this.employeedPercent = employeedPercent;
  }

  public String getState() {
    return state;
  }

  public String getDataType() {
    return dataType;
  }

  public String getAvgWeeklyEarnings() {
    return avgWeeklyEarnings;
  }

  public String getNumWorkers() {
    return numWorkers;
  }

  public String getEarningDispar() {
    return earningDispar;
  }

  public String getEmployeedPercent() {
    return employeedPercent;
  }
}

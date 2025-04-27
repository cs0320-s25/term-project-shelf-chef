package Exceptions;

import java.util.ArrayList;
import java.util.List;

public class FactoryFailureException extends Exception {
  final List<String> row;

  /**
   * @param message Message to be shown to user when error is thrown
   * @param row row that the error is associated with
   */
  public FactoryFailureException(String message, List<String> row) {
    super(message);
    this.row = new ArrayList<>(row);
  }
}

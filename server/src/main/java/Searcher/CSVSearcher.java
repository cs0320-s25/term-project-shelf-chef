package Searcher;

import java.util.ArrayList;
import java.util.List;

public class CSVSearcher {
  private List<List<String>> data;
  private List<String> headers;

  public CSVSearcher(List<List<String>> data, List<String> headers) {
    this.data = data;
    this.headers = headers;
  }

  /**
   * Searches the data for the inputted value in the specified column and returns the list of rows
   * with the value as a list of strings
   *
   * @param columnIdentifier column identifier user is trying to look for
   * @param value value user is searching for
   */
  public List<List<String>> search(String value, String columnIdentifier) {

    List<List<String>> results = new ArrayList<>();

    int columnIndex = getColumnIndex(columnIdentifier);

    if (columnIndex == -1) {
      throw new IllegalArgumentException(
          "ERROR: Column identifier '" + columnIdentifier + "' does not exist.");
    }

    int rowSize = 0; // keeping track of max row size to check if an index exceeds it
    for (List<String> row : data) {
      if (row.size() > rowSize) {
        rowSize = row.size();
      }

      if (row == null || row.isEmpty()) {
        continue;
      }

      if (columnIndex >= 0 && (columnIndex >= rowSize)) {
        throw new IndexOutOfBoundsException(
            "ERROR: Column index " + columnIndex + " is out of range.");
      }

      if (columnIndex >= 0 && columnIndex < row.size()) {
        if (cleanValue(row.get(columnIndex)).equalsIgnoreCase(value)) {
          results.add(row);
        }
      } else if (columnIndex == -2) {
        for (String cell : row) {
          if (cleanValue(cell).equalsIgnoreCase(value)) {
            results.add(row);
            break;
          }
        }
      }
    }
    return results;
  }

  /**
   * Determines the column index based on the column identifier.
   *
   * @param columnIdentifier the column identifier (name or index)
   * @return the column index, -2 for all columns, or -1 if invalid
   */
  private int getColumnIndex(String columnIdentifier) {
    if (columnIdentifier == null) {
      return -2;
    }

    try {
      int ind = Integer.parseInt(columnIdentifier);
      if (ind < 0) { // Explicitly handle negative indices
        throw new IllegalArgumentException(
            "ERROR: Column index cannot be negative. Provided: " + columnIdentifier);
      }
      return ind;
    } catch (NumberFormatException e) {
      if (headers != null) {
        for (int i = 0; i < headers.size(); i++) {
          if (headers.get(i).equalsIgnoreCase(columnIdentifier)) {
            return i;
          }
        }
      }
    }

    return -1;
  }

  /**
   * cleans and normalizes a string by trimming spaces and removing surrounding quotes.
   *
   * @param input the string to clean
   * @return the cleaned string
   */
  private String cleanValue(String input) {
    if (input == null) {
      return "";
    }
    return input.trim().replaceAll("^\"|\"$", "");
  }
}

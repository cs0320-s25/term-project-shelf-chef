package CSV;

import Creator.TrivialCreator;
import Exceptions.FactoryFailureException;
import Parser.Parser;
import Searcher.CSVSearcher;
import java.io.*;
import java.util.List;

public class CSVUtilities {

  private static List<List<String>> loadedData;
  private static Parser parser;
  private static String loadedFilePath;

  /**
   * * Constructor for CSVUtilities. Initializes the class with no loaded data, no parser, and no
   * file path.
   */
  public CSVUtilities() {
    this.loadedData = null;
    this.parser = null;
    this.loadedFilePath = null;
  }

  /**
   * * Uploads and parses a CSV file from the specified file path. * @param filePath The path to the
   * CSV file to upload. * @throws IOException If there is an error reading the file. * @throws
   * FactoryFailureException If the parser's factory creation fails.
   */
  public static void uploadCSV(String filePath) throws IOException, FactoryFailureException {
    FileReader fileReader = new FileReader(filePath);
    TrivialCreator trivialCreator = new TrivialCreator();
    Parser parser = new Parser(fileReader, trivialCreator);
    parser.parse(true);
    setParser(parser);
    setCSVData(parser.parsedContent);
  }
  /**
   * * Searches the currently loaded CSV data for rows matching the specified value and column
   * identifier. * @param value The value to search for. * @param columnIdentifier The column name
   * or index to search within. * @return A list of rows containing the matching value.
   */
  public static List<List<String>> searchCSV(String value, String columnIdentifier) {
    CSVSearcher searcher = new CSVSearcher(loadedData, parser.getHeaders());
    return searcher.search(value, columnIdentifier);
  }

  /**
   * * Retrieves the currently loaded and parsed CSV data. * * @return The loaded CSV data as a 2D
   * list.
   */
  public static List<List<String>> getLoadedData() {
    return loadedData;
  }

  /** * Sets the loaded CSV data. * * @param data The CSV data to be stored as a 2D list. */
  public static void setCSVData(List<List<String>> data) {
    loadedData = data;
  }

  /**
   * * Sets the parser instance to the provided parser. * * @param newParser The parser object to
   * set.
   */
  public static void setParser(Parser newParser) {
    parser = newParser;
  }

  /**
   * * Retrieves the file path of the currently loaded CSV file. * * @return The file path of the
   * loaded CSV file as a string.
   */
  public static String getLoadedFilePath() {
    return loadedFilePath;
  }

  /**
   * * Sets the file path of the currently loaded CSV file. * * @param filePath The file path to
   * set.
   */
  public static void setLoadedFilePath(String filePath) {
    loadedFilePath = filePath;
  }
}

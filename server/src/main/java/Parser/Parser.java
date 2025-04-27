package Parser;

import Creator.CreatorFromRow;
import Exceptions.FactoryFailureException;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class Parser<T> {
  /** TODO is this defensive enough? Feel free to edit any variable declarations. */
  public BufferedReader reader;

  public CreatorFromRow<T> creator;
  public List<T> parsedContent;
  private final List<List<String>> rawData; // where we will store the csvData to search later that
  private List<List<String>> data;
  private List<String> headers;

  /**
   * TODO feel free to modify the header and body of this constructor however you wish.
   *
   * @param reader - reader object to be parsed
   * @param creator - creator to turn list of row into specified object
   */
  public Parser(Reader reader, CreatorFromRow<T> creator) throws IOException {
    if (reader == null) {
      throw new IllegalArgumentException("Reader cannot be null");
    }
    if (creator == null) {
      throw new IllegalArgumentException("Creator cannot be null");
    }

    this.reader = new BufferedReader(reader);
    this.parsedContent = new ArrayList<>();
    this.creator = creator;
    this.rawData = new ArrayList<>();
    this.headers = new ArrayList<>();
  }

  /**
   * TODO feel free to modify this method to incorporate your design choices.
   *
   * @throws IOException when buffer reader fails to read-in a line
   * @throws FactoryFailureException when csv and creator object don't align
   */
  public void parse(Boolean header) throws IOException, FactoryFailureException {
    String line;
    Pattern regexSplitCSVRow =
        Pattern.compile("\s*,\s*(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");

    BufferedReader readInBuffer = this.reader; // wraps around readers to improve efficiency when reading

    if (header) {
      // Read the first line, store it in headers, and also process it as data
      String head = readInBuffer.readLine();
      String[] columns = regexSplitCSVRow.split(head);
      this.headers = List.of(columns);

      // Process the header row like any other row
      List<String> lineToArr = List.of(columns);
      rawData.add(lineToArr);

      T obj = creator.create(lineToArr);
      parsedContent.add(obj);
    }

    while ((line = readInBuffer.readLine()) != null) {
      String[] result = regexSplitCSVRow.split(line);
      List<String> lineToArr = Arrays.stream(result).toList();
      rawData.add(lineToArr);

      // Convert the row into an object of type T
      T obj = creator.create(lineToArr);
      parsedContent.add(obj);
    }

    readInBuffer.close();
  }

  public List<List<String>> getRawData() {
    if (data == null) {
      data = Collections.unmodifiableList(this.rawData);
    }
    return data;
  }

  public List<String> getHeaders() {
    if (headers == null) {
      headers = Collections.unmodifiableList(this.headers);
    }
    return headers;
  }
}

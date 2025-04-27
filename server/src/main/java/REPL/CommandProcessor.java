package REPL;

import Creator.TrivialCreator;
import Exceptions.FactoryFailureException;
import Parser.Parser;
import Searcher.CSVSearcher;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;

public class CommandProcessor {
  private final InputStream in;
  private final PrintStream out;
  private final PrintStream err;

  private Parser<?> parser;
  private CSVSearcher searcher;
  private List<List<String>> csvData;
  private List<String> headers;

  /**
   * Create a CommandProcessor that listens for input, sends output, and sends errors to the given
   * streams.
   *
   * @param in the input stream to use
   * @param out the output stream to use
   * @param err the error stream to use
   */
  public CommandProcessor(InputStream in, PrintStream out, PrintStream err) {
    this.in = in;
    this.out = out;
    this.err = err;
  }

  /**
   * Create a CommandProcessor using the standard System-defined input, output, and error streams.
   */
  public CommandProcessor() {
    this(System.in, System.out, System.err);
  }

  /** Start listening for input, and don't stop until you see "exit". */
  public void run() {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(this.in))) {
      String input;
      this.out.println("Welcome to CSV Search!");
      this.out.println(
          "Available commands are: LOAD <file>, SEARCH <column> <value>, or EXIT, USE ':' to separate commands and arguments");

      while (true) {
        input = br.readLine();
        String[] parts = input.split(":", 3);
        String command = parts[0].toUpperCase();

        if (input.equalsIgnoreCase("EXIT")) {
          this.out.println("Exiting...");
          return;
        }

        switch (command) {
          case "LOAD":
            if (parts.length == 2) {
              loadCSV(parts[1]);
            } else {
              this.err.println("ERROR: Usage: LOAD <file_path>");
            }
            break;
          case "SEARCH":
            if (parts.length == 2) {
              searchCSV(null, parts[1]);
            } else if (parts.length == 3) {
              searchCSV(parts[1], parts[2]);
            } else {
              this.err.println("ERROR: Usage: SEARCH <column_name> <value>");
            }
            break;
          case "EXIT":
            this.out.println("Exiting...");
            break;
          default:
            this.err.println("ERROR: Invalid command.");
        }
      }
    } catch (IOException ex) {
      this.err.println("ERROR: Error reading input.");
    }
  }

  /**
   * Loads a CSV file using `Parser` and initializes `CSVSearcher`.
   *
   * @param filepath the filepath to the file the user wants to search
   */
  public void loadCSV(String filepath) {
    if (!filepath.startsWith("data/")) {
      this.err.println("ERROR: File provided is not within the data directory");
    }
    try (FileReader fileReader = new FileReader(filepath); ) {
      TrivialCreator creator = new TrivialCreator();

      // creating the parser, parsing, and loading the data
      this.parser = new Parser<>(fileReader, creator);
      this.parser.parse(true);

      this.csvData = parser.getRawData();
      this.headers = parser.getHeaders();

      this.searcher = new CSVSearcher(csvData, headers);

      this.out.println("CSV file loaded successfully: " + filepath);
    } catch (IOException e) {
      this.err.println("ERROR: File not found or cannot be read.");
    } catch (FactoryFailureException e) {
      this.err.println("ERROR: Failed to create rows from CSV.");
    }
  }

  /**
   * Searches the loaded CSV for the inputted value and stores the results in this.results
   *
   * @param identifier column identifier user is trying to look for
   * @param value value user is searching for
   */
  public void searchCSV(String identifier, String value) {
    try {
      if (this.searcher == null) {
        this.err.println("No CSV file loaded. Use LOAD <file_path> first.");
      }

      List<List<String>> results = searcher.search(value, identifier);

      if (results.isEmpty()) {
        this.out.println("No matching results found.");
      } else {
        this.out.println("Search results:");
        for (List<String> row : results) {
          this.out.println(String.join(", ", row));
        }
      }
    } catch (NullPointerException e) {
      this.err.println(e);
    } catch (IndexOutOfBoundsException e) {
      this.err.println(e);
    }
  }

  /** This is the entry point for the command-line application. */
  public static void main(String[] args) {
    CommandProcessor proc = new CommandProcessor();
    proc.run();
  }
}

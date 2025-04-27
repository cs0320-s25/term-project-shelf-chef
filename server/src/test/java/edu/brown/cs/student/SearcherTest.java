package edu.brown.cs.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import Searcher.CSVSearcher;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SearcherTest {

  @Test
  public void testSearchWithNullValue() {
    List<List<String>> data =
        Arrays.asList(
            Arrays.asList("Alice", "25", "Engineer"),
            Arrays.asList("Bob", null, "Doctor"),
            Arrays.asList("Claire", "25", "Artist"));

    List<String> headers = Arrays.asList("Name", "Age", "Profession");
    CSVSearcher searcher = new CSVSearcher(data, headers);
    List<List<String>> results = searcher.search("", "Age");

    Assertions.assertEquals(1, results.size());
    Assertions.assertEquals(Arrays.asList("Bob", null, "Doctor"), results.get(0));
  }

  @Test
  public void testSearchWithOutOfRangeColumnIndex() {
    List<List<String>> data =
        Arrays.asList(
            Arrays.asList("Alice", "25", "Engineer"),
            Arrays.asList("Bob", "30", "Doctor"),
            Arrays.asList("Claire", "25", "Artist"));

    List<String> headers = Arrays.asList("Name", "Age", "Profession");
    CSVSearcher searcher = new CSVSearcher(data, headers);

    Exception exception =
        assertThrows(IndexOutOfBoundsException.class, () -> searcher.search("25", "5"));

    assertEquals("ERROR: Column index 5 is out of range.", exception.getMessage());
  }

  @Test
  public void testSearchWithMissingHeaders() {
    List<List<String>> data =
        Arrays.asList(
            Arrays.asList("Alice", "25", "Engineer"),
            Arrays.asList("Bob", "30", "Doctor"),
            Arrays.asList("Claire", "25", "Artist"));

    CSVSearcher searcher = new CSVSearcher(data, null);
    List<List<String>> results = searcher.search("25", "1");

    Assertions.assertEquals(2, results.size());
    Assertions.assertEquals(Arrays.asList("Alice", "25", "Engineer"), results.get(0));
    Assertions.assertEquals(Arrays.asList("Claire", "25", "Artist"), results.get(1));
  }

  @Test
  public void testSearchByIdentifier() {
    List<List<String>> data =
        Arrays.asList(
            Arrays.asList("Alice", "25", "Engineer"),
            Arrays.asList("Bob", "30", "Doctor"),
            Arrays.asList("Claire", "25", "Artist"));

    List<String> headers = Arrays.asList("Name", "Age", "Profession");
    CSVSearcher searcher = new CSVSearcher(data, headers);
    List<List<String>> results = searcher.search("25", "Age");

    Assertions.assertEquals(2, results.size());
    Assertions.assertEquals(Arrays.asList("Alice", "25", "Engineer"), results.get(0));
    Assertions.assertEquals(Arrays.asList("Claire", "25", "Artist"), results.get(1));
  }

  @Test
  public void testSearchByColumnIndex() {
    List<List<String>> data =
        Arrays.asList(
            Arrays.asList("Alice", "25", "Engineer"),
            Arrays.asList("Bob", "30", "Doctor"),
            Arrays.asList("Claire", "25", "Artist"));
    List<String> headers = Arrays.asList("Name", "Age", "Profession");
    CSVSearcher searcher = new CSVSearcher(data, headers);
    List<List<String>> results = searcher.search("Claire", "0");

    Assertions.assertEquals(1, results.size());
    Assertions.assertEquals(Arrays.asList("Claire", "25", "Artist"), results.get(0));
  }

  @Test
  public void testSearchWithoutColumnIdentifier() {
    List<List<String>> data =
        Arrays.asList(
            Arrays.asList("Alice", "25", "Engineer"),
            Arrays.asList("Bob", "30", "Doctor"),
            Arrays.asList("Claire", "25", "Artist"));

    List<String> headers = Arrays.asList("Name", "Age", "Profession");
    CSVSearcher searcher = new CSVSearcher(data, headers);
    List<List<String>> results = searcher.search("25", null);

    Assertions.assertEquals(2, results.size());
    Assertions.assertEquals(Arrays.asList("Alice", "25", "Engineer"), results.get(0));
    Assertions.assertEquals(Arrays.asList("Claire", "25", "Artist"), results.get(1));
  }

  @Test
  public void testCaseInsensitiveSearch() {
    List<List<String>> data =
        Arrays.asList(
            Arrays.asList("Alice", "25", "Engineer"),
            Arrays.asList("Bob", "30", "Doctor"),
            Arrays.asList("Claire", "25", "Artist"));

    List<String> headers = Arrays.asList("Name", "Age", "Profession");
    CSVSearcher searcher = new CSVSearcher(data, headers);
    List<List<String>> results = searcher.search("ARTIST", "Profession");

    Assertions.assertEquals(1, results.size());
    Assertions.assertEquals(Arrays.asList("Claire", "25", "Artist"), results.get(0));
  }

  @Test
  public void testValueNotPresent() {
    List<List<String>> data =
        Arrays.asList(
            Arrays.asList("Alice", "25", "Engineer"),
            Arrays.asList("Bob", "30", "Doctor"),
            Arrays.asList("Claire", "25", "Artist"));

    List<String> headers = Arrays.asList("Name", "Age", "Profession");
    CSVSearcher searcher = new CSVSearcher(data, headers);
    List<List<String>> results = searcher.search("40", "Age");

    Assertions.assertEquals(0, results.size());
  }

  @Test
  public void testValueInWrongColumn() {
    List<List<String>> data =
        Arrays.asList(
            Arrays.asList("Alice", "25", "Engineer"),
            Arrays.asList("Bob", "30", "Doctor"),
            Arrays.asList("Claire", "25", "Artist"));

    List<String> headers = Arrays.asList("Name", "Age", "Profession");
    CSVSearcher searcher = new CSVSearcher(data, headers);
    List<List<String>> results = searcher.search("Engineer", "Age");

    Assertions.assertEquals(0, results.size());
  }

  @Test
  public void testInconsistentRowSizes() {
    List<List<String>> data =
        Arrays.asList(
            Arrays.asList("Alice", "25", "Engineer"),
            Arrays.asList("Bob", "30"),
            Arrays.asList("Claire", "25", "Artist", "Extra"));

    List<String> headers = Arrays.asList("Name", "Age", "Profession");
    CSVSearcher searcher = new CSVSearcher(data, headers);
    List<List<String>> results = searcher.search("25", "Age");

    Assertions.assertEquals(2, results.size());
    Assertions.assertEquals(Arrays.asList("Alice", "25", "Engineer"), results.get(0));
    Assertions.assertEquals(Arrays.asList("Claire", "25", "Artist", "Extra"), results.get(1));
  }

  @Test
  public void testInvalidRegularExpression() {
    // test that helped me find and fix error in regex
    List<List<String>> data =
        Arrays.asList(
            Arrays.asList("Alice", "25", "Engineer"),
            Arrays.asList("Bob", "30   ", "Doctor"),
            Arrays.asList("Claire", "25", "Artist"));

    List<String> headers = Arrays.asList("Na(me)", "Age", "Profession");
    CSVSearcher searcher = new CSVSearcher(data, headers);

    List<List<String>> results = searcher.search("30", "Age");
    Assertions.assertEquals(1, results.size());
  }
}

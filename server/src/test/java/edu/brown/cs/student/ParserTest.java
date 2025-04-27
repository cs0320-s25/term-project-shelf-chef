package edu.brown.cs.student;

import static org.junit.jupiter.api.Assertions.*;

import CreatedObjects.Person;
import CreatedObjects.RhodeIslandRace;
import CreatedObjects.Star;
import Creator.PersonCreator;
import Creator.RhodeIslandRaceCreator;
import Creator.StarCreator;
import Creator.TrivialCreator;
import Exceptions.FactoryFailureException;
import Parser.Parser;
import java.io.*;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * TODO: add more tests in this file to build an extensive test suite for your parser and parsing
 * functionalities
 *
 * <p>Tests for the parser class
 */
public class ParserTest {
  Parser incomeByRaceParser;
  Parser malformedParser;
  Parser stringCSVParser;
  Parser starParser;
  Parser personParser;
  Parser personParser2;
  Parser rhodeParser;

  TrivialCreator trivialCreator;
  StarCreator starCreator;
  PersonCreator personCreator;
  RhodeIslandRaceCreator rhodeIslandRaceCreator;

  Star star;
  Star testStar;

  Person person;
  Person testPerson;

  RhodeIslandRace rhode;

  RhodeIslandRace testRhode;

  // test parsing uniformed CSV
  @Test
  public void testParseRegCSV() {
    try {
      FileReader fileReader = new FileReader("data/census/income_by_race.csv");
      trivialCreator = new TrivialCreator();

      incomeByRaceParser = new Parser(fileReader, trivialCreator);
      incomeByRaceParser.parse(true);

    } catch (IOException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }
    List<List<String>> parsedContent = incomeByRaceParser.parsedContent;

    assertEquals(323, incomeByRaceParser.parsedContent.size());
    assertEquals(9, parsedContent.get(223).size());
    assertEquals(9, parsedContent.get(0).size());
    assertEquals(
        List.of(
            "7",
            "Two Or More",
            "2017",
            "2017",
            "44000",
            "11831",
            "\"Kent County, RI\"",
            "05000US44003",
            "kent-county-ri"),
        incomeByRaceParser.parsedContent.get(142));
    assertFalse(incomeByRaceParser.parsedContent.contains(List.of("Gemini", "Roberto", "Nick")));
  }

  @Test
  public void testStringReader() {
    try {
      StringReader stringReader =
          new StringReader(
              "name,age,city\nAlice,25,New York\nBob,30,Los Angeles\nCharlie,22,Chicago");
      personCreator = new PersonCreator();
      stringCSVParser = new Parser(stringReader, personCreator);
      stringCSVParser.parse(true);
    } catch (IOException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }

    assertEquals(3, stringCSVParser.parsedContent.size());
  }

  @Test
  public void testPersonCreatorWithoutHeader() {
    try {
      StringReader stringReader =
          new StringReader("Alice,25,New York\nBob,30,Los Angeles\nCharlie,22,Chicago");
      personCreator = new PersonCreator();
      personParser2 = new Parser(stringReader, personCreator);
      personParser2.parse(false);
    } catch (IOException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }

    person = new Person("Alice", "25", "New York");
    testPerson = (Person) personParser2.parsedContent.get(0);

    assertEquals(3, personParser2.parsedContent.size());
    assertEquals(testPerson.getName(), person.getName());
    assertEquals(testPerson.getCity(), person.getCity());
    assertEquals(testPerson.getAge(), person.getAge());
  }

  @Test
  public void testRhodeIslandRaceCreator() {
    // test that helped me find the error in the regex given in the assignment (leading and trailing
    // white spaces)
    try {
      FileReader fileReader = new FileReader("data/census/dol_ri_earnings_disparity.csv");
      rhodeIslandRaceCreator = new RhodeIslandRaceCreator();
      rhodeParser = new Parser(fileReader, rhodeIslandRaceCreator);
      rhodeParser.parse(true);
    } catch (IOException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }

    rhode = new RhodeIslandRace("RI", "Hispanic/Latino", "$673.14", "74596.18851", "$0.64", "14%");
    testRhode = (RhodeIslandRace) rhodeParser.parsedContent.get(4);

    assertEquals(6, rhodeParser.parsedContent.size());
    assertEquals(testRhode.getState(), rhode.getState());
    assertEquals(testRhode.getAvgWeeklyEarnings(), rhode.getAvgWeeklyEarnings());
    assertEquals(testRhode.getDataType(), rhode.getDataType());
    assertEquals(testRhode.getEarningDispar(), rhode.getEarningDispar());
    assertEquals(testRhode.getNumWorkers(), rhode.getNumWorkers());
    assertEquals(testRhode.getEmployeedPercent(), rhode.getEmployeedPercent());
  }

  // test parsing malformed data
  @Test
  public void testParseMalformedCSV() {
    try {
      FileReader fileReader = new FileReader("data/malformed/malformed_signs.csv");
      trivialCreator = new TrivialCreator();

      malformedParser = new Parser(fileReader, trivialCreator);
      malformedParser.parse(true);
    } catch (IOException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }
    List<List<String>> parsedContent = malformedParser.parsedContent;

    assertEquals(12, malformedParser.parsedContent.size());
    assertEquals(2, parsedContent.get(0).size());
    assertEquals(List.of("Aquarius"), malformedParser.parsedContent.get(10));
    assertEquals(List.of("Gemini", "Roberto", "Nick"), malformedParser.parsedContent.get(2));
  }

  // test parser for a file not found, example for exception testing
  @Test
  public void testFileNotFoundParse() throws IOException {
    trivialCreator = new TrivialCreator();
    Exception exception =
        assertThrows(
            FileNotFoundException.class,
            () -> new Parser(new FileReader("data/census/housing.csv"), trivialCreator));
  }

  @Test
  public void testFactoryFailureException() throws IOException {
    personCreator = new PersonCreator();
    personParser =
        new Parser(
            new StringReader(
                "name,age,city\nAlice,25,New York,extra,extra\nBob,30,Los Angeles\nCharlie,22,Chicago"),
            personCreator);
    Exception exception =
        assertThrows(FactoryFailureException.class, () -> personParser.parse(true));

    assertEquals("Row does not have 3 columns", exception.getMessage());
  }

  @Test
  public void testStarCreator() {
    try {
      FileReader fileReader = new FileReader("data/stars/stardata.csv");
      starCreator = new StarCreator();

      starParser = new Parser(fileReader, starCreator);
      starParser.parse(true);
    } catch (IOException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }

    star = new Star("0", "Sol", "0", "0", "0");
    testStar = (Star) starParser.parsedContent.get(0);

    assertEquals(119617, starParser.parsedContent.size());
    assertEquals(testStar.getStarId(), star.getStarId());
    assertEquals(testStar.getX(), star.getX());
    assertEquals(testStar.getY(), star.getY());
    assertEquals(testStar.getZ(), star.getZ());
    assertEquals(testStar.getProperName(), star.getProperName());
  }
}

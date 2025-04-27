package edu.brown.cs.student;

import REPL.CommandProcessor;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class REPLTest {

  @Test
  public void testREPLMock() throws IOException {
    try (MockSystemIn mockIn = MockSystemIn.build(8192);
        MockSystemOut mockOut = MockSystemOut.build(8192);
        MockSystemOut mockErr = MockSystemOut.build(8192)) {

      CommandProcessor proc =
          new CommandProcessor(mockIn.mockSystemIn(), mockOut.mockOutput(), mockErr.mockOutput());

      // pre-populating series of commands
      mockIn.println("LOAD:data/census/income_by_race.csv");
      mockIn.println("SEARCH:Race:White");
      mockIn.println("exit");

      proc.run();

      Assertions.assertTrue(mockOut.terminal().ready());
      String out1 = mockOut.terminal().readLine();
      Assertions.assertEquals("Welcome to CSV Search!", out1);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out2 = mockOut.terminal().readLine();
      Assertions.assertEquals(
          "Available commands are: LOAD <file>, SEARCH <column> <value>, or EXIT, USE ':' to separate commands and arguments",
          out2);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out3 = mockOut.terminal().readLine();
      Assertions.assertEquals(
          "CSV file loaded successfully: " + "data/census/income_by_race.csv", out3);

      List<String> incomeRaceWhiteResults =
          Arrays.asList(
              "1, White, 2020, 2020, 85359, 6432, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2020, 2020, 75408, 2311, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2020, 2020, 87407, 3706, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2020, 2020, 67639, 1255, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2020, 2020, 88147, 3942, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2019, 2019, 82750, 5075, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2019, 2019, 73415, 1906, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2019, 2019, 82158, 2740, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2019, 2019, 64195, 1128, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2019, 2019, 87019, 2150, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2018, 2018, 75730, 3643.0000000000000, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2018, 2018, 70402, 1972, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2018, 2018, 80035, 3330, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2018, 2018, 60437, 1180, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2018, 2018, 82240, 3183, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2017, 2017, 74736, 3411, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2017, 2017, 69912, 2016.0000000000000, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2017, 2017, 77244, 2184, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2017, 2017, 57917, 1170, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2017, 2017, 78350, 2452, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2016, 2016, 73255, 2929, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2016, 2016, 66116, 1376, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2016, 2016, 73101, 2389, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2016, 2016, 55418, 964, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2016, 2016, 74962, 2699, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2015, 2015, 72815, 3491.0000000000000, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2015, 2015, 65089, 1594, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2015, 2015, 72063, 2802, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2015, 2015, 54148, 1068, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2015, 2015, 74124, 2231, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2014, 2014, 70086, 5118, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2014, 2014, 63702, 1404, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2014, 2014, 74412, 2263, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2014, 2014, 54134, 854, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2014, 2014, 74110, 1884.0000000000000, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2013, 2013, 71580, 3233, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2013, 2013, 62855, 1339, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2013, 2013, 72924, 2313, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2013, 2013, 54104, 869, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2013, 2013, 73923, 2847, \"Washington County, RI\", 05000US44009, washington-county-ri");

      Assertions.assertTrue(mockOut.terminal().ready());
      String out4 = mockOut.terminal().readLine();
      Assertions.assertEquals("Search results:", out4);

      Assertions.assertTrue(mockOut.terminal().ready());
      for (String row : incomeRaceWhiteResults) {
        String output = mockOut.terminal().readLine();
        Assertions.assertEquals(row, output);
      }
    }
  }

  @Test
  public void testSearchBeforeLoad() throws IOException {
    try (MockSystemIn mockIn = MockSystemIn.build(8192);
        MockSystemOut mockOut = MockSystemOut.build(8192);
        MockSystemOut mockErr = MockSystemOut.build(8192)) {

      // Create a new application instance to test
      CommandProcessor proc =
          new CommandProcessor(mockIn.mockSystemIn(), mockOut.mockOutput(), mockErr.mockOutput());

      // pre-populating series of commands
      mockIn.println("SEARCH:Race:Black");
      mockIn.println("exit");
      proc.run();

      Assertions.assertTrue(mockOut.terminal().ready());
      String out1 = mockOut.terminal().readLine();
      Assertions.assertEquals("Welcome to CSV Search!", out1);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out2 = mockOut.terminal().readLine();
      Assertions.assertEquals(
          "Available commands are: LOAD <file>, SEARCH <column> <value>, or EXIT, USE ':' to separate commands and arguments",
          out2);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out3 = mockErr.terminal().readLine();
      Assertions.assertEquals("No CSV file loaded. Use LOAD <file_path> first.", out3);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out4 = mockOut.terminal().readLine();
      Assertions.assertEquals("Exiting...", out4);
    }
  }

  @Test
  public void testNoResults() throws IOException {
    try (MockSystemIn mockIn = MockSystemIn.build(8192);
        MockSystemOut mockOut = MockSystemOut.build(8192);
        MockSystemOut mockErr = MockSystemOut.build(8192)) {

      CommandProcessor proc =
          new CommandProcessor(mockIn.mockSystemIn(), mockOut.mockOutput(), mockErr.mockOutput());

      // pre-populating series of commands
      mockIn.println("LOAD:data/census/income_by_race.csv");
      mockIn.println("SEARCH:Race:Fuscia");
      mockIn.println("exit");

      proc.run();

      Assertions.assertTrue(mockOut.terminal().ready());
      String out1 = mockOut.terminal().readLine();
      Assertions.assertEquals("Welcome to CSV Search!", out1);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out2 = mockOut.terminal().readLine();
      Assertions.assertEquals(
          "Available commands are: LOAD <file>, SEARCH <column> <value>, or EXIT, USE ':' to separate commands and arguments",
          out2);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out3 = mockOut.terminal().readLine();
      Assertions.assertEquals("CSV file loaded successfully: data/census/income_by_race.csv", out3);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out4 = mockOut.terminal().readLine();
      Assertions.assertEquals("No matching results found.", out4);
    }
  }

  @Test
  public void testPresentButWrongColumn() throws IOException {
    try (MockSystemIn mockIn = MockSystemIn.build(8192);
        MockSystemOut mockOut = MockSystemOut.build(8192);
        MockSystemOut mockErr = MockSystemOut.build(8192)) {

      CommandProcessor proc =
          new CommandProcessor(mockIn.mockSystemIn(), mockOut.mockOutput(), mockErr.mockOutput());

      // pre-populating series of commands
      mockIn.println("LOAD:data/census/income_by_race.csv");
      mockIn.println("SEARCH:2:Black");
      mockIn.println("exit");

      proc.run();

      Assertions.assertTrue(mockOut.terminal().ready());
      String out1 = mockOut.terminal().readLine();
      Assertions.assertEquals("Welcome to CSV Search!", out1);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out2 = mockOut.terminal().readLine();
      Assertions.assertEquals(
          "Available commands are: LOAD <file>, SEARCH <column> <value>, or EXIT, USE ':' to separate commands and arguments",
          out2);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out3 = mockOut.terminal().readLine();
      Assertions.assertEquals("CSV file loaded successfully: data/census/income_by_race.csv", out3);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out4 = mockOut.terminal().readLine();
      Assertions.assertEquals("No matching results found.", out4);
    }
  }

  @Test
  public void testSearchByIndex() throws IOException {
    try (MockSystemIn mockIn = MockSystemIn.build(8192);
        MockSystemOut mockOut = MockSystemOut.build(8192);
        MockSystemOut mockErr = MockSystemOut.build(8192)) {

      CommandProcessor proc =
          new CommandProcessor(mockIn.mockSystemIn(), mockOut.mockOutput(), mockErr.mockOutput());

      // pre-populating series of commands
      mockIn.println("LOAD:data/census/income_by_race.csv");
      mockIn.println("SEARCH:1:White");
      mockIn.println("exit");

      proc.run();

      Assertions.assertTrue(mockOut.terminal().ready());
      String out1 = mockOut.terminal().readLine();
      Assertions.assertEquals("Welcome to CSV Search!", out1);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out2 = mockOut.terminal().readLine();
      Assertions.assertEquals(
          "Available commands are: LOAD <file>, SEARCH <column> <value>, or EXIT, USE ':' to separate commands and arguments",
          out2);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out3 = mockOut.terminal().readLine();
      Assertions.assertEquals("CSV file loaded successfully: data/census/income_by_race.csv", out3);

      List<String> incomeRaceWhiteResults =
          Arrays.asList(
              "1, White, 2020, 2020, 85359, 6432, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2020, 2020, 75408, 2311, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2020, 2020, 87407, 3706, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2020, 2020, 67639, 1255, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2020, 2020, 88147, 3942, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2019, 2019, 82750, 5075, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2019, 2019, 73415, 1906, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2019, 2019, 82158, 2740, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2019, 2019, 64195, 1128, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2019, 2019, 87019, 2150, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2018, 2018, 75730, 3643.0000000000000, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2018, 2018, 70402, 1972, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2018, 2018, 80035, 3330, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2018, 2018, 60437, 1180, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2018, 2018, 82240, 3183, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2017, 2017, 74736, 3411, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2017, 2017, 69912, 2016.0000000000000, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2017, 2017, 77244, 2184, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2017, 2017, 57917, 1170, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2017, 2017, 78350, 2452, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2016, 2016, 73255, 2929, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2016, 2016, 66116, 1376, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2016, 2016, 73101, 2389, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2016, 2016, 55418, 964, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2016, 2016, 74962, 2699, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2015, 2015, 72815, 3491.0000000000000, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2015, 2015, 65089, 1594, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2015, 2015, 72063, 2802, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2015, 2015, 54148, 1068, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2015, 2015, 74124, 2231, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2014, 2014, 70086, 5118, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2014, 2014, 63702, 1404, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2014, 2014, 74412, 2263, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2014, 2014, 54134, 854, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2014, 2014, 74110, 1884.0000000000000, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2013, 2013, 71580, 3233, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2013, 2013, 62855, 1339, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2013, 2013, 72924, 2313, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2013, 2013, 54104, 869, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2013, 2013, 73923, 2847, \"Washington County, RI\", 05000US44009, washington-county-ri");

      Assertions.assertTrue(mockOut.terminal().ready());
      String out4 = mockOut.terminal().readLine();
      Assertions.assertEquals("Search results:", out4);

      Assertions.assertTrue(mockOut.terminal().ready());
      for (String row : incomeRaceWhiteResults) {
        String output = mockOut.terminal().readLine();
        Assertions.assertEquals(row, output);
      }
    }
  }

  @Test
  public void testMalformedData() throws IOException {
    try (MockSystemIn mockIn = MockSystemIn.build(8192);
        MockSystemOut mockOut = MockSystemOut.build(8192);
        MockSystemOut mockErr = MockSystemOut.build(8192)) {

      CommandProcessor proc =
          new CommandProcessor(mockIn.mockSystemIn(), mockOut.mockOutput(), mockErr.mockOutput());

      // pre-populating series of commands
      mockIn.println("LOAD:data/malformed/malformed_signs.csv");
      mockIn.println("SEARCH:member:roberto");
      mockIn.println("exit");

      proc.run();

      Assertions.assertTrue(mockOut.terminal().ready());
      String out1 = mockOut.terminal().readLine();
      Assertions.assertEquals("Welcome to CSV Search!", out1);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out2 = mockOut.terminal().readLine();
      Assertions.assertEquals(
          "Available commands are: LOAD <file>, SEARCH <column> <value>, or EXIT, USE "
              + "':' to separate commands and arguments",
          out2);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out3 = mockOut.terminal().readLine();
      Assertions.assertEquals(
          "CSV file loaded successfully: data/malformed/malformed_signs.csv", out3);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out4 = mockOut.terminal().readLine();
      Assertions.assertEquals("Search results:", out4);

      Assertions.assertTrue(mockOut.terminal().ready());
      String output = mockOut.terminal().readLine();
      Assertions.assertEquals("Gemini, Roberto, Nick", output);
      // also test for case insensitivity in column identifier

    }
  }

  @Test
  public void testSearchStarData() throws IOException {
    try (MockSystemIn mockIn = MockSystemIn.build(8192);
        MockSystemOut mockOut = MockSystemOut.build(8192);
        MockSystemOut mockErr = MockSystemOut.build(8192)) {

      CommandProcessor proc =
          new CommandProcessor(mockIn.mockSystemIn(), mockOut.mockOutput(), mockErr.mockOutput());

      // pre-populating series of commands
      mockIn.println("LOAD:data/stars/stardata.csv");
      mockIn.println("SEARCH:propername:Lucille");
      mockIn.println("exit");

      proc.run();

      Assertions.assertTrue(mockOut.terminal().ready());
      String out1 = mockOut.terminal().readLine();
      Assertions.assertEquals("Welcome to CSV Search!", out1);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out2 = mockOut.terminal().readLine();
      Assertions.assertEquals(
          "Available commands are: LOAD <file>, SEARCH <column> <value>, or EXIT, USE "
              + "':' to separate commands and arguments",
          out2);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out3 = mockOut.terminal().readLine();
      Assertions.assertEquals("CSV file loaded successfully: data/stars/stardata.csv", out3);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out4 = mockOut.terminal().readLine();
      Assertions.assertEquals("Search results:", out4);

      Assertions.assertTrue(mockOut.terminal().ready());
      String output = mockOut.terminal().readLine();
      Assertions.assertEquals("29, Lucille, 229.69649, 0.36935, -265.24363", output);
      // also test for case insensitivity in column identifier

    }
  }

  @Test
  public void testSearchByIdentifier() throws IOException {
    try (MockSystemIn mockIn = MockSystemIn.build(8192);
        MockSystemOut mockOut = MockSystemOut.build(8192);
        MockSystemOut mockErr = MockSystemOut.build(8192)) {

      CommandProcessor proc =
          new CommandProcessor(mockIn.mockSystemIn(), mockOut.mockOutput(), mockErr.mockOutput());

      // pre-populating series of commands
      mockIn.println("LOAD:data/census/income_by_race.csv");
      mockIn.println("SEARCH:Race:White");
      mockIn.println("exit");

      proc.run();

      Assertions.assertTrue(mockOut.terminal().ready());
      String out1 = mockOut.terminal().readLine();
      Assertions.assertEquals("Welcome to CSV Search!", out1);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out2 = mockOut.terminal().readLine();
      Assertions.assertEquals(
          "Available commands are: LOAD <file>, SEARCH <column> <value>, or EXIT,"
              + " USE ':' to separate commands and arguments",
          out2);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out3 = mockOut.terminal().readLine();
      Assertions.assertEquals("CSV file loaded successfully: data/census/income_by_race.csv", out3);

      List<String> incomeRaceWhiteResults =
          Arrays.asList(
              "1, White, 2020, 2020, 85359, 6432, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2020, 2020, 75408, 2311, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2020, 2020, 87407, 3706, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2020, 2020, 67639, 1255, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2020, 2020, 88147, 3942, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2019, 2019, 82750, 5075, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2019, 2019, 73415, 1906, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2019, 2019, 82158, 2740, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2019, 2019, 64195, 1128, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2019, 2019, 87019, 2150, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2018, 2018, 75730, 3643.0000000000000, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2018, 2018, 70402, 1972, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2018, 2018, 80035, 3330, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2018, 2018, 60437, 1180, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2018, 2018, 82240, 3183, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2017, 2017, 74736, 3411, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2017, 2017, 69912, 2016.0000000000000, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2017, 2017, 77244, 2184, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2017, 2017, 57917, 1170, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2017, 2017, 78350, 2452, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2016, 2016, 73255, 2929, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2016, 2016, 66116, 1376, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2016, 2016, 73101, 2389, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2016, 2016, 55418, 964, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2016, 2016, 74962, 2699, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2015, 2015, 72815, 3491.0000000000000, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2015, 2015, 65089, 1594, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2015, 2015, 72063, 2802, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2015, 2015, 54148, 1068, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2015, 2015, 74124, 2231, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2014, 2014, 70086, 5118, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2014, 2014, 63702, 1404, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2014, 2014, 74412, 2263, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2014, 2014, 54134, 854, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2014, 2014, 74110, 1884.0000000000000, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "1, White, 2013, 2013, 71580, 3233, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "1, White, 2013, 2013, 62855, 1339, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "1, White, 2013, 2013, 72924, 2313, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "1, White, 2013, 2013, 54104, 869, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "1, White, 2013, 2013, 73923, 2847, \"Washington County, RI\", 05000US44009, washington-county-ri");

      Assertions.assertTrue(mockOut.terminal().ready());
      String out4 = mockOut.terminal().readLine();
      Assertions.assertEquals("Search results:", out4);

      Assertions.assertTrue(mockOut.terminal().ready());
      for (String row : incomeRaceWhiteResults) {
        String output = mockOut.terminal().readLine();
        Assertions.assertEquals(row, output);
      }
    }
  }

  @Test
  public void testSearchWithoutIdentifier() throws IOException {

    // also testing values with spaces within them

    try (MockSystemIn mockIn = MockSystemIn.build(8192);
        MockSystemOut mockOut = MockSystemOut.build(8192);
        MockSystemOut mockErr = MockSystemOut.build(8192)) {

      CommandProcessor proc =
          new CommandProcessor(mockIn.mockSystemIn(), mockOut.mockOutput(), mockErr.mockOutput());

      // pre-populating series of commands
      mockIn.println("LOAD:data/census/income_by_race.csv");
      mockIn.println("SEARCH:Two or More");
      mockIn.println("exit");

      proc.run();

      Assertions.assertTrue(mockOut.terminal().ready());
      String out1 = mockOut.terminal().readLine();
      Assertions.assertEquals("Welcome to CSV Search!", out1);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out2 = mockOut.terminal().readLine();
      Assertions.assertEquals(
          "Available commands are: LOAD <file>, SEARCH <column> <value>, or EXIT, USE ':' to separate commands and arguments",
          out2);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out3 = mockOut.terminal().readLine();
      Assertions.assertEquals("CSV file loaded successfully: data/census/income_by_race.csv", out3);

      List<String> incomeRaceWhiteResults =
          Arrays.asList(
              "7, Two Or More, 2020, 2020, 100250, 22504, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "7, Two Or More, 2020, 2020, 75938, 26788, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "7, Two Or More, 2020, 2020, 83574, 5944, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "7, Two Or More, 2020, 2020, 47163, 8892, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "7, Two Or More, 2020, 2020, 96898, 42213, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "7, Two Or More, 2019, 2019, 110078, 43635, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "7, Two Or More, 2019, 2019, 58112, 11419, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "7, Two Or More, 2019, 2019, 70750, 16835, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "7, Two Or More, 2019, 2019, 45030, 6614, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "7, Two Or More, 2018, 2018, 48083, 14257, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "7, Two Or More, 2018, 2018, 68125, 32171, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "7, Two Or More, 2018, 2018, 41607, 3396, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "7, Two Or More, 2017, 2017, 77011, 37514, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "7, Two Or More, 2017, 2017, 44000, 11831, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "7, Two Or More, 2017, 2017, 56250, 45583, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "7, Two Or More, 2017, 2017, 40453, 5913, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "7, Two Or More, 2016, 2016, 73315, 62393.00000000000, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "7, Two Or More, 2016, 2016, 34136, 7418, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "7, Two Or More, 2016, 2016, 51125, 43611, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "7, Two Or More, 2016, 2016, 35605, 6327, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "7, Two Or More, 2016, 2016, 51860, 42964, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "7, Two Or More, 2015, 2015, 38750, 18572, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "7, Two Or More, 2015, 2015, 36607, 21926, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "7, Two Or More, 2015, 2015, 37426, 6042, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "7, Two Or More, 2015, 2015, 48950, 27437, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "7, Two Or More, 2014, 2014, 59969, 30608, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "7, Two Or More, 2014, 2014, 31861, 6940.000000000000, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "7, Two Or More, 2014, 2014, 45404, 17020, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "7, Two Or More, 2014, 2014, 36420, 4046, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "7, Two Or More, 2014, 2014, 50857, 6132, \"Washington County, RI\", 05000US44009, washington-county-ri",
              "7, Two Or More, 2013, 2013, 55854, 40346, \"Bristol County, RI\", 05000US44001, bristol-county-ri",
              "7, Two Or More, 2013, 2013, 42708, 17404, \"Kent County, RI\", 05000US44003, kent-county-ri",
              "7, Two Or More, 2013, 2013, 36932, 24531, \"Newport County, RI\", 05000US44005, newport-county-ri",
              "7, Two Or More, 2013, 2013, 35795, 5484, \"Providence County, RI\", 05000US44007, providence-county-ri",
              "7, Two Or More, 2013, 2013, 50083, 26976, \"Washington County, RI\", 05000US44009, washington-county-ri");

      Assertions.assertTrue(mockOut.terminal().ready());
      String out4 = mockOut.terminal().readLine();
      Assertions.assertEquals("Search results:", out4);

      Assertions.assertTrue(mockOut.terminal().ready());
      for (String row : incomeRaceWhiteResults) {
        String output = mockOut.terminal().readLine();
        Assertions.assertEquals(row, output);
      }
    }
  }

  @Test
  public void testUsingIncorrectDelimeter() throws IOException {
    try (MockSystemIn mockIn = MockSystemIn.build(8192);
        MockSystemOut mockOut = MockSystemOut.build(8192);
        MockSystemOut mockErr = MockSystemOut.build(8192)) {

      CommandProcessor proc =
          new CommandProcessor(mockIn.mockSystemIn(), mockOut.mockOutput(), mockErr.mockOutput());

      // pre-populating series of commands
      mockIn.println("LOAD:data/census/income_by_race.csv");
      mockIn.println("SEARCH Two or More");
      mockIn.println("exit");

      proc.run();

      Assertions.assertTrue(mockOut.terminal().ready());
      String out1 = mockOut.terminal().readLine();
      Assertions.assertEquals("Welcome to CSV Search!", out1);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out2 = mockOut.terminal().readLine();
      Assertions.assertEquals(
          "Available commands are: LOAD <file>, SEARCH <column> <value>, or EXIT, USE ':' to separate commands and arguments",
          out2);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out3 = mockOut.terminal().readLine();
      Assertions.assertEquals("CSV file loaded successfully: data/census/income_by_race.csv", out3);

      Assertions.assertTrue(mockOut.terminal().ready());
      String out4 = mockErr.terminal().readLine();
      Assertions.assertEquals("ERROR: Invalid command.", out4);
    }
  }
}

package calendar.controller;

import org.junit.Before;
import org.junit.Test;


import calendar.model.CalendarManager;
import calendar.model.Event;
import calendar.view.CalendarTextView;
import calendar.view.CalendarView;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Represents testing for HeadlessController class.
 */
public class HeadlessControllerTest {

  private CalendarManager manager;
  private CalendarView view;
  private CommandParser parser;
  private ByteArrayOutputStream output;
  private HeadlessController controller;
  private String validCommandsContent;
  private String commandOneInvalidContent;
  private String noExitContent;

  @Before
  public void setup() {
    this.manager = new CalendarManager();
    this.output = new ByteArrayOutputStream();
    this.view = new CalendarTextView(new PrintStream(output));
    this.parser = new CommandParserImpl(manager, view);

    try {
      this.validCommandsContent = readFileContent("res/valid_commands.txt");
      this.commandOneInvalidContent = readFileContent("res/command_one_invalid.txt");
      this.noExitContent = readFileContent("res/no_exit.txt");
    } catch (IOException e) {
      this.validCommandsContent = "";
      this.commandOneInvalidContent = "";
      this.noExitContent = "";
    }
  }

  private String readFileContent(String filename) throws IOException {
    StringBuilder content = new StringBuilder();
    try (FileReader fileReader = new FileReader(filename);
         Scanner scanner = new Scanner(fileReader)) {
      while (scanner.hasNextLine()) {
        content.append(scanner.nextLine()).append("\n");
      }
    }
    return content.toString();
  }

  @Test
  public void testValidCommandsErrors() {
    StringReader reader = new StringReader("create calendar --name testCal " +
            "--timezone America/New_York\nuse calendar --name testCal\n" + validCommandsContent);
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertTrue(this.output.toString().contains("Error"));
  }

  @Test
  public void testCommandOneInvalidErrors() {
    StringReader reader = new StringReader("create calendar --name testCal " +
            "--timezone America/New_York\nuse " +
            "calendar --name testCal\n" + commandOneInvalidContent);
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertTrue(this.output.toString().contains("Error"));
  }

  @Test
  public void testNoExitFileError() {
    StringReader reader = new StringReader("create calendar --name testCal" +
            " --timezone America/New_York\nuse calendar --name testCal\n" + noExitContent);
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertTrue(this.output.toString().contains("File must end with 'exit' command"));
  }

  @Test
  public void testBasicCreateCommand() {
    StringReader reader = new StringReader("create calendar --name cal1 --timezone " +
            "America/New_York\nuse calendar --name cal1\ncreate event Meeting from " +
            "2024-03-15T10:00 to 2024-03-15T11:00\nexit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    List<Event> events = manager.getCalendar("cal1").getEvents();
    assertEquals(1, events.size());
    assertEquals("Meeting", events.get(0).getSubject());
  }

  @Test
  public void testPrintEventOnDayCommand() {
    StringReader reader = new StringReader("create calendar --name cal1 --timezone " +
            "America/New_York\nuse calendar --name cal1\ncreate event Meeting from " +
            "2024-03-15T10:00 to 2024-03-15T11:00\nprint events on 2024-03-15\nexit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    List<Event> events = manager.getCalendar("cal1").getEvents();
    assertTrue(output.toString().contains("Meeting from 10:00 to 11:00 on 2024-03-15"));
    assertEquals(1, events.size());
    assertEquals("Meeting", events.get(0).getSubject());
  }

  @Test
  public void testPrintEventsInRangeCommand() {
    StringReader reader = new StringReader("create calendar --name cal1 --timezone " +
            "America/New_York\nuse calendar --name cal1\ncreate event Meeting from " +
            "2024-03-15T10:00 to 2024-03-15T11:00\nprint events from 2024-03-15T00:00 to " +
            "2024-03-16T00:00\nexit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertTrue(output.toString().contains("Meeting from 10:00 to 11:00 on 2024-03-15"));
  }

  @Test
  public void testShowStatusBusyCommand() {
    StringReader reader = new StringReader("create calendar --name cal1 --timezone " +
            "America/New_York\nuse calendar --name cal1\ncreate event Meeting from " +
            "2024-03-15T10:00 to 2024-03-15T11:00\nshow status on 2024-03-15T10:00\nexit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertTrue(output.toString().contains("Meeting from 10:00 to 11:00 on 2024-03-15"));
  }

  @Test
  public void testExitCommand() {
    StringReader reader = new StringReader("create calendar --name cal1 --timezone" +
            " America/New_York\nuse calendar --name cal1\nexit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertTrue(manager.getCalendar("cal1").getEvents().isEmpty());
  }

  @Test
  public void testCreateSeriesCommand() {
    StringReader reader = new StringReader("create calendar --name cal1 --timezone " +
            "America/New_York\nuse calendar --name cal1\ncreate event \"Weekly Meeting\" " +
            "from 2024-03-15T10:00 to 2024-03-15T11:00 repeats M for 3 times\nexit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertEquals(3, manager.getCalendar("cal1").getEvents().size());
  }

  @Test
  public void testQuoteSubject() {
    StringReader reader = new StringReader("create calendar --name cal1 --timezone " +
            "America/New_York\nuse calendar --name cal1\ncreate event \"CS Project Work\" " +
            "from 2024-03-15T10:00 to 2024-03-15T11:00\nexit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    List<Event> events = manager.getCalendar("cal1").getEvents();
    assertEquals(1, events.size());
    assertEquals("CS Project Work", events.get(0).getSubject());
  }

  @Test
  public void testInvalidCommandThrowsError() {
    StringReader reader = new StringReader("create calendar --name cal1 --timezone " +
            "America/New_York\nuse calendar --name cal1\ndelete event something\nexit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    String outputText = this.output.toString();
    assertTrue(outputText.contains("Error"));
    assertTrue(outputText.contains("Invalid command"));
  }

  @Test
  public void testMultipleValidCommands() {
    StringReader reader = new StringReader("create calendar --name cal1 --timezone " +
            "America/New_York\nuse calendar --name cal1\ncreate event First from " +
            "2024-03-15T10:00 to 2024-03-15T11:00\ncreate event Second from " +
            "2024-03-15T12:00 to 2024-03-15T13:00\nexit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertEquals(2, manager.getCalendar("cal1").getSingleEvents().size());
  }

  @Test
  public void testRecurringWeekdays() {
    StringReader reader = new StringReader("create calendar --name cal1 --timezone " +
            "America/New_York\nuse calendar --name cal1\ncreate event \"Daily Standup\"" +
            " from 2024-03-15T09:00 to 2024-03-15T09:30 repeats MTWRF for 5 times\nexit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertEquals(5, manager.getCalendar("cal1").getEvents().size());
  }

  @Test
  public void testCaseInsensitiveExit() {
    StringReader reader = new StringReader("create calendar --name cal1 --timezone" +
            " America/New_York\nuse calendar --name cal1\ncreate event Test from " +
            "2024-03-15T10:00 to 2024-03-15T11:00\nEXIT\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertEquals(1, manager.getCalendar("cal1").getEvents().size());
    assertFalse(this.output.toString().contains("File must end with 'exit' command"));
  }

  @Test
  public void testMixedValidInvalidCommands() {
    StringReader reader = new StringReader("create calendar --name cal1 --timezone " +
            "America/New_York\nuse calendar --name cal1\ncreate event Valid from" +
            " 2024-03-15T10:00 to 2024-03-15T11:00\ninvalid command here\ncreate event " +
            "Another from 2024-03-15T12:00 to 2024-03-15T13:00\nexit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertEquals(2, manager.getCalendar("cal1").getEvents().size());
    assertTrue(this.output.toString().contains("Error"));
  }

  @Test
  public void testExitWithWhitespace() {
    StringReader reader = new StringReader("create calendar --name cal1 --timezone" +
            " America/New_York\nuse calendar --name cal1\ncreate event Test from " +
            "2024-03-15T10:00 to 2024-03-15T11:00\n   exit   \n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertEquals(1, manager.getCalendar("cal1").getEvents().size());
    assertTrue(this.output.toString().contains("File must end with 'exit' command"));
  }

  @Test
  public void testRecurringWeekends() {
    StringReader reader = new StringReader("create calendar --name cal1 --timezone " +
            "America/New_York\nuse calendar --name cal1\ncreate event \"Weekend Plans\" " +
            "from 2024-03-16T14:00 to 2024-03-16T16:00 repeats SU for 4 times\nexit\n");
    CalendarManager manager = new CalendarManager();
    CalendarView view = new CalendarTextView(System.out);
    CommandParser parser = new CommandParserImpl(manager, view);
    HeadlessController controller = new HeadlessController(view, reader, parser);
    controller.run();
    assertEquals(4, manager.getCalendar("cal1").getEvents().size());
  }

  @Test
  public void testEmptyFileNeedsExit() {
    StringReader reader = new StringReader("");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertTrue(this.output.toString().contains("File must end with 'exit' command"));
  }

  @Test
  public void testCreateAndUseMultipleCalendars() {
    StringReader reader = new StringReader(
            "create calendar --name cal1 --timezone America/New_York\n" +
                    "create calendar --name cal2 --timezone America/Los_Angeles\n" +
                    "use calendar --name cal1\n" +
                    "create event Meeting from 2024-03-15T10:00 to 2024-03-15T11:00\n" +
                    "use calendar --name cal2\n" +
                    "create event Lunch from 2024-03-15T12:00 to 2024-03-15T13:00\n" +
                    "exit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertEquals(1, manager.getCalendar("cal1").getEvents().size());
    assertEquals("Meeting", manager.getCalendar("cal1").getEvents().get(0).getSubject());
    assertEquals(1, manager.getCalendar("cal2").getEvents().size());
    assertEquals("Lunch", manager.getCalendar("cal2").getEvents().get(0).getSubject());
  }

  @Test
  public void testDuplicateCalendarNameThrowsError() {
    StringReader reader = new StringReader(
            "create calendar --name cal1 --timezone America/New_York\n" +
                    "create calendar --name cal1 --timezone America/Chicago\n" +
                    "exit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertTrue(this.output.toString().contains("Error"));
    assertTrue(this.output.toString().contains("already exists"));
    assertEquals(1, manager.getCalendars().size());
  }

  @Test
  public void testSetAndChangeTimezone() {
    StringReader reader = new StringReader(
            "create calendar --name cal1 --timezone America/New_York\n" +
                    "use calendar --name cal1\n" +
                    "edit calendar --name cal1 --property timezone America/Los_Angeles\n" +
                    "exit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertEquals("America/Los_Angeles",
            manager.getCalendar("cal1").getZoneId().toString());
  }

  @Test
  public void testSetAndChangeName() {
    StringReader reader = new StringReader(
            "create calendar --name cal1 --timezone America/New_York\n" +
                    "use calendar --name cal1\n" +
                    "edit calendar --name cal1 --property name newcal1\n" +
                    "use calendar --name newcal1\n" +
                    "exit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    try {
      this.manager.getCalendar("cal1");
      fail("shouldn't get here");
    } catch (IllegalArgumentException e) {
      this.manager.getCalendar("newcal1");
      //Should get here and therefore test passes.
    }
  }

  @Test
  public void testCopyEventSameTimezone() {
    StringReader reader = new StringReader(
            "create calendar --name cal1 --timezone America/New_York\n" +
                    "create calendar --name cal2 --timezone America/New_York\n" +
                    "use calendar --name cal1\n" +
                    "create event Meeting from 2024-03-15T10:00 to 2024-03-15T11:00\n" +
                    "copy events on 2024-03-15 --target cal2 " +
                    "to 2024-03-15\n" +
                    "exit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertEquals(1, manager.getCalendar("cal2").getEvents().size());
    assertEquals("Meeting", manager.getCalendar("cal2").
            getEvents().get(0).getSubject());
    assertEquals(manager.getCalendar("cal1").getEvents().get(0).getStartDateTime(),
            manager.getCalendar("cal2").getEvents().get(0).getStartDateTime());
  }

  @Test
  public void testCopyEventDifferentTimezone() {
    StringReader reader = new StringReader(
            "create calendar --name cal1 --timezone America/New_York\n" +
                    "create calendar --name cal2 --timezone America/Los_Angeles\n" +
                    "use calendar --name cal1\n" +
                    "create event Meeting from 2024-03-15T10:00 to 2024-03-15T11:00\n" +
                    "copy events on 2024-03-15 --target cal2 " +
                    "to 2024-03-15\n" +
                    "exit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertEquals(1, manager.getCalendar("cal2").getEvents().size());
    assertEquals("Meeting", manager.getCalendar("cal2").
            getEvents().get(0).getSubject());
    assertEquals(manager.getCalendar("cal1").getEvents().get(0).getStartDateTime(),
            LocalDateTime.of(2024, 3, 15, 10, 00));
    assertEquals(manager.getCalendar("cal2").getEvents().get(0).getStartDateTime(),
            LocalDateTime.of(2024, 3, 15, 7, 00));
  }

  @Test
  public void testCopySingleEventSpecifiedTime() {
    StringReader reader = new StringReader(
            "create calendar --name cal1 --timezone America/New_York\n" +
                    "create calendar --name cal2 --timezone America/Los_Angeles\n" +
                    "use calendar --name cal1\n" +
                    "create event Meeting from 2024-03-15T10:00 to 2024-03-15T11:00\n" +
                    "copy event Meeting on 2024-03-15T10:00 --target " +
                    "cal2 to 2024-03-15T10:00\n" +
                    "exit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertEquals(manager.getCalendar("cal1").getEvents().get(0).getStartDateTime(),
            manager.getCalendar("cal2").getEvents().get(0).getStartDateTime());
  }

  @Test
  public void testCopyEventsInRange() {
    StringReader reader = new StringReader(
            "create calendar --name cal1 --timezone America/New_York\n" +
                    "create calendar --name cal2 --timezone America/New_York\n" +
                    "use calendar --name cal1\n" +
                    "create event Meeting from 2025-06-12T10:00 to 2025-06-12T11:00 repeats " +
                    "M for 5 times\n" +
                    "copy events between 2025-06-12 and 2025-07-01 --target cal2 to 2025-10-01\n" +
                    "exit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertEquals(3, manager.getCalendar("cal2").getEvents().size());
    assertEquals(LocalDate.of(2025, 10, 5),
            manager.getCalendar("cal2").getEvents().get(0).getStartDateTime().toLocalDate());
  }

  @Test
  public void testCopyNonexistentEventError() {
    StringReader reader = new StringReader(
            "create calendar --name cal1 --timezone America/New_York\n" +
                    "create calendar --name cal2 --timezone America/New_York\n" +
                    "copy event Meeting on 2024-03-15T10:00 --target " +
                    "cal2 to 2024-03-15T10:00\n" +
                    "exit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertTrue(this.output.toString().contains("Error"));
  }

  @Test
  public void testCopyEventToNonexistentCalendar() {
    StringReader reader = new StringReader(
            "create calendar --name cal1 --timezone America/New_York\n" +
                    "use calendar --name cal1\n" +
                    "create event Test from 2024-03-15T09:00 to 2024-03-15T10:00\n" +
                    "copy event Test on 2024-03-15T10:00 --target " +
                    "cal45 to 2024-03-15T10:00\n" +
                    "exit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertTrue(this.output.toString().contains("Error"));
  }

  @Test
  public void testCreateCalendarMissingTimezoneError() {
    StringReader reader = new StringReader(
            "create calendar --name cal1\nexit\n");
    this.controller = new HeadlessController(this.view, reader, this.parser);
    this.controller.run();
    assertTrue(this.output.toString().contains("Error"));
  }

}
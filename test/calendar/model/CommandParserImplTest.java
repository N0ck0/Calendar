package calendar.model;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import calendar.controller.CommandParser;
import calendar.controller.CommandParserImpl;
import calendar.controller.InteractiveController;
import calendar.controller.commands.Command;
import calendar.controller.commands.CommandExecutionException;
import calendar.controller.commands.CreateEventCommand;
import calendar.controller.commands.CreateEventSeriesCommand;
import calendar.view.CalendarTextView;
import calendar.view.CalendarView;
import java.io.StringReader;

/**
 * Represents testing for CommandParserImpl class.
 */
public class CommandParserImplTest {

  private StringReader createReader(String input) {
    return new StringReader(input);
  }

  @Test
  public void testBasicCreateEventParsing() throws CommandExecutionException {
    CalendarManager manager = new CalendarManager();
    CalendarView view = new CalendarTextView(System.out);
    CommandParser parser = new CommandParserImpl(manager, view);
    InteractiveController controller = new InteractiveController(view,
            createReader("create calendar --name cal1 --timezone America/New_York\n" +
                    "use calendar --name cal1\n"), parser);
    controller.run();
    String command = "create event interview from 2024-06-15T14:00 to 2024-06-15T15:00";

    Command result = parser.parse(command);
    assertEquals(CreateEventCommand.class, result.getClass());
  }

  @Test
  public void testInvalidCommandGivesError() {
    CalendarManager manager = new CalendarManager();
    CalendarView view = new CalendarTextView(System.out);
    CommandParser parser = new CommandParserImpl(manager, view);
    InteractiveController controller = new InteractiveController(view,
            createReader("create calendar --name cal1 --timezone America/New_York\n" +
                    "use calendar --name cal1\n"), parser);
    controller.run();

    String badCommand = "delete event \"Stats Quiz\"";
    String exceptionMessage = "";
    try {
      parser.parse(badCommand);
    } catch (CommandExecutionException e) {
      exceptionMessage = e.getMessage();
    }

    assertEquals("Invalid command", exceptionMessage);
  }

  @Test
  public void testRecurringEventParsing() throws CommandExecutionException {
    CalendarManager manager = new CalendarManager();
    CalendarView view = new CalendarTextView(System.out);
    CommandParser parser = new CommandParserImpl(manager, view);
    InteractiveController controller = new InteractiveController(view,
            createReader("create calendar --name cal1 --timezone America/New_York\n" +
                    "use calendar --name cal1\n"), parser);
    controller.run();

    String seriesCommand = "create event \"CS Study Group\""
            +
            " from 2024-03-15T10:00 to 2024-03-15T11:00 repeats MW for 4 times";

    Command result = parser.parse(seriesCommand);
    assertEquals(CreateEventSeriesCommand.class, result.getClass());
  }
}
package calendar.model;

import org.junit.Before;
import org.junit.Test;

import java.time.ZoneId;

import calendar.controller.commands.CommandExecutionException;
import calendar.controller.commands.CreateEventCommand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Represents testing for CreateEventCommand class.
 */
public class CreateEventCommandTest {

  private CalendarModel model;
  private CreateEventCommand basicCommand;
  private String validStart;
  private String validEnd;

  @Before
  public void setup() throws CommandExecutionException {
    this.model = new CalendarModelImpl("cal1", ZoneId.of("America/New_York"));
    this.validStart = "2024-03-15T10:00";
    this.validEnd = "2024-03-15T11:00";
    this.basicCommand = new CreateEventCommand(this.model, "Team Meeting",
            this.validStart, this.validEnd);
  }

  @Test
  public void testCommandExecutionAddsEventToModel() throws CommandExecutionException {
    assertEquals(0, this.model.getEvents().size());
    this.basicCommand.execute();
    assertEquals(1, this.model.getEvents().size());
    assertEquals("Team Meeting", this.model.getEvents().get(0).getSubject());
  }


  @Test
  public void testCommandWithMidnightTime() throws CommandExecutionException {
    CreateEventCommand midnightCommand = new CreateEventCommand(this.model,
            "New Year Party",
            "2024-01-01T00:00", "2024-01-01T02:00");
    midnightCommand.execute();
    assertEquals(1, this.model.getEvents().size());
    assertEquals(0, this.model.getEvents().get(0).getStartDateTime().getHour());
  }

  @Test
  public void testCommandWithInvalidTimes() throws CommandExecutionException {
    try {
      CreateEventCommand midnightCommand = new CreateEventCommand(this.model,
              "New Year Party",
              "2024-01-01T02:00", "2024-01-01T01:00");
      fail("shouldn't get here");
    } catch (IllegalArgumentException e) {
      //Should get here and pass.
    }
  }

  @Test
  public void testCommandWithSpecialCharactersInSubject() throws CommandExecutionException {
    CreateEventCommand specialCommand = new CreateEventCommand(this.model,
            "Dr. Smith's $500 Event @ 3pm!",
            this.validStart, this.validEnd);
    specialCommand.execute();
    assertEquals("Dr. Smith's $500 Event @ 3pm!",
            this.model.getEvents().get(0).getSubject());
  }

  @Test
  public void testMultipleCommandsExecution() throws CommandExecutionException {
    this.basicCommand.execute();
    CreateEventCommand secondCommand = new CreateEventCommand(this.model, "Lunch Meeting",
            "2024-03-15T12:00", "2024-03-15T13:00");
    secondCommand.execute();
    assertEquals(2, this.model.getEvents().size());
  }

  @Test
  public void testCommandWithInvalidStartDateThrowsException() {
    String exceptionMessage = "";
    try {
      new CreateEventCommand(this.model, "Invalid Event", "invalid-date",
              this.validEnd);
    } catch (CommandExecutionException e) {
      exceptionMessage = "CommandExecutionException thrown";
    }
    assertEquals("CommandExecutionException thrown", exceptionMessage);
  }

  @Test
  public void testCommandWithInvalidEndDateThrowsException() {
    String exceptionMessage = "";
    try {
      new CreateEventCommand(this.model, "Invalid Event",
              this.validStart, "bad-date");
    } catch (CommandExecutionException e) {
      exceptionMessage = "CommandExecutionException thrown";
    }
    assertEquals("CommandExecutionException thrown", exceptionMessage);
  }

  @Test
  public void testCommandWithEmptySubject() throws CommandExecutionException {
    CreateEventCommand emptyCommand = new CreateEventCommand(this.model, "",
            this.validStart, this.validEnd);
    emptyCommand.execute();
    assertEquals("", this.model.getEvents().get(0).getSubject());
  }

  @Test
  public void testCommandWithLongSubject() throws CommandExecutionException {
    String longSubject = "This is a very long event subject that goes"
            +
            " on purpose lol";
    CreateEventCommand longCommand = new CreateEventCommand(this.model, longSubject,
            this.validStart, this.validEnd);
    longCommand.execute();
    assertEquals(longSubject, model.getEvents().get(0).getSubject());
  }

  @Test
  public void testCommandExecutionTwiceDoesNotDuplicate() throws CommandExecutionException {
    this.basicCommand.execute();
    try {
      this.basicCommand.execute();
      fail("Shouldn't get here");
    } catch (CommandExecutionException e) {
      //Should get here and pass.
    }
  }
}
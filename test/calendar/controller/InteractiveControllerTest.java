package calendar.controller;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import calendar.model.CalendarManager;
import calendar.view.CalendarTextView;
import calendar.view.CalendarView;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;

/**
 * Testing class for the InteractiveController class.
 */
public class InteractiveControllerTest {

  private CalendarManager manager;
  private CalendarView view;
  private CommandParser parser;
  private ByteArrayOutputStream output;
  private InteractiveController controller;

  @Before
  public void setup() {
    this.manager = new CalendarManager();
    this.output = new ByteArrayOutputStream();
    this.view = new CalendarTextView(new PrintStream(this.output));
    this.parser = new CommandParserImpl(this.manager, this.view);
  }

  private StringReader createReader(String input) {
    return new StringReader(input);
  }

  @Test
  public void testBasicExit() {
    CalendarManager manager = new CalendarManager();
    CalendarView view = new CalendarTextView(System.out);
    CommandParser parser = new CommandParserImpl(manager, view);
    InteractiveController controller = new InteractiveController(view,
            createReader("create calendar --name cal1 --timezone America/New_York\n" +
                    "use calendar --name cal1\n" +
                    "exit\n"), parser);
    controller.run();
    assertEquals(0, manager.getActiveCalendar().getEvents().size());
  }

  @Test
  public void testCreateStatsQuiz() {
    controller = new InteractiveController(view, createReader("create calendar --name cal1 " +
            "--timezone America/New_York\n" +
            "use calendar --name cal1\n" +
            "create event \"Stats " +
            "Quiz\" from 2023-04-14T10:00 to 2023-04-14T11:15\nexit\n"), parser);
    controller.run();
    assertEquals(1, this.manager.getActiveCalendar().getEvents().size());
    assertEquals("Stats Quiz", this.manager.getActiveCalendar().getEvents().get(0).getSubject());
  }

  @Test
  public void testCSProjectRecurrence() {
    controller = new InteractiveController(view, createReader("create calendar --name cal1 " +
            "--timezone America/New_York\n" +
            "use calendar --name cal1\n" +
            "create event \"CS project" +
            "work\" from 2023-11-05T16:00 to 2023-11-05T19:00 repeats " +
            "MWF for 3 times\nexit\n"), parser);
    controller.run();
    assertEquals(3, this.manager.getActiveCalendar().getEvents().size());
  }

  @Test
  public void testPhilosophyEssay() {
    controller = new InteractiveController(view, createReader("create calendar --name cal1 " +
            "--timezone America/New_York\n" +
            "use calendar --name cal1\n" +
            "create event \"Philosophy " +
            "Essay Due\" from 2023-09-28T23:59 to 2023-09-28T23:59\nexit\n"), parser);
    controller.run();
    assertEquals(1, this.manager.getActiveCalendar().getEvents().size());
  }

  @Test
  public void testOrganicChem() {
    controller = new InteractiveController(view, createReader("create calendar --name cal1 " +
            "--timezone America/New_York\n" +
            "use calendar --name cal1\n" +
            "create event \"Organic " +
            "Chemistry\" from 2024-09-12T11:00 to 2024-09-12T12:15\nexit\n"), parser);
    controller.run();
    assertEquals(1, this.manager.getActiveCalendar().getEvents().size());
  }

  @Test
  public void testBasketballRecurrence() {
    controller = new InteractiveController(view, createReader("create calendar --name " +
            "cal1 --timezone America/New_York\n" +
            "use calendar --name cal1\n" +
            "create event \"Pickup " +
            "basketball with friends\" from 2023-08-30T16:00 to 2023-08-30T18:00 " +
            "repeats WF for 12 times\nexit\n"), parser);
    controller.run();
    assertEquals(12, this.manager.getActiveCalendar().getEvents().size());
  }

  @Test
  public void testKeepsGoingAfterBadCommand() {
    controller = new InteractiveController(view, createReader("create calendar --name cal1 " +
            "--timezone America/New_York\n" +
            "use calendar --name cal1\n" +
            "list all my events\ncreate " +
            "event \"Study group for midterms\" from 2023-10-18T19:00 to " +
            "2023-10-18T22:00\nexit\n"), parser);
    controller.run();
    assertEquals(1, this.manager.getActiveCalendar().getEvents().size());
    assertEquals(true, output.toString().contains("Error"));
  }

  @Test
  public void testOfficeHours() {
    controller = new InteractiveController(view, createReader("create calendar --name cal1 " +
            "--timezone America/New_York\n" +
            "use calendar --name cal1\n" +
            "create event \"Office hours with" +
            " Prof Martinez\" from 2024-01-15T14:00 to 2024-01-15T15:30 repeats MW " +
            "for 6 times\nexit\n"), parser);
    controller.run();
    assertEquals(6, this.manager.getActiveCalendar().getEvents().size());
  }

  @Test
  public void testJobInterview() {
    controller = new InteractiveController(view, createReader("create calendar --name " +
            "cal1 --timezone America/New_York\n" +
            "use calendar --name cal1\n" +
            "create event \"Part-time job " +
            "interview\" from 2024-06-15T14:00 to 2024-06-15T15:00\nexit\n"), parser);
    controller.run();
    assertEquals(1, this.manager.getActiveCalendar().getEvents().size());
  }

  @Test
  public void testTwoEvents() {
    controller = new InteractiveController(view, createReader("create calendar --name cal1 " +
            "--timezone America/New_York\n" +
            "use calendar --name cal1\n" +
            "create event \"Dinner with roommates\" from 2024-08-23T18:30 " +
            "to 2024-08-23T20:00\ncreate event \"Research meeting with advisor\" from " +
            "2024-11-08T13:30 to 2024-11-08T14:30\nexit\n"), parser);
    controller.run();
    assertEquals(2, this.manager.getActiveCalendar().getEvents().size());
  }

  @Test
  public void testMixedCommands() {
    controller = new InteractiveController(view, createReader("create calendar --name cal1" +
            " --timezone America/New_York\n" +
            "use calendar --name cal1\n" +
            "create event \"Study group for midterms\" " +
            "from 2023-10-18T19:00 to 2023-10-18T22:00\ncancel \"Gym session\"\nview my calendar " +
            "for next week\ncreate event \"Research meeting with advisor\" from 2024-11-08T13:30 " +
            "to 2024-11-08T14:30 repeats F for 3 times\nexit\n"), parser);
    controller.run();
    assertEquals(4, this.manager.getActiveCalendar().getEvents().size());
    assertEquals(true, output.toString().contains("Error"));
  }
}
package calendar.view;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import calendar.model.Event;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents testing for CalendarTextView class.
 */
public class CalendarTextViewTest {

  private CalendarTextView view;
  private ByteArrayOutputStream output;
  private List<Event> eventList;
  private List<Event> emptyList;

  @Before
  public void setUp() {
    this.output = new ByteArrayOutputStream();
    this.view = new CalendarTextView(new PrintStream(this.output));

    LocalDateTime start = LocalDateTime.of(2024, 1, 23, 14, 0);
    LocalDateTime end = LocalDateTime.of(2024, 1, 23, 14, 30);
    Event testEvent = new Event("Call financial aid office", start, end);

    this.eventList = Arrays.asList(testEvent);
    this.emptyList = new ArrayList<>();
  }

  @Test
  public void testRenderSingleEvent() {
    this.view.renderEvents(this.eventList);
    String result = this.output.toString();

    assertEquals(true, result.contains("Call financial aid office"));
    assertEquals(true, result.contains("14:00"));
    assertEquals(true, result.contains("14:30"));
  }

  @Test
  public void testRenderEmptyEventList() {
    this.view.renderEvents(this.emptyList);
    String result = this.output.toString();

    assertEquals(true, result.contains("No events found"));
  }

  @Test
  public void testErrorMessageShows() {
    this.view.renderError("Something went wrong");
    String result = this.output.toString();

    assertEquals(true, result.contains("Error"));
    assertEquals(true, result.contains("Something went wrong"));
  }

  @Test
  public void testBasicMessageWorks() {
    this.view.renderMessage("Event created successfully");
    String result = this.output.toString();

    assertEquals(true, result.contains("Event created successfully"));
  }

  @Test
  public void testMultipleEventsDisplay() {
    LocalDateTime start1 = LocalDateTime.of(2024, 3, 15, 10,
            0);
    LocalDateTime end1 = LocalDateTime.of(2024, 3, 15, 11, 0);
    Event meeting = new Event("Team meeting", start1, end1);

    LocalDateTime start2 = LocalDateTime.of(2024, 3, 15, 14,
            0);
    LocalDateTime end2 = LocalDateTime.of(2024, 3, 15, 15,
            0);
    Event lunch = new Event("Lunch break", start2, end2);

    List<Event> multipleEvents = Arrays.asList(meeting, lunch);
    this.view.renderEvents(multipleEvents);
    String result = this.output.toString();

    assertEquals(true, result.contains("Team meeting"));
    assertEquals(true, result.contains("Lunch break"));
    assertEquals(true, result.contains("10:00"));
    assertEquals(true, result.contains("14:00"));
  }

  @Test
  public void testBusyStatusWorks() {
    LocalDateTime testTime = LocalDateTime.of(2024, 3, 15, 14,
            30);
    this.view.renderBusyStatus(true, testTime);
    String result = this.output.toString();

    assertEquals(true, result.contains("busy"));
  }

  @Test
  public void testAvailableStatusWorks() {
    LocalDateTime testTime = LocalDateTime.of(2024, 3, 15, 14,
            30);
    this.view.renderBusyStatus(false, testTime);
    String result = this.output.toString();

    assertEquals(true, result.contains("available"));
  }

  @Test
  public void testAllDayEventDisplay() {
    LocalDate date = LocalDate.of(2024, 7, 4);
    Event allDayEvent = new Event("Independence Day", date);
    List<Event> allDayList = Arrays.asList(allDayEvent);

    this.view.renderEvents(allDayList);
    String result = this.output.toString();

    assertEquals(true, result.contains("Independence Day"));
    assertEquals(true, result.contains("08:00"));
    assertEquals(true, result.contains("17:00"));
  }

  @Test
  public void testLongEventNameWorks() {
    LocalDateTime start = LocalDateTime.of(2024, 5, 1, 9, 0);
    LocalDateTime end = LocalDateTime.of(2024, 5, 1, 10, 0);
    Event longNameEvent = new Event("Really really long event name that goes on and on",
            start, end);
    List<Event> longEventList = Arrays.asList(longNameEvent);

    this.view.renderEvents(longEventList);
    String result = this.output.toString();

    assertEquals(true, result.contains("Really really long event name"));
  }

  @Test
  public void testSpecialCharactersInEventName() {
    LocalDateTime start = LocalDateTime.of(2024, 6, 1, 15, 0);
    LocalDateTime end = LocalDateTime.of(2024, 6, 1, 16, 0);
    Event specialEvent = new Event("Dr. Smith's Meeting @ Room #123 (urgent!)", start, end);
    List<Event> specialList = Arrays.asList(specialEvent);

    this.view.renderEvents(specialList);
    String result = this.output.toString();

    assertEquals(true, result.contains("Dr. Smith's Meeting"));
    assertEquals(true, result.contains("@"));
    assertEquals(true, result.contains("#123"));
  }

  @Test
  public void testEmptyErrorMessage() {
    this.view.renderError("");
    String result = this.output.toString();

    assertEquals(true, result.contains("Error"));
  }
}
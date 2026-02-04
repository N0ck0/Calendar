package calendar.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import calendar.controller.commands.CommandExecutionException;

/**
 * Represents testing for CalendarModelImpl class.
 */
public class CalendarModelImplTest {

  private CalendarModel model;
  private Event testEvent;
  private Event differentEvent;

  @Before
  public void setUp() {
    this.model = new CalendarModelImpl("cal1", ZoneId.of("America/New_York"));
    LocalDateTime start = LocalDateTime.of(2023, 9, 28, 23, 59);
    LocalDateTime end = LocalDateTime.of(2023, 9, 28, 23, 59);
    this.testEvent = new Event("Philosophy Essay Due", start, end);

    LocalDateTime start2 = LocalDateTime.of(2024, 6, 15, 14, 0);
    LocalDateTime end2 = LocalDateTime.of(2024, 6, 15, 15, 0);
    this.differentEvent = new Event("Part-time job interview", start2, end2);
  }

  @Test
  public void testAddSingleEvent() throws CommandExecutionException {
    this.model.addEvent(this.testEvent);
    List<Event> events = this.model.getEvents();
    assertEquals(1, events.size());
    assertEquals("Philosophy Essay Due", events.get(0).getSubject());
  }

  @Test
  public void testAddMultipleEvents() throws CommandExecutionException {
    this.model.addEvent(this.testEvent);
    this.model.addEvent(this.differentEvent);

    List<Event> events = this.model.getEvents();
    assertEquals(2, events.size());
    assertEquals("Philosophy Essay Due", events.get(0).getSubject());
    assertEquals("Part-time job interview", events.get(1).getSubject());
  }

  // test from self eval assignment 5
  @Test
  public void testEventsShowCorrectTimesAfterTimezoneChange() throws CommandExecutionException {

    Event originalEvent = new Event("Important Meeting",
            LocalDateTime.of(2024, 3, 15, 15, 0),
            LocalDateTime.of(2024, 3, 15, 16, 0));
    this.model.addEvent(originalEvent);
    this.model.updateZoneId("America/Los_Angeles");
    Event convertedEvent = this.model.getEvents().get(0);
    assertEquals("Event should be converted to 12 PM PST",
            LocalDateTime.of(2024, 3, 15, 12, 0),
            convertedEvent.getStartDateTime());
  }
}
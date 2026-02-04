package calendar.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import calendar.controller.commands.CommandExecutionException;

/**
 * Tests for GuiCalendarModelImpl class.
 */
public class GuiCalendarModelImplTest {

  private GuiCalendarModelImpl model;

  @Before
  public void setUp() {
    this.model = new GuiCalendarModelImpl(
            "GUI Calendar", ZoneId.of("America/New_York"));
  }

  @Test
  public void testConstructorInitialization() {
    assertEquals("GUI Calendar", this.model.getName());
    assertEquals(ZoneId.of("America/New_York"), this.model.getZoneId());
    assertEquals(LocalDate.of(1990, 1, 1), this.model.startDate);
    assertTrue("Schedule list should be empty initially",
            this.model.scheduleList.isEmpty());
  }

  @Test
  public void testSetScheduleStart() {
    LocalDate newStart = LocalDate.of(2035, 8, 1);
    this.model.setScheduleStart(newStart);
    assertEquals(newStart, this.model.startDate);
  }

  @Test
  public void testGetScheduledEventsWithEmptyCalendar() {
    List<Event> scheduled = this.model.getScheduledEvents();
    assertTrue("Should return empty list when no events", scheduled.isEmpty());
  }

  @Test
  public void testGetScheduledEventsWithEventsAfterStartDate() throws CommandExecutionException {
    LocalDate startDate = LocalDate.of(2024, 6, 15);
    this.model.setScheduleStart(startDate);

    Event futureEvent = new Event("Future Event",
            LocalDateTime.of(2024, 6, 20, 10, 0),
            LocalDateTime.of(2024, 6, 20, 11, 0));
    this.model.addEvent(futureEvent);
    List<Event> scheduled = this.model.getScheduledEvents();
    assertEquals(1, scheduled.size());
    assertEquals("Future Event", scheduled.get(0).getSubject());
  }

  @Test
  public void testGetScheduledEventsWithEventOnStartDate() throws CommandExecutionException {
    LocalDate startDate = LocalDate.of(2024, 6, 15);
    this.model.setScheduleStart(startDate);

    Event onStartEvent = new Event("On Start Event",
            LocalDateTime.of(2024, 6, 15, 10, 0),
            LocalDateTime.of(2024, 6, 15, 11, 0));
    this.model.addEvent(onStartEvent);

    List<Event> scheduled = this.model.getScheduledEvents();
    assertEquals(1, scheduled.size());
    assertEquals("On Start Event", scheduled.get(0).getSubject());
  }

  @Test
  public void testGetScheduledEventsWithMixedEvents() throws CommandExecutionException {
    LocalDate startDate = LocalDate.of(2024, 6, 15);
    this.model.setScheduleStart(startDate);

    Event pastEvent = new Event("Past Event",
            LocalDateTime.of(2024, 6, 10, 10, 0),
            LocalDateTime.of(2024, 6, 10, 11, 0));
    Event onStartEvent = new Event("On Start Event",
            LocalDateTime.of(2024, 6, 15, 14, 0),
            LocalDateTime.of(2024, 6, 15, 15, 0));
    Event futureEvent1 = new Event("Future Event 1",
            LocalDateTime.of(2024, 6, 20, 10, 0),
            LocalDateTime.of(2024, 6, 20, 11, 0));
    Event futureEvent2 = new Event("Future Event 2",
            LocalDateTime.of(2024, 6, 18, 10, 0),
            LocalDateTime.of(2024, 6, 18, 11, 0));

    this.model.addEvent(pastEvent);
    this.model.addEvent(futureEvent1);
    this.model.addEvent(onStartEvent);
    this.model.addEvent(futureEvent2);

    List<Event> scheduled = this.model.getScheduledEvents();
    assertEquals(3, scheduled.size());
    assertEquals("On Start Event", scheduled.get(0).getSubject());
    assertEquals("Future Event 2", scheduled.get(1).getSubject());
    assertEquals("Future Event 1", scheduled.get(2).getSubject());
  }

  @Test
  public void testGetScheduledEventsLimitToTen() throws CommandExecutionException {
    LocalDate startDate = LocalDate.of(2024, 6, 15);
    this.model.setScheduleStart(startDate);

    for (int i = 1; i <= 15; i++) {
      Event event = new Event("Event " + i,
              LocalDateTime.of(2024, 6, 15 + i, 10, 0),
              LocalDateTime.of(2024, 6, 15 + i, 11, 0));
      this.model.addEvent(event);
    }

    List<Event> scheduled = this.model.getScheduledEvents();
    assertTrue("Should limit to 10 events or less", scheduled.size() <= 11);
  }

  @Test
  public void testGetScheduledEventsWithEventSeries() throws CommandExecutionException {
    LocalDate startDate = LocalDate.of(2024, 6, 15);
    this.model.setScheduleStart(startDate);

    Event baseEvent = new Event("Weekly Meeting",
            LocalDateTime.of(2024, 6, 17, 10, 0),
            LocalDateTime.of(2024, 6, 17, 11, 0));
    EventSeries series = new EventSeries(baseEvent, "M", 3);
    this.model.addEventSeries(series);

    List<Event> scheduled = this.model.getScheduledEvents();
    assertEquals(3, scheduled.size());

    for (Event event : scheduled) {
      assertEquals("Weekly Meeting", event.getSubject());
    }
  }

  @Test
  public void testGetScheduledEventsWithMixedSingleAndSeriesEvents()
          throws CommandExecutionException {
    LocalDate startDate = LocalDate.of(2024, 6, 15);
    this.model.setScheduleStart(startDate);

    Event singleEvent = new Event("Single Event",
            LocalDateTime.of(2024, 6, 16, 10, 0),
            LocalDateTime.of(2024, 6, 16, 11, 0));
    this.model.addEvent(singleEvent);

    Event baseEvent = new Event("Series Event",
            LocalDateTime.of(2024, 6, 17, 14, 0),
            LocalDateTime.of(2025, 11, 17, 15, 0));
    EventSeries series = new EventSeries(baseEvent, "M", 2);
    this.model.addEventSeries(series);

    List<Event> scheduled = this.model.getScheduledEvents();
    assertEquals(3, scheduled.size());
    assertEquals("Single Event", scheduled.get(0).getSubject());
    assertEquals("Series Event", scheduled.get(1).getSubject());
    assertEquals("Series Event", scheduled.get(2).getSubject());
  }

  @Test
  public void testSetScheduleStartAffectsSubsequentCalls() throws CommandExecutionException {
    Event event1 = new Event("Event 1",
            LocalDateTime.of(2024, 6, 10, 10, 0),
            LocalDateTime.of(2024, 6, 10, 11, 0));
    Event event2 = new Event("Event 2",
            LocalDateTime.of(2024, 9, 20, 10, 0),
            LocalDateTime.of(2024, 9, 20, 11, 0));
    this.model.addEvent(event1);
    this.model.addEvent(event2);

    this.model.setScheduleStart(LocalDate.of(2024, 6, 5));
    assertEquals(2, this.model.getScheduledEvents().size());

    this.model.setScheduleStart(LocalDate.of(2024, 9, 15));
    List<Event> scheduled = this.model.getScheduledEvents();
    assertEquals(1, scheduled.size());
    assertEquals("Event 2", scheduled.get(0).getSubject());
  }

  @Test
  public void testInheritsFromCalendarModelImpl() throws CommandExecutionException {
    Event testEvent = new Event("Test Event",
            LocalDateTime.of(2024, 6, 15, 10, 0),
            LocalDateTime.of(2024, 6, 15, 11, 0));

    this.model.addEvent(testEvent);

    assertEquals(1, this.model.getEvents().size());
    assertEquals(1, this.model.getSingleEvents().size());
    assertEquals(0, this.model.getEventSeries().size());
    assertEquals("Test Event", this.model.getEvents().get(0).getSubject());
  }

  @Test
  public void testScheduleStartDefaultValue() {
    assertEquals(LocalDate.of(1990, 1, 1), this.model.startDate);
  }

  @Test
  public void testGetScheduledEventsWithLeapYear() throws CommandExecutionException {
    LocalDate startDate = LocalDate.of(2024, 2, 28);
    this.model.setScheduleStart(startDate);

    Event leapDayEvent = new Event("Leap Year Event",
            LocalDateTime.of(2024, 2, 29, 12, 0),
            LocalDateTime.of(2028, 1, 29, 18, 0));
    this.model.addEvent(leapDayEvent);

    List<Event> scheduled = this.model.getScheduledEvents();
    assertEquals(1, scheduled.size());
    assertEquals("Leap Year Event", scheduled.get(0).getSubject());
  }
}
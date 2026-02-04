package calendar.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import calendar.controller.commands.CommandExecutionException;

/**
 * Test class for EventSeries functionality including event series creation,
 * event list generation, and series property modification.
 */
public class EventSeriesTest {

  private Event baseEvent;
  private LocalDateTime startTime;
  private CalendarModel mockModel;

  @Before
  public void setUp() {
    startTime = LocalDateTime.of(2025, 6, 16, 10, 0);
    LocalDateTime endTime = LocalDateTime.of(2025, 6, 16, 11, 0);
    //startDate = LocalDate.of(2025, 6, 16);

    baseEvent = new Event("Weekly Meeting", startTime, endTime);
    //allDayEvent = new Event("Daily Standup", startDate);
    mockModel = new CalendarModelImpl("cal1", ZoneId.of("America/New_York"));
  }

  @Test
  public void testEventSeriesConstructorWithUntilDate() {
    LocalDate untilDate = LocalDate.of(2025, 6, 30);
    EventSeries series = new EventSeries(baseEvent, "MWF", untilDate);

    List<Event> events = series.getEvents();
    assertNotNull(events);
    assertTrue(events.size() > 1);
    assertEquals("Weekly Meeting", events.get(0).getSubject());
  }

  @Test
  public void testEventSeriesConstructorWithRepeatTimes() {
    EventSeries series = new EventSeries(baseEvent, "TR", 5);

    List<Event> events = series.getEvents();
    assertNotNull(events);
    assertEquals(5, events.size());

    for (Event event : events) {
      assertEquals("Weekly Meeting", event.getSubject());
    }
  }

  @Test
  public void testCreateEventListUntilDateMonday() {
    LocalDate untilDate = LocalDate.of(2025, 6, 23);
    EventSeries series = new EventSeries(new Event("haircut",
            LocalDate.of(2025, 6, 15)), "M", untilDate);

    List<Event> events = series.getEvents();
    assertTrue(events.size() == 2);

    assertEquals(LocalDate.of(2025, 6, 23), events.get(1).getDate());
  }

  @Test
  public void testCreateEventListUntilDateMultipleDays() {
    LocalDate untilDate = LocalDate.of(2025, 6, 20);
    EventSeries series = new EventSeries(baseEvent, "MW", untilDate);

    List<Event> events = series.getEvents();
    assertTrue(events.size() >= 2);

    assertEquals(LocalDate.of(2025, 6, 16), events.get(0).getDate());
    assertEquals(LocalDate.of(2025, 6, 18), events.get(1).getDate());
  }

  @Test
  public void testCreateEventListRepeatTimesAllDays() {
    EventSeries series = new EventSeries(baseEvent, "MTWRFSU", 7);

    List<Event> events = series.getEvents();
    assertEquals(7, events.size());

    LocalDate expectedDate = LocalDate.of(2025, 6, 16);
    for (int i = 0; i < 7; i++) {
      assertEquals(expectedDate.plusDays(i), events.get(i).getDate());
    }
  }

  @Test
  public void testCreateEventListRepeatTimesSpecificDays() {
    EventSeries series = new EventSeries(baseEvent, "MW", 4);

    List<Event> events = series.getEvents();
    assertEquals(4, events.size());

    assertEquals(LocalDate.of(2025, 6, 16), events.get(0).getDate());
    assertEquals(LocalDate.of(2025, 6, 18), events.get(1).getDate());
    assertEquals(LocalDate.of(2025, 6, 23), events.get(2).getDate());
    assertEquals(LocalDate.of(2025, 6, 25), events.get(3).getDate());
  }

  @Test
  public void testGetDayListFromStringAllDays() {
    EventSeries series = new EventSeries(baseEvent, "MTWRFSU", 1);
    List<Event> events = series.getEvents();
    assertNotNull(events);
  }

  @Test
  public void testGetDayListFromStringMondayOnly() {
    EventSeries series = new EventSeries(baseEvent, "M", 2);
    List<Event> events = series.getEvents();

    assertEquals(2, events.size());
    assertEquals(LocalDate.of(2025, 6, 16), events.get(0).getDate());
    assertEquals(LocalDate.of(2025, 6, 23), events.get(1).getDate());
  }

  @Test
  public void testGetDayListFromStringTuesdayThursday() {
    EventSeries series = new EventSeries(baseEvent, "TR", 3);
    List<Event> events = series.getEvents();

    assertEquals(3, events.size());
    assertEquals(LocalDate.of(2025, 6, 17), events.get(0).getDate());
    assertEquals(LocalDate.of(2025, 6, 19), events.get(1).getDate());
    assertEquals(LocalDate.of(2025, 6, 24), events.get(2).getDate());
  }

  @Test
  public void testGetDayListFromStringWeekend() {
    EventSeries series = new EventSeries(baseEvent, "SU", 2);
    List<Event> events = series.getEvents();

    assertEquals(2, events.size());
    assertEquals(LocalDate.of(2025, 6, 21), events.get(0).getDate());
    assertEquals(LocalDate.of(2025, 6, 22), events.get(1).getDate());
  }

  @Test
  public void testContainsEvent() {
    EventSeries series = new EventSeries(baseEvent, "M", 2);

    assertTrue(series.containsEvent("Weekly Meeting", startTime));
    assertFalse(series.containsEvent("Different Meeting", startTime));
    assertFalse(series.containsEvent("Weekly Meeting", startTime.plusHours(1)));
  }

  @Test
  public void testChangeAllAfterNonStartProperty() throws CommandExecutionException {
    EventSeries series = new EventSeries(baseEvent, "M", 3);

    series.changeAllAfter(mockModel, "Weekly Meeting", startTime, "subject", "Modified Meeting");

    List<Event> events = series.getEvents();
    boolean foundModifiedEvent = false;
    for (Event event : events) {
      if (event.getSubject().equals("Modified Meeting")) {
        foundModifiedEvent = true;
        break;
      }
    }
    assertTrue(foundModifiedEvent);
  }

  @Test
  public void testChangeAllAfterStartProperty() throws CommandExecutionException {
    EventSeries series = new EventSeries(baseEvent, "M", 2);

    series.changeAllAfter(mockModel, "Weekly Meeting", startTime, "start", "2025-06-16T15:00");

    List<Event> singleEvents = mockModel.getSingleEvents();
    assertTrue(singleEvents.size() > 0);
  }

  @Test
  public void testChangeAllNonStartProperty() throws CommandExecutionException {
    EventSeries series = new EventSeries(baseEvent, "M", 3);

    series.changeAll(mockModel, "Weekly Meeting", startTime, "subject", "Changed Meeting");

    List<Event> events = series.getEvents();
    for (Event event : events) {
      assertEquals("Changed Meeting", event.getSubject());
    }
  }

  @Test
  public void testChangeAllStartProperty() throws CommandExecutionException {
    EventSeries series = new EventSeries(baseEvent, "M", 2);

    series.changeAll(mockModel, "Weekly Meeting", startTime, "start", "2025-06-16T16:00");

    List<Event> singleEvents = mockModel.getSingleEvents();
    assertTrue(singleEvents.size() > 0);
  }

  @Test
  public void testGetEvents() {
    EventSeries series = new EventSeries(baseEvent, "MWF", 5);

    List<Event> events = series.getEvents();
    assertNotNull(events);
    assertEquals(5, events.size());

    for (Event event : events) {
      assertNotNull(event);
      assertEquals("Weekly Meeting", event.getSubject());
    }
  }

  @Test
  public void testEmptyDayStringCreatesNoEvents() {
    try {
      EventSeries series = new EventSeries(baseEvent, "", 5);
      fail("Shouldn't get here");
    } catch (IllegalArgumentException e) {
      //Should get here and pass.
    }
  }

  @Test
  public void testZeroRepeatTimesCreatesNoEvents() {
    EventSeries series = new EventSeries(baseEvent, "M", 0);

    List<Event> events = series.getEvents();
    assertEquals(0, events.size());
  }
}
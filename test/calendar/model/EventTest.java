package calendar.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.io.StringWriter;
import java.io.IOException;
import calendar.controller.commands.CommandExecutionException;

/**
 * Test class for Event functionality including constructors, property modification,
 * conflict detection, equality, and string representation.
 */
public class EventTest {

  private Event timedEvent;
  private Event allDayEvent;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private LocalDate eventDate;

  @Before
  public void setUp() {
    startTime = LocalDateTime.of(2025, 6, 15, 10, 30);
    endTime = LocalDateTime.of(2025, 6, 15, 12, 0);
    eventDate = LocalDate.of(2025, 6, 15);

    timedEvent = new Event("Team Meeting", startTime, endTime);
    allDayEvent = new Event("Conference", eventDate);
  }

  @Test
  public void testTimedEventConstructor() {
    assertEquals("Team Meeting", timedEvent.getSubject());
    assertEquals(startTime, timedEvent.getStartDateTime());
    assertEquals(endTime, timedEvent.getEndDateTime());
  }

  @Test
  public void testAllDayEventConstructor() {
    assertEquals("Conference", allDayEvent.getSubject());
    assertEquals(LocalDateTime.of(eventDate, LocalTime.parse("08:00")),
            allDayEvent.getStartDateTime());
    assertEquals(LocalDateTime.of(eventDate, LocalTime.parse("17:00")),
            allDayEvent.getEndDateTime());
  }

  @Test
  public void testGetDate() {
    assertEquals(eventDate, timedEvent.getDate());
    assertEquals(eventDate, allDayEvent.getDate());
  }

  @Test
  public void testOnThisDate() {
    LocalDateTime newDate = LocalDateTime.of(2025, 7, 1, 14, 0);
    Event newEvent = timedEvent.onThisDate(newDate);

    assertEquals("Team Meeting", newEvent.getSubject());
    assertEquals(LocalDateTime.of(2025, 7, 1, 10, 30), newEvent.getStartDateTime());
    assertEquals(LocalDateTime.of(2025, 7, 1, 12, 0), newEvent.getEndDateTime());
  }

  @Test
  public void testModifyPropertySubject() throws CommandExecutionException {
    timedEvent.modifyProperty("subject", "New Meeting Title");
    assertEquals("New Meeting Title", timedEvent.getSubject());
  }

  @Test
  public void testModifyPropertyStart() throws CommandExecutionException {
    String newStart = "2025-06-15T11:00";
    timedEvent.modifyProperty("start", newStart);
    assertEquals(LocalDateTime.parse(newStart), timedEvent.getStartDateTime());
  }

  @Test
  public void testModifyPropertyEnd() throws CommandExecutionException {
    String newEnd = "2025-06-15T13:00";
    timedEvent.modifyProperty("end", newEnd);
    assertEquals(LocalDateTime.parse(newEnd), timedEvent.getEndDateTime());
  }


  @Test(expected = CommandExecutionException.class)
  public void testModifyPropertyInvalidProperty() throws CommandExecutionException {
    timedEvent.modifyProperty("invalid", "value");
  }

  @Test(expected = CommandExecutionException.class)
  public void testModifyPropertyInvalidDate() throws CommandExecutionException {
    timedEvent.modifyProperty("start", "invalid-date");
  }

  @Test
  public void testConflictsWith() {
    Event conflictingEvent = new Event("Team Meeting", startTime, endTime);
    Event nonConflictingEvent = new Event("Different Meeting", startTime, endTime);
    Event differentTimeEvent = new Event("Team Meeting",
            startTime.plusHours(1), endTime.plusHours(1));

    assertTrue(timedEvent.conflictsWith(conflictingEvent));
    assertFalse(timedEvent.conflictsWith(nonConflictingEvent));
    assertFalse(timedEvent.conflictsWith(differentTimeEvent));
  }

  @Test
  public void testEquals() {
    Event identicalEvent = new Event("Team Meeting", startTime, endTime);
    Event differentEvent = new Event("Other Meeting", startTime, endTime);

    assertTrue(timedEvent.equals(identicalEvent));
    assertFalse(timedEvent.equals(differentEvent));
    assertFalse(timedEvent.equals("not an event"));
  }

  @Test
  public void testHashCode() {
    Event identicalEvent = new Event("Team Meeting", startTime, endTime);
    assertEquals(timedEvent.hashCode(), identicalEvent.hashCode());
  }

  @Test
  public void testOccursDuring() {
    LocalDateTime during = LocalDateTime.of(2025, 6, 15, 11, 0);
    LocalDateTime before = LocalDateTime.of(2025, 6, 15, 9, 0);
    LocalDateTime after = LocalDateTime.of(2025, 6, 15, 13, 0);
    LocalDateTime atStart = startTime;
    LocalDateTime atEnd = endTime;

    assertTrue(timedEvent.occursDuring(during));
    assertTrue(timedEvent.occursDuring(atStart));
    assertTrue(timedEvent.occursDuring(atEnd));
    assertFalse(timedEvent.occursDuring(before));
    assertFalse(timedEvent.occursDuring(after));
  }

  @Test
  public void testToStringWithTimedEvent() throws IOException, CommandExecutionException {
    Event eventWithLocation = new Event("Meeting", startTime, endTime);
    eventWithLocation.modifyProperty("location", "Room 101");

    StringWriter writer = new StringWriter();
    eventWithLocation.toString(writer);

    String result = writer.toString();
    assertTrue(result.contains("Meeting"));
    assertTrue(result.contains("10:30"));
    assertTrue(result.contains("12:00"));
    assertTrue(result.contains("2025-06-15"));
    assertTrue(result.contains("Room 101"));
  }

  @Test
  public void testToStringWithoutLocation() throws IOException {
    StringWriter writer = new StringWriter();
    timedEvent.toString(writer);

    String result = writer.toString();
    assertTrue(result.contains("Team Meeting"));
    assertFalse(result.contains(" at "));
  }
}
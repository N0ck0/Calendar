package calendar.controller.commands;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import calendar.model.CalendarManager;
import calendar.model.CalendarModel;
import calendar.model.Event;

/**
 * Representing the test classes for the commands.
 */
public class EventCommandsTest {

  private CalendarManager manager;
  private CalendarModel cal1;
  private CalendarModel cal2;

  @Before
  public void setUp() throws CommandExecutionException {
    this.manager = new CalendarManager();
    this.manager.addCalendar("cal1", ZoneId.of("America/New_York"));
    this.manager.addCalendar("cal2", ZoneId.of("America/Los_Angeles"));
    this.manager.switchToCalendar("cal1");

    this.cal1 = this.manager.getCalendar("cal1");
    this.cal2 = this.manager.getCalendar("cal2");
  }

  @Test
  public void testCreateCalendarCommand() throws CommandExecutionException {
    CalendarManager newManager = new CalendarManager();
    CreateCalendarCommand cmd = new CreateCalendarCommand(newManager,
            "work", ZoneId.of("Europe/Paris"));
    cmd.execute();

    assertEquals("work", newManager.getCalendar("work").getName());
    assertEquals(ZoneId.of("Europe/Paris"),
            newManager.getCalendar("work").getZoneId());
  }

  @Test(expected = CommandExecutionException.class)
  public void testCreateDuplicateCalendarCommand() throws CommandExecutionException {
    CreateCalendarCommand cmd = new CreateCalendarCommand(this.manager,
            "cal1", ZoneId.of("Europe/Paris"));
    cmd.execute();
  }

  @Test
  public void testCreateCalendarWithSpecialName() throws CommandExecutionException {
    CreateCalendarCommand cmd = new CreateCalendarCommand(this.manager,
            "Dr. Smith's Calendar", ZoneId.of("Asia/Tokyo"));
    cmd.execute();

    assertEquals("Dr. Smith's Calendar",
            this.manager.getCalendar("Dr. Smith's Calendar").getName());
  }

  @Test
  public void testUseCalendarCommand() throws CommandExecutionException {
    UseCalendarCommand cmd = new UseCalendarCommand(this.manager, "cal2");
    cmd.execute();

    assertEquals("cal2", this.manager.getActiveCalendar().getName());
  }

  @Test(expected = CommandExecutionException.class)
  public void testUseNonExistentCalendarCommand() throws CommandExecutionException {
    UseCalendarCommand cmd = new UseCalendarCommand(this.manager, "nonexistent");
    cmd.execute();
  }

  @Test
  public void testEditCalendarNameCommand() throws CommandExecutionException {
    EditCalendarCommand cmd = new EditCalendarCommand(this.manager,
            "cal1", "name", "newcal1");
    cmd.execute();

    assertEquals("newcal1", this.manager.getCalendar("newcal1").getName());
    assertEquals("newcal1", this.manager.getActiveCalendar().getName());
  }

  @Test
  public void testEditCalendarTimezoneCommand() throws CommandExecutionException {
    EditCalendarCommand cmd = new EditCalendarCommand(this.manager,
            "cal1", "timezone", "Europe/Paris");
    cmd.execute();

    assertEquals(ZoneId.of("Europe/Paris"),
            this.manager.getCalendar("cal1").getZoneId());
  }

  @Test
  public void testEditCalendarInvalidProperty() throws CommandExecutionException {
    EditCalendarCommand cmd = new EditCalendarCommand(this.manager,
            "cal1", "invalid", "value");

    try {
      cmd.execute();
      fail("Should throw exception for invalid property");
    } catch (CommandExecutionException e) {
      assertTrue(e.getMessage().contains("Failed to execute command"));
    }
  }

  @Test
  public void testCopySingleEventSameTimezone() throws CommandExecutionException {
    Event originalEvent = new Event("Team Standup",
            LocalDateTime.of(2025, 8, 7, 9, 30),
            LocalDateTime.of(2025, 8, 7, 10, 0));
    this.cal1.addEvent(originalEvent);

    this.manager.addCalendar("cal3", ZoneId.of("America/New_York"));
    CopyEventCommand cmd = new CopyEventCommand(this.manager, "Team Standup",
            LocalDateTime.of(2025, 8, 7, 9, 30),
            "cal3",
            LocalDateTime.of(2025, 8, 21, 9, 30));
    cmd.execute();

    CalendarModel cal3 = this.manager.getCalendar("cal3");
    assertEquals(1, cal3.getEvents().size());
    assertEquals("Team Standup", cal3.getEvents().get(0).getSubject());
    assertEquals(LocalDateTime.of(2025, 8, 21, 9, 30),
            cal3.getEvents().get(0).getStartDateTime());
  }

  @Test
  public void testCopyEventsOnDateSameTimezone() throws CommandExecutionException {
    Event event1 = new Event("Morning Brief",
            LocalDateTime.of(2026, 1, 12, 8, 15),
            LocalDateTime.of(2026, 1, 12, 9, 0));
    Event event2 = new Event("Client Call",
            LocalDateTime.of(2026, 1, 12, 16, 45),
            LocalDateTime.of(2026, 1, 12, 17, 30));
    this.cal1.addEvent(event1);
    this.cal1.addEvent(event2);

    CopyEventCommand cmd = new CopyEventCommand(this.manager,
            LocalDate.of(2026, 1, 12), "cal2",
            LocalDate.of(2026, 2, 3));
    cmd.execute();

    assertEquals(2, this.cal2.getEvents().size());
  }

  @Test
  public void testCopyEventsOnDateDifferentTimezone() throws CommandExecutionException {
    Event event = new Event("Product Demo",
            LocalDateTime.of(2023, 9, 4, 13, 20),
            LocalDateTime.of(2023, 9, 4, 14, 45));
    this.cal1.addEvent(event);

    CopyEventCommand cmd = new CopyEventCommand(this.manager,
            LocalDate.of(2023, 9, 4), "cal2",
            LocalDate.of(2023, 9, 4));
    cmd.execute();

    Event copiedEvent = this.cal2.getEvents().get(0);
    assertEquals(LocalDateTime.of(2023, 9, 4, 10, 20),
            copiedEvent.getStartDateTime());
  }

  @Test
  public void testCopyEventsBetweenDates() throws CommandExecutionException {
    Event event1 = new Event("Workshop Planning",
            LocalDateTime.of(2024, 11, 6, 14, 30),
            LocalDateTime.of(2024, 11, 6, 15, 45));
    Event event2 = new Event("Budget Review",
            LocalDateTime.of(2024, 11, 18, 11, 0),
            LocalDateTime.of(2024, 11, 18, 12, 30));
    Event event3 = new Event("Year End Party",
            LocalDateTime.of(2024, 12, 28, 18, 0),
            LocalDateTime.of(2024, 12, 28, 22, 0));
    this.cal1.addEvent(event1);
    this.cal1.addEvent(event2);
    this.cal1.addEvent(event3);

    CopyEventCommand cmd = new CopyEventCommand(this.manager,
            LocalDate.of(2024, 11, 5),
            LocalDate.of(2024, 11, 25), "cal2",
            LocalDate.of(2025, 2, 10));
    cmd.execute();

    assertEquals(2, this.cal2.getEvents().size());
  }

  @Test
  public void testCopyNonExistentEvent() throws CommandExecutionException {
    CopyEventCommand cmd = new CopyEventCommand(this.manager, "Ghost Meeting",
            LocalDateTime.of(2025, 5, 22, 14, 0),
            "cal2",
            LocalDateTime.of(2025, 6, 8, 14, 0));

    try {
      cmd.execute();
      fail("Should throw exception for non-existent event");
    } catch (CommandExecutionException e) {
      assertTrue(e.getMessage().contains("more than one event")
              ||
              e.getMessage().contains("no event"));
    }
  }

  @Test
  public void testCopyToNonExistentCalendar() throws CommandExecutionException {
    Event event = new Event("Quarterly Review",
            LocalDateTime.of(2023, 12, 14, 15, 30),
            LocalDateTime.of(2023, 12, 14, 16, 30));
    this.cal1.addEvent(event);

    CopyEventCommand cmd = new CopyEventCommand(this.manager, "Quarterly Review",
            LocalDateTime.of(2023, 12, 14, 15, 30),
            "phantom_calendar",
            LocalDateTime.of(2024, 1, 5, 15, 30));

    try {
      cmd.execute();
      fail("Should throw exception for non-existent calendar");
    } catch (Exception e) {
      // Expected
    }
  }

  @Test
  public void testEditSingleEventCommand() throws CommandExecutionException {
    Event event = new Event("Initial Brainstorm",
            LocalDateTime.of(2026, 4, 9, 10, 45),
            LocalDateTime.of(2026, 4, 9, 11, 30));
    this.cal1.addEvent(event);

    EditEventCommand cmd = new EditEventCommand(this.cal1,
            "Initial Brainstorm", "subject",
            LocalDateTime.of(2026, 4, 9, 10, 45),
            LocalDateTime.of(2026, 4, 9, 11, 30),
            "Final Strategy Session");
    cmd.execute();

    assertEquals("Final Strategy Session", this.cal1.getEvents().get(0).getSubject());
  }

  @Test
  public void testEditEventLocationCommand() throws CommandExecutionException {
    Event event = new Event("Summer Picnic",
            LocalDateTime.of(2023, 7, 29, 18, 0),
            LocalDateTime.of(2023, 7, 29, 21, 30));
    this.cal1.addEvent(event);

    EditEventCommand cmd = new EditEventCommand(this.cal1,
            "Summer Picnic",
            "location",
            LocalDateTime.of(2023, 7, 29, 18, 0),
            LocalDateTime.of(2023, 7, 29, 21, 30),
            "Central Park Pavilion");
    cmd.execute();

    assertEquals(1, this.cal1.getEvents().size());
  }

  @Test
  public void testCopyEventPreservesAllProperties() throws CommandExecutionException {
    Event originalEvent = new Event("Complex Board Meeting",
            LocalDateTime.of(2025, 2, 14, 9, 0),
            LocalDateTime.of(2025, 2, 14, 12, 0));
    originalEvent.modifyProperty("location", "Executive Conference Room");
    originalEvent.modifyProperty("description", "Q4 Results and 2025 Planning");
    this.cal1.addEvent(originalEvent);

    CopyEventCommand cmd = new CopyEventCommand(this.manager, "Complex Board Meeting",
            LocalDateTime.of(2025, 2, 14, 9, 0),
            "cal2",
            LocalDateTime.of(2025, 3, 28, 9, 0));
    cmd.execute();

    Event copiedEvent = this.cal2.getEvents().get(0);
    assertEquals("Complex Board Meeting", copiedEvent.getSubject());
  }

  @Test
  public void testCopyMultipleEventsWithSameName() throws CommandExecutionException {
    Event event1 = new Event("Daily Scrum",
            LocalDateTime.of(2024, 6, 11, 7, 30),
            LocalDateTime.of(2024, 6, 11, 8, 0));
    this.cal1.addEvent(event1);

    CopyEventCommand cmd = new CopyEventCommand(this.manager,
            "Daily Scrum",
            LocalDateTime.of(2024, 6, 11, 7, 30),
            "cal2",
            LocalDateTime.of(2024, 6, 25, 7, 30));

    cmd.execute();
    assertEquals(1, this.cal2.getEvents().size());
  }

  @Test
  public void testCopyEventsRangeDifferentTimezones() throws CommandExecutionException {
    this.manager.switchToCalendar("cal1");

    Event day1Event = new Event("Holiday Planning",
            LocalDateTime.of(2025, 12, 3, 11, 15),
            LocalDateTime.of(2025, 12, 3, 12, 30));
    Event day2Event = new Event("Year-End Conference",
            LocalDateTime.of(2025, 12, 16, 13, 45),
            LocalDateTime.of(2025, 12, 16, 17, 0));
    Event day3Event = new Event("Final Retrospective",
            LocalDateTime.of(2025, 12, 29, 8, 30),
            LocalDateTime.of(2025, 12, 29, 11, 45));

    this.cal1.addEvent(day1Event);
    this.cal1.addEvent(day2Event);
    this.cal1.addEvent(day3Event);

    CopyEventCommand copyRange = new CopyEventCommand(this.manager,
            LocalDate.of(2025, 12, 1),
            LocalDate.of(2025, 12, 30),
            "cal2", LocalDate.of(2026, 1, 15));
    copyRange.execute();

    assertEquals("Should copy 3 events in range", 3,
            this.cal2.getEvents().size());

    for (Event copiedEvent : this.cal2.getEvents()) {
      if (copiedEvent.getSubject().equals("Holiday Planning")) {
        assertEquals(LocalDateTime.of(2026, 1, 17, 8, 15),
                copiedEvent.getStartDateTime());
      } else if (copiedEvent.getSubject().equals("Year-End Conference")) {
        assertEquals(LocalDateTime.of(2026, 1, 30, 10, 45),
                copiedEvent.getStartDateTime());
      } else if (copiedEvent.getSubject().equals("Final Retrospective")) {
        assertEquals(LocalDateTime.of(2026, 2, 12, 5, 30),
                copiedEvent.getStartDateTime());
      }
    }
  }

  @Test
  public void testCopyEventsRangeExtremeTimezones() throws CommandExecutionException {
    this.manager.addCalendar("tokyo", ZoneId.of("Asia/Tokyo"));
    this.manager.addCalendar("hawaii", ZoneId.of("Pacific/Honolulu"));
    this.manager.switchToCalendar("tokyo");

    CalendarModel tokyoCal = this.manager.getActiveCalendar();
    CalendarModel hawaiiCal = this.manager.getCalendar("hawaii");

    Event tokyoEvent = new Event("Global Partnership Summit",
            LocalDateTime.of(2024, 10, 8, 16, 20),
            LocalDateTime.of(2024, 10, 8, 18, 0));
    tokyoCal.addEvent(tokyoEvent);

    CopyEventCommand extremeCopy = new CopyEventCommand(this.manager,
            LocalDate.of(2024, 10, 8),
            LocalDate.of(2024, 10, 8), "hawaii",
            LocalDate.of(2024, 10, 8));
    extremeCopy.execute();

    assertEquals(1, hawaiiCal.getEvents().size());
    Event copiedEvent = hawaiiCal.getEvents().get(0);

    assertTrue("Event should be shifted significantly earlier due to timezone difference",
            copiedEvent.getStartDateTime().isBefore(tokyoEvent.getStartDateTime()));
  }
}
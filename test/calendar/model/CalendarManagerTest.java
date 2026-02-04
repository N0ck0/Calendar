
package calendar.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.ZoneId;
import calendar.controller.commands.CommandExecutionException;

/**
 * Tests for CalendarManager class.
 */
public class CalendarManagerTest {

  private CalendarManager manager;

  @Before
  public void setUp() {
    this.manager = new CalendarManager();
  }

  @Test
  public void testAddCalendar() throws CommandExecutionException {
    this.manager.addCalendar("Work", ZoneId.of("America/New_York"));
    assertEquals("Work", this.manager.getCalendar("Work").getName());
  }

  @Test(expected = CommandExecutionException.class)
  public void testAddDuplicateCalendar() throws CommandExecutionException {
    this.manager.addCalendar("Work", ZoneId.of("America/New_York"));
    this.manager.addCalendar("Work", ZoneId.of("America/Chicago"));
  }

  @Test
  public void testSwitchToCalendar() throws CommandExecutionException {
    this.manager.addCalendar("Personal", ZoneId.of("America/Los_Angeles"));
    this.manager.switchToCalendar("Personal");
    assertEquals("Personal", this.manager.getActiveCalendar().getName());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSwitchToNonExistentCalendar() {
    this.manager.switchToCalendar("NonExistent");
  }

  @Test
  public void testGetActiveCalendarReturnsNull() {
    assertNull(this.manager.getActiveCalendar());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetNonExistentCalendar() {
    this.manager.getCalendar("NonExistent");
  }

  //tests from self eval 5
  @Test(expected = Exception.class)
  public void testCreateCalendarWithInvalidTimezone() throws CommandExecutionException {
    this.manager.addCalendar("testCal", ZoneId.of("Invalid/Timezone"));
  }

  @Test(expected = CommandExecutionException.class)
  public void testUpdateCalendarWithInvalidTimezone() throws CommandExecutionException {
    this.manager.addCalendar("testCal", ZoneId.of("America/New_York"));
    this.manager.updateTimezone("testCal", "Invalid/BadZone");
  }
}
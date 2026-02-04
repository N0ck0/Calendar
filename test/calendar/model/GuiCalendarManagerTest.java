package calendar.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.ZoneId;

import calendar.controller.commands.CommandExecutionException;

/**
 * Tests for GuiCalendarManager class.
 */
public class GuiCalendarManagerTest {

  private GuiCalendarManager manager;

  @Before
  public void setUp() {
    this.manager = new GuiCalendarManager();
  }

  @Test
  public void testAddCalendarWithTokyoTimezone() throws CommandExecutionException {
    this.manager.addCalendar("Tokyo-Business-Calendar", ZoneId.of("Asia/Tokyo"));

    CalendarModel calendar = this.manager.getCalendar("Tokyo-Business-Calendar");
    assertTrue("Should create GuiCalendarModel instance",
            calendar instanceof GuiCalendarModel);
    assertTrue("Should be instance of GuiCalendarModelImpl",
            calendar instanceof GuiCalendarModelImpl);
    assertEquals(ZoneId.of("Asia/Tokyo"), calendar.getZoneId());
  }

  @Test
  public void testAddCalendarWithSydneyTimezone() throws CommandExecutionException {
    this.manager.addCalendar("Sydney-Family-Events", ZoneId.of("Australia/Sydney"));

    CalendarModel calendar = this.manager.getCalendar("Sydney-Family-Events");
    assertEquals("Sydney-Family-Events", calendar.getName());
    assertEquals(ZoneId.of("Australia/Sydney"), calendar.getZoneId());
    assertTrue("Should be GuiCalendarModel", calendar instanceof GuiCalendarModel);
  }

  @Test
  public void testAddCalendarWithLondonTimezone() throws CommandExecutionException {
    this.manager.addCalendar("London-Conference-Schedule",
            ZoneId.of("Europe/London"));

    CalendarModel calendar = this.manager.getCalendar("London-Conference-Schedule");
    assertEquals("London-Conference-Schedule", calendar.getName());
    assertEquals(ZoneId.of("Europe/London"), calendar.getZoneId());
    assertTrue("Should be GuiCalendarModel", calendar instanceof GuiCalendarModel);
  }

  @Test
  public void testAddMultipleCalendarsWithDifferentTimezones() throws CommandExecutionException {
    this.manager.addCalendar("West-Coast-Work", ZoneId.of("America/Los_Angeles"));
    this.manager.addCalendar("East-Coast-Family", ZoneId.of("America/New_York"));
    this.manager.addCalendar("European-Vacation", ZoneId.of("Europe/Paris"));
    this.manager.addCalendar("Asian-Business", ZoneId.of("Asia/Singapore"));
    this.manager.addCalendar("Mountain-Adventures", ZoneId.of("America/Denver"));

    assertEquals(5, this.manager.getCalendars().size());

    CalendarModel westCoast = this.manager.getCalendar("West-Coast-Work");
    CalendarModel eastCoast = this.manager.getCalendar("East-Coast-Family");
    CalendarModel european = this.manager.getCalendar("European-Vacation");
    CalendarModel asian = this.manager.getCalendar("Asian-Business");
    CalendarModel mountain = this.manager.getCalendar("Mountain-Adventures");

    //googled instance of checking
    assertTrue("West Coast should be GuiCalendarModel",
            westCoast instanceof GuiCalendarModel);
    assertTrue("East Coast should be GuiCalendarModel",
            eastCoast instanceof GuiCalendarModel);
    assertTrue("European should be GuiCalendarModel",
            european instanceof GuiCalendarModel);
    assertTrue("Asian should be GuiCalendarModel",
            asian instanceof GuiCalendarModel);
    assertTrue("Mountain should be GuiCalendarModel",
            mountain instanceof GuiCalendarModel);
    assertEquals(ZoneId.of("America/Los_Angeles"), westCoast.getZoneId());
    assertEquals(ZoneId.of("America/New_York"), eastCoast.getZoneId());
    assertEquals(ZoneId.of("Europe/Paris"), european.getZoneId());
    assertEquals(ZoneId.of("Asia/Singapore"), asian.getZoneId());
    assertEquals(ZoneId.of("America/Denver"), mountain.getZoneId());
  }

  @Test(expected = CommandExecutionException.class)
  public void testAddDuplicateCalendarThrowsException() throws CommandExecutionException {
    this.manager.addCalendar("Duplicate-Schedule", ZoneId.of("Pacific/Honolulu"));
    this.manager.addCalendar("Duplicate-Schedule", ZoneId.of("America/Anchorage"));
  }

  @Test
  public void testSwitchToCalendarWithMoscowTimezone() throws CommandExecutionException {
    this.manager.addCalendar("Moscow-Research-Calendar", ZoneId.of("Europe/Moscow"));
    this.manager.switchToCalendar("Moscow-Research-Calendar");

    CalendarModel active = this.manager.getActiveCalendar();
    assertEquals("Moscow-Research-Calendar", active.getName());
    assertEquals(ZoneId.of("Europe/Moscow"), active.getZoneId());
    assertTrue("Active calendar should be GuiCalendarModel",
            active instanceof GuiCalendarModel);
  }

  @Test
  public void testSwitchToCalendarWithBangkokTimezone() throws CommandExecutionException {
    this.manager.addCalendar("Bangkok-Street-Food-Tour", ZoneId.of("Asia/Bangkok"));
    this.manager.switchToCalendar("Bangkok-Street-Food-Tour");

    CalendarModel active = this.manager.getActiveCalendar();
    assertEquals("Bangkok-Street-Food-Tour", active.getName());
    assertTrue("Active calendar should be GuiCalendarModel",
            active instanceof GuiCalendarModel);
  }

  @Test
  public void testInheritsBasicCalendarManagerFunctionality() throws CommandExecutionException {
    this.manager.addCalendar("UTC-International-Meetings", ZoneId.of("UTC"));

    // Test basic methods still work
    assertEquals("UTC-International-Meetings",
            this.manager.getCalendar("UTC-International-Meetings").getName());
    assertEquals(1, this.manager.getCalendars().size());

    this.manager.switchToCalendar("UTC-International-Meetings");
    assertEquals("UTC-International-Meetings",
            this.manager.getActiveCalendar().getName());
  }

  @Test
  public void testUpdateNameForSpecialCharacterCalendar() throws CommandExecutionException {
    this.manager.addCalendar("Mom's Birthday & Anniversary Calendar",
            ZoneId.of("America/Chicago"));
    this.manager.switchToCalendar("Mom's Birthday & Anniversary Calendar");

    this.manager.updateName("Mom's Birthday & Anniversary Calendar",
            "Family Celebrations & Special Days");

    CalendarModel renamed = this.manager.getCalendar("Family Celebrations & Special Days");
    assertEquals("Family Celebrations & Special Days", renamed.getName());
    assertTrue("Renamed calendar should still be GuiCalendarModel",
            renamed instanceof GuiCalendarModel);
    assertEquals("Family Celebrations & Special Days",
            this.manager.getActiveCalendar().getName());
  }

  @Test
  public void testUpdateTimezoneFromHawaiiToAlaska() throws CommandExecutionException {
    this.manager.addCalendar("Island-Life-Schedule", ZoneId.of("Pacific/Honolulu"));

    this.manager.updateTimezone("Island-Life-Schedule", "America/Anchorage");

    CalendarModel updated = this.manager.getCalendar("Island-Life-Schedule");
    assertEquals(ZoneId.of("America/Anchorage"), updated.getZoneId());
    assertTrue("Updated calendar should still be GuiCalendarModel",
            updated instanceof GuiCalendarModel);
  }

  @Test
  public void testUpdateTimezoneFromCairoToKarachi() throws CommandExecutionException {
    this.manager.addCalendar("Middle-East-Adventure", ZoneId.of("Africa/Cairo"));

    this.manager.updateTimezone("Middle-East-Adventure", "Asia/Karachi");

    CalendarModel updated = this.manager.getCalendar("Middle-East-Adventure");
    assertEquals(ZoneId.of("Asia/Karachi"), updated.getZoneId());
    assertTrue("Updated calendar should still be GuiCalendarModel",
            updated instanceof GuiCalendarModel);
  }

  @Test
  public void testGuiSpecificFunctionalityWithIcelandicCalendar()
          throws CommandExecutionException {
    this.manager.addCalendar("Iceland-Northern-Lights",
            ZoneId.of("Atlantic/Reykjavik"));

    GuiCalendarModel guiCal = (GuiCalendarModel)
            this.manager.getCalendar("Iceland-Northern-Lights");
    guiCal.setScheduleStart(java.time.LocalDate.of(2025, 11, 15));
    assertEquals(0, guiCal.getScheduledEvents().size());
  }

  @Test
  public void testGuiSpecificFunctionalityWithBrazilianCalendar()
          throws CommandExecutionException {
    this.manager.addCalendar("Rio-Carnival-Events",
            ZoneId.of("America/Sao_Paulo"));

    GuiCalendarModel guiCal = (GuiCalendarModel)
            this.manager.getCalendar("Rio-Carnival-Events");
    guiCal.setScheduleStart(java.time.LocalDate.of(2024, 2, 10));
    assertEquals(0, guiCal.getScheduledEvents().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetNonExistentCalendar() {
    this.manager.getCalendar("Atlantis-Underwater-Calendar");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSwitchToNonExistentCalendar() {
    this.manager.switchToCalendar("Mars-Colony-Schedule");
  }

  @Test
  public void testCalendarTypeAcrossContinents() throws CommandExecutionException {
    String[] calendarNames = {
        "North-America-Business", "South-America-Adventure", "Europe-Cultural-Tour",
        "Africa-Safari-Schedule", "Asia-Food-Journey", "Australia-Outback-Trip",
        "Antarctica-Research-Station"
    };
    ZoneId[] timeZones = {
            ZoneId.of("America/Toronto"),
            ZoneId.of("America/Buenos_Aires"),
            ZoneId.of("Europe/Rome"),
            ZoneId.of("Africa/Johannesburg"),
            ZoneId.of("Asia/Seoul"),
            ZoneId.of("Australia/Perth"),
            ZoneId.of("Antarctica/McMurdo")
    };

    for (int i = 0; i < calendarNames.length; i++) {
      this.manager.addCalendar(calendarNames[i], timeZones[i]);
    }

    assertEquals(7, this.manager.getCalendars().size());
    for (int i = 0; i < calendarNames.length; i++) {
      CalendarModel cal = this.manager.getCalendar(calendarNames[i]);
      assertTrue("Calendar " + calendarNames[i] + " should be GuiCalendarModel instance",
              cal instanceof GuiCalendarModel);
      assertEquals("Timezone should match for " + calendarNames[i],
              timeZones[i], cal.getZoneId());
    }
  }
}
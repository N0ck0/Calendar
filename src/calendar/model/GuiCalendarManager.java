
package calendar.model;

import java.time.ZoneId;

import calendar.controller.commands.CommandExecutionException;

/**
 * A class representing the GUICalendar Manager for the commands.
 */
public class GuiCalendarManager extends CalendarManager {

  /**
   * Constructs a GuiCalendarManager.
   */
  public GuiCalendarManager() {
    super();
  }

  /**
   * Adds a calendar to this GuiCalendarManager.
   *
   * @param name   The name of the new Calendar.
   * @param zoneId The timezone of the new Calendar.
   * @throws CommandExecutionException if a calendar by that name already exists.
   */
  public void addCalendar(String name, ZoneId zoneId) throws CommandExecutionException {
    super.addCalendar(new GuiCalendarModelImpl(name, zoneId));
  }
}
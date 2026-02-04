package calendar.model;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import calendar.controller.commands.CommandExecutionException;

/**
 * Represents a Manager that organizes and controls the use
 * of any amount of calendars.
 */
public class CalendarManager {
  private Map<String, CalendarModel> calendarList;
  private String activeCalendar;

  /**
   * Constructs a CalendarManager.
   */
  public CalendarManager() {
    this.calendarList = new HashMap<>();
    this.activeCalendar = null;
  }

  /**
   * Adds a calendar to this CalendarManager.
   *
   * @param name   The name of the new Calendar.
   * @param zoneId The timezone of the new Calendar.
   * @throws CommandExecutionException if a calendar by that name already exists.
   */
  public void addCalendar(String name, ZoneId zoneId) throws CommandExecutionException {
    if (this.calendarList.containsKey(name)) {
      throw new CommandExecutionException("Calendar already exists");
    }
    calendarList.put(name, new CalendarModelImpl(name, zoneId));
  }

  /**
   * Adds the given calendar to this calendarList.
   *
   * @param model the new calendar.
   * @throws CommandExecutionException if a calendar by that name already exists.
   */
  public void addCalendar(CalendarModel model) throws CommandExecutionException {
    if (this.calendarList.containsKey(model.getName())) {
      throw new CommandExecutionException("Calendar already exists");
    }
    calendarList.put(model.getName(), model);
  }

  /**
   * Retrieves the calendar that is currently in use.
   *
   * @return A CalendarModelImpl.
   */
  public CalendarModel getActiveCalendar() {
    return calendarList.get(activeCalendar);
  }

  /**
   * Switches the calendar in use to the calendar with the given name.
   *
   * @param name the name of the calendar to switch to.
   */
  public void switchToCalendar(String name) {
    if (!calendarList.containsKey(name)) {
      throw new IllegalArgumentException("No such calendar.");
    }
    activeCalendar = name;
  }

  /**
   * Gets the CalendarModel of the calendar with the given name.
   *
   * @param name the name of the calendar.
   * @return CalendarModelImpl.
   */
  public CalendarModel getCalendar(String name) {
    if (!calendarList.containsKey(name)) {
      throw new IllegalArgumentException("No such calendar.");
    } else {
      return calendarList.get(name);
    }
  }

  /**
   * Gets a full list of all currently existing calendars.
   *
   * @return List of CalendarModels.
   */
  public List<CalendarModel> getCalendars() {
    return new ArrayList<>(calendarList.values());
  }

  /**
   * Updates the name of the given calendar to be the given new name.
   *
   * @param calName old name.
   * @param newName new name.
   * @throws CommandExecutionException if newName already exists for another calendar.
   */
  public void updateName(String calName, String newName) throws CommandExecutionException {
    if (this.calendarList.containsKey(newName) && !calName.equals(newName)) {
      throw new CommandExecutionException("Calendar with name (" + calName + ") already exists");
    }

    CalendarModel calendar = this.getCalendar(calName);
    this.calendarList.remove(calName);
    calendar.updateName(newName);
    this.calendarList.put(newName, calendar);
    if (calName.equals(this.activeCalendar)) {
      this.activeCalendar = newName;
    }
  }

  /**
   * Updates the timezone of the given calendar to be the given new timezone.
   *
   * @param calName old name.
   * @param newZone new zone.
   * @throws CommandExecutionException if timezone update fails.
   */
  public void updateTimezone(String calName, String newZone) throws CommandExecutionException {
    CalendarModel calendar = this.getCalendar(calName);
    calendar.updateZoneId(newZone);
  }

}

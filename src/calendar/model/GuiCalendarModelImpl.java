
package calendar.model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

/**
 * Implementation of GuiCalendarModel that extends CalendarModelImpl to provide
 * GUI-specific functionality for calendar management.
 */
public class GuiCalendarModelImpl extends CalendarModelImpl implements GuiCalendarModel {

  List<Event> scheduleList;
  LocalDate startDate;

  /**
   * Constructs a new CalendarModelImpl object with empty event lists.
   */
  public GuiCalendarModelImpl(String name, ZoneId zoneId) {
    super(name, zoneId);
    this.scheduleList = new ArrayList<>();
    this.startDate = LocalDate.of(1990, 1, 1);
  }

  /**
   * Sets the starting date for the schedule view.
   *
   * @param start the starting date for the schedule
   */
  @Override
  public void setScheduleStart(LocalDate start) {
    this.startDate = start;
  }

  /**
   * Gets the events to be included in a schedule based on the start field.
   *
   * @return List of Events.
   */
  public List<Event> getScheduledEvents() {
    List<Event> allEvents = super.getEvents();
    if (allEvents.isEmpty()) {
      return new ArrayList<Event>();
    }
    Collections.sort(allEvents);
    List<Event> scheduledEvents = new ArrayList<>();
    int i = 0;
    while (scheduledEvents.size() <= 9 && i < allEvents.size()) {
      if (!allEvents.get(i).getStartDateTime().isBefore(this.startDate.atStartOfDay())) {
        scheduledEvents.add(allEvents.get(i));
      }
      i += 1;
    }
    return scheduledEvents;
  }
}
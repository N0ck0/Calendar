
package calendar.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface for GUI-specific calendar model operations.
 */
public interface GuiCalendarModel extends CalendarModel {

  /**
   * Sets the starting date for the schedule view.
   *
   * @param start the starting date for the schedule
   */
  public void setScheduleStart(LocalDate start);

  /**
   * Gets the events to be displayed in the schedule.
   *
   * @return a list of scheduled events
   */
  public List<Event> getScheduledEvents();
}


package calendar.controller;

import java.time.LocalDateTime;
import java.time.LocalDate;
import calendar.controller.commands.CommandExecutionException;

/**
 * Interface defining the main features available for calendar management.
 */
public interface Features {

  /**
   * Adds a new event to the currently active calendar.
   *
   * @param subject the title of the event
   * @param start the starting date and time of the event
   * @param end the ending date and time of the event
   * @throws CommandExecutionException if the event cannot be added
   */
  void addEvent(String subject, LocalDateTime start, LocalDateTime end)
          throws CommandExecutionException;

  /**
   * Sets the starting date for the schedule view display.
   *
   * @param startDate the date from which to begin displaying scheduled events
   */
  void setScheduleStartDate(LocalDate startDate);

}

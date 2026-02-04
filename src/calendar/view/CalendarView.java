package calendar.view;

import java.time.LocalDateTime;
import java.util.List;

import calendar.model.Event;

/**
 * An interface representing the ways to display calendar information to the user.
 */
public interface CalendarView {
  /**
   * Displays a general message to the user.
   *
   * @param message the message to display
   */
  void renderMessage(String message);

  /**
   * Displays an error message to the user.
   *
   * @param error the error message to display
   */
  void renderError(String error);

  /**
   * Displays a list of events in a formatted way.
   *
   * @param events the events to display
   */
  void renderEvents(List<Event> events);

  /**
   * Displays the busy status at a specific time.
   *
   * @param busy whether the user is busy
   * @param dateTime the date and time being checked
   */
  void renderBusyStatus(boolean busy, LocalDateTime dateTime);
}

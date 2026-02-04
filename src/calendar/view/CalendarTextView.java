package calendar.view;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.io.PrintStream;

import calendar.model.Event;


/**
 * Text-based implementation of CalendarView that outputs to a PrintStream.
 */
public class CalendarTextView implements CalendarView {
  private PrintStream out;

  /**
   * Constructs an {@code calendar.calendarTextView} object.
   * @param out the message on the print stream
   */
  public CalendarTextView(PrintStream out) {
    this.out = out;
  }

  /**
   * Displays a general message to the user.
   *
   * @param message the message to display
   */
  public void renderMessage(String message) {
    this.out.println(message);
  }

  /**
   * Displays an error message to the user.
   *
   * @param error the error message to display
   */
  public void renderError(String error) {
    this.out.println("Error " + error);
  }

  /**
   * Displays a list of events in a formatted way.
   *
   * @param eventsList the events to display
   */
  public void renderEvents(List<Event> eventsList) {
    try {
      if (eventsList.isEmpty()) {
        out.println("No events found.");
      } else {
        for (Event event : eventsList) {
          StringBuilder sb = new StringBuilder();
          event.toString(sb);
          out.println(sb);
        }
      }
    }
    catch (IOException e) {
      renderError(e.getMessage());
    }
  }

  /**
   * Displays the busy status at a specific time.
   *
   * @param busy whether the user is busy or not
   * @param dateTime the date and time being checked
   */
  public void renderBusyStatus(boolean busy, LocalDateTime dateTime) {
    if (busy) {
      out.println("busy");
    } else {
      out.println("available");
    }
  }
}

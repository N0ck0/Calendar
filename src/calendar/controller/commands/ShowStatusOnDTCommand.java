package calendar.controller.commands;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import calendar.model.CalendarModel;
import calendar.model.Event;
import calendar.view.CalendarView;

/**
 * Command class that displays the availability status at a specific date and time.
 */
public class ShowStatusOnDTCommand implements Command {

  CalendarModel model;
  CalendarView view;
  LocalDateTime date;

  /**
   * Constructs an {@code calendar.controller.commands.showStatusOnDTCommand} object with the
   * specified paramters.
   *
   * @param model the CalendarModel to check for events
   * @param view  the CalendarView to use for displaying the status info
   * @param date  the specific date and time to check availability for
   */
  public ShowStatusOnDTCommand(CalendarModel model, CalendarView view, LocalDateTime date) {
    this.model = model;
    this.view = view;
    this.date = date;
  }

  /**
   * Executes the command by checking all events to see which ones are occurring
   * at the specific date and time.
   */
  public void execute() {
    List<Event> events = model.getEvents();
    List<Event> filteredEvents = new ArrayList<>();

    for (Event event : events) {
      if (event.occursDuring(date)) {
        filteredEvents.add(event);
      }
    }
    view.renderMessage("Status: ");
    if (!filteredEvents.isEmpty()) {
      view.renderEvents(filteredEvents);
    } else {
      view.renderMessage("Available");
    }
  }
}

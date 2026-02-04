package calendar.controller.commands;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import calendar.model.CalendarModel;
import calendar.model.Event;
import calendar.view.CalendarView;

/**
 * Command that gets and displays all events if their start times are within a specific date and
 * time interval.
 */
public class PrintEventsDTIntervalCommand implements Command {

  CalendarModel model;
  CalendarView view;
  LocalDateTime startDT;
  LocalDateTime endDT;

  /**
   * Constructs an {@code calendar.controller.commands.printEventsDTIntervalCommand} object  with
   * the given parameters.
   *
   * @param model   the CalendarModel to get events from
   * @param view    the CalendarView to use for rendering the filtered events
   * @param startDT the start of the date and time interval
   * @param endDT   the end of the date and time interval
   */
  public PrintEventsDTIntervalCommand(CalendarModel model, CalendarView view,
                                      LocalDateTime startDT, LocalDateTime endDT) {
    this.model = model;
    this.view = view;
    this.startDT = startDT;
    this.endDT = endDT;
  }

  /**
   * Checks if a given date and time falls within the given interval that is inclusive.
   *
   * @param dateTime the date-time to check
   * @return true if the date-time is within the interval and false otherwise
   */
  private boolean isBetween(LocalDateTime dateTime) {
    return (dateTime.isEqual(startDT) || dateTime.isAfter(startDT))
            && (dateTime.isBefore(endDT) || dateTime.isEqual(endDT));
  }

  /**
   * Executes the command by getting all events from the model, filtering the ones that fall
   * within the intervals and rendering the filtered list through the view.
   */
  public void execute() {
    List<Event> events = model.getEvents();
    List<Event> filteredEvents = new ArrayList<>();
    for (Event event : events) {
      if (this.isBetween(event.getStartDateTime())) {
        filteredEvents.add(event);
      }
    }
    view.renderEvents(filteredEvents);
  }
}

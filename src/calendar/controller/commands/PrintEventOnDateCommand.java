package calendar.controller.commands;

import java.time.LocalDate;
import java.util.List;

import calendar.model.CalendarModel;
import calendar.model.Event;
import calendar.view.CalendarView;

/**
 * Command that gets, filters, and displays all events that occur on a specific date.
 */
public class PrintEventOnDateCommand implements Command {

  private CalendarModel model;
  private CalendarView view;
  private LocalDate date;

  /**
   * Constructs an {@code calendar.controller.commands.printEventOnDateCommand} object with the
   * specified parameters.
   *
   * @param model the CalendarModel to retrieve events from
   * @param view  the CalendarView to use for rendering the filtered events
   * @param date  the specific date to filter events by
   */
  public PrintEventOnDateCommand(CalendarModel model, CalendarView view, LocalDate date) {
    this.model = model;
    this.view = view;
    this.date = date;
  }

  /**
   * Executes the command by asking for the date-filtered events from the model
   * and displaying them through the view.
   */
  public void execute() {
    List<Event> filteredEvents = this.model.getEventsOnDate(this.date);
    this.view.renderEvents(filteredEvents);
  }
}

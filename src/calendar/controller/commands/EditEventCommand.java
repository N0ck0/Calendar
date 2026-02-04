package calendar.controller.commands;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import calendar.model.CalendarModel;
import calendar.model.Event;
import calendar.model.EventSeries;

/**
 * Command class that modifies properties of existing events in the calendar.
 */
public class EditEventCommand implements Command {

  private CalendarModel model;
  private String property;
  private String newValue;
  private Event event;
  private String editType;

  /**
   * Constructs an {@code calendar.controller.commands.EditEventCommand} object that finds and
   * edits an event by checking against the temporary properties.
   *
   * @param model the CalendarModel that has the events to edit
   * @param subject the subject of the event to find and edit
   * @param property the property of the event to edit
   * @param startDT the start date and time of the event to find
   * @param endDT the end date and time of the event to find
   * @param newValue the new value to set for the specified property
   * @throws CommandExecutionException if the event cannot be found or edited
   */
  public EditEventCommand(CalendarModel model, String subject, String property,
                          LocalDateTime startDT, LocalDateTime endDT, String newValue)
          throws CommandExecutionException {
    this.model = model;
    this.property = property;
    this.newValue = newValue;
    this.editType = "single";
    Event tempEvent = new Event(subject, startDT, endDT);
    if (property.equals("start")) {
      for (EventSeries series : model.getEventSeries()) {
        series.changeStartTime(model, subject, startDT, endDT, newValue);
      }
      for (Event event : model.getSingleEvents()) {
        if (event.conflictsWith(tempEvent)) {
          this.event = event;
        }
      }
    } else {
      List<Event> events = new ArrayList<>();
      for (Event event : model.getEvents()) {
        if (event.conflictsWith(tempEvent)) {
          this.event = event;
        }
      }
    }
  }

  /**
   * Constructs an {@code calendar.controller.commands.EditEventCommand} object that directly
   * edits a specific event when the instance is known.
   *
   * @param model the CalendarModel that has the event
   * @param event the specific Event instance to edit
   * @param property the property of the event to edit
   * @param newValue the new value to set for the given property
   */
  public EditEventCommand(CalendarModel model, Event event, String property, String newValue) {
    this.model = model;
    this.property = property;
    this.newValue = newValue;
    this.event = event;
    this.editType = "single";
  }

  /**
   * Constructs an {@code calendar.controller.commands.EditEventCommand} object that edits all
   * events in a series that finds the events by subject and times.
   *
   * @param model the CalendarModel that has the events
   * @param subject the subject of the event series to edit
   * @param property the property to edit in the matching events
   * @param dateTime the start date and time to match for finding the event
   * @param newValue the new value to set for the given property
   * @throws CommandExecutionException if no event is found, multiple events match
   *                                   the criteria, or the edit fails
   */
  public EditEventCommand(CalendarModel model, String subject, String property,
                          LocalDateTime dateTime, String newValue) throws
          CommandExecutionException {
    this.model = model;
    this.property = property;
    this.newValue = newValue;
    this.editType = "allAfter";
    List<Event> eventsFound = new ArrayList<>();
    for (Event event : model.getEvents()) {
      if (event.getSubject().equals(subject)
              && event.getStartDateTime().isEqual(dateTime)) {
        eventsFound.add(event);
      }
    }
    if (eventsFound.size() > 1) {
      throw new CommandExecutionException("More than one event meets search criteria");
    } else if (eventsFound.isEmpty()) {
      throw new CommandExecutionException("Found no events that met criteria");
    } else {
      this.event = eventsFound.get(0);

    }
  }

  /**
   * Constructs an {@code calendar.controller.commands.EditEventCommand} object that edits all
   * events in a series and finds the event by subject and start time then modifies all events in
   * the series.
   *
   * @param model the CalendarModel that has the events
   * @param subject the subject of the event series to edit
   * @param property the property to edit in all events of the series
   * @param dateTime the start date and time to match for finding the event series
   * @param newValue the new value to set for the given property
   * @param bool checks if the constructor overloads
   * @throws CommandExecutionException if no event is found, multiple events match
   *                                   the criteria, or the edit fails
   */
  public EditEventCommand(CalendarModel model, String subject, String property,
                          LocalDateTime dateTime, String newValue, boolean bool) throws
          CommandExecutionException {
    this.model = model;
    this.property = property;
    this.newValue = newValue;
    this.editType = "all";
    List<Event> eventsFound = new ArrayList<>();
    for (Event event : model.getEvents()) {
      if (event.getSubject().equals(subject)
              && event.getStartDateTime().isEqual(dateTime)) {
        eventsFound.add(event);
      }
    }
    if (eventsFound.size() > 1) {
      throw new CommandExecutionException("More than one event meets search criteria");
    } else if (eventsFound.isEmpty()) {
      throw new CommandExecutionException("Found no events that met criteria");
    } else {
      this.event = eventsFound.get(0);
    }
  }

  /**
   * Executes the edit command based on the specified editType.
   *
   * @throws CommandExecutionException if the edit operation fails or if an
   *                                   unsupported editType is encountered
   */
  public void execute() throws CommandExecutionException {
    switch (editType) {

      case "single":
        this.event.modifyProperty(property, newValue);
        break;

      case "allAfter":
        for (EventSeries series : model.getEventSeries()) {
          if (series.containsEvent(event.getSubject(), event.getStartDateTime())) {
            series.changeAllAfter(model, event.getSubject(), event.getStartDateTime(),
                    property, newValue);
            return;
          }
        }
        new EditEventCommand(model, event, property, newValue).execute();
        break;

      case "all":
        List<EventSeries>  eventSeriesFound = new ArrayList<>();
        for (EventSeries series : model.getEventSeries()) {
          if (series.containsEvent(event.getSubject(), event.getStartDateTime())) {
            eventSeriesFound.add(series);
          }
        }
        eventSeriesFound.get(0).changeAll(model, event.getSubject(),
                event.getStartDateTime(), property,
                newValue);
        new EditEventCommand(model, event, property, newValue).execute();
        break;

      default:
        throw new CommandExecutionException("Unsupported editType");
    }
  }
}

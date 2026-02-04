
package calendar.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import calendar.controller.commands.CommandExecutionException;
import calendar.model.CalendarManager;
import calendar.model.CalendarModel;
import calendar.model.Event;
import calendar.model.GuiCalendarModel;
import calendar.view.CalendarGuiView;

/**
 * Controller implementation for GUI-based calendar operations.
 */
public class GuiController implements CalendarController, Features {

  private final CalendarManager manager;
  private final CalendarGuiView view;

  /**
   * Constructs a GuiController with the given manager, view, and model.
   *
   * @param manager the calendar manager
   * @param view the GUI view
   */
  public GuiController(CalendarManager manager, CalendarGuiView view) {
    this.manager = manager;
    this.view = view;
  }

  /**
   * Starts the GUI controller by setting up features and refreshing events.
   */
  public void run() {
    this.view.setFeatures();
    this.view.refreshEvents();
  }

  /**
   * Adds a new event to the currently active calendar.
   *
   * @param subject the title of the event
   * @param start the starting date and time of the event
   * @param end the ending date and time of the event
   * @throws CommandExecutionException if the event can't be added
   */
  @Override
  public void addEvent(String subject, LocalDateTime start, LocalDateTime end)
          throws CommandExecutionException {
    if (subject == null || subject.trim().isEmpty()) {
      throw new CommandExecutionException("Event subject can't be empty");
    }
    if (start == null || end == null) {
      throw new CommandExecutionException("Event start and end times can't be null");
    }
    if (start.isAfter(end) || start.isEqual(end)) {
      throw new CommandExecutionException("Event start time has to be before end time");
    }
    this.manager.getActiveCalendar().addEvent(new Event(subject, start, end));
  }

  /**
   * Sets the starting date for the schedule view display.
   *
   * @param date the date from which to begin displaying scheduled events
   */
  public void setScheduleStartDate(LocalDate date) {
    if (date == null) {
      this.view.renderError("Date can't be null");
      return;
    }

    CalendarModel curCal = this.manager.getActiveCalendar();
    try {
      GuiCalendarModel attemptCal = (GuiCalendarModel) curCal;
      attemptCal.setScheduleStart(date);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      this.view.renderError("Unable to set schedule start date");
    }
  }

  /**
   * Gets the scheduled events from the current calendar.
   *
   * @return a list of scheduled events
   */
  public List<Event> getEventSchedule() {
    CalendarModel curCal = this.manager.getActiveCalendar();
    try {
      GuiCalendarModel attemptCal = (GuiCalendarModel) curCal;
      return attemptCal.getScheduledEvents();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      this.view.renderError("Unable to get event schedule");
    }
    return List.of();
  }

  /**
   * Creates a new calendar with the given name.
   *
   * @param name the name of the new calendar
   * @throws CommandExecutionException if the calendar can't be created
   */
  public void createCalendar(String name) throws CommandExecutionException {
    if (name == null || name.trim().isEmpty()) {
      throw new CommandExecutionException("Calendar name can't be empty");
    }
    this.manager.addCalendar(name, ZoneId.systemDefault());
  }

  /**
   * Selects the calendar with the given name as the active calendar.
   *
   * @param name the name of the calendar to select
   */
  public void selectCalendar(String name) {
    if (name == null || name.trim().isEmpty()) {
      this.view.renderError("Calendar name can't be empty");
      return;
    }

    try {
      this.manager.switchToCalendar(name.trim());
    } catch (IllegalArgumentException e) {
      this.view.renderError("Calendar '" + name + "' does not exist");
    }
  }

  /**
   * Gets the names of all available calendars.
   *
   * @return a list of calendar names
   */
  public List<String> getCalendarNames() {
    List<String> names = new ArrayList<>();
    for (CalendarModel calendar : this.manager.getCalendars()) {
      names.add(calendar.getName());
    }
    return names;
  }

  /**
   * Edits an existing event by replacing it with new event data.
   *
   * @param originalEvent the original event to be replaced
   * @param editedEvent the new event data
   * @throws CommandExecutionException if the event cannot be edited
   */
  public void editEvent(Event originalEvent, Event editedEvent) throws CommandExecutionException {
    if (originalEvent == null || editedEvent == null) {
      throw new CommandExecutionException("Original and edited events can't be null");
    }

    CalendarModel activeCalendar = this.manager.getActiveCalendar();
    if (editedEvent.getSubject() == null || editedEvent.getSubject().trim().isEmpty()) {
      throw new CommandExecutionException("Event subject can't be empty");
    }
    if (editedEvent.getStartDateTime().isAfter(editedEvent.getEndDateTime()) ||
            editedEvent.getStartDateTime().isEqual(editedEvent.getEndDateTime())) {
      throw new CommandExecutionException("Event start time have to be before end time");
    }
    boolean removed = activeCalendar.removeEvent(originalEvent);
    if (!removed) {
      throw new CommandExecutionException("Original event not found in calendar");
    }

    try {
      activeCalendar.addEvent(editedEvent);
    } catch (CommandExecutionException e) {
      try {
        activeCalendar.addEvent(originalEvent);
      } catch (CommandExecutionException ignored) {
      }
      throw new CommandExecutionException("Failed to update event: " + e.getMessage());
    }
  }
}
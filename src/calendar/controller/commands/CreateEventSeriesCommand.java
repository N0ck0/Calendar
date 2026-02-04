package calendar.controller.commands;

import java.time.LocalDate;
import java.time.LocalDateTime;

import calendar.model.CalendarModel;
import calendar.model.Event;
import calendar.model.EventSeries;
//import java.time.LocalTime;
//import java.time.format.DateTimeParseException;

/**
 * Command that creates a new recurring event series in the calendar.
 */
public class CreateEventSeriesCommand implements Command {
  EventSeries eventSeries;
  CalendarModel model;

  /**
   * Constructs an {@code calendar.controller.commands.createEventSeriesCommand} object.
   *
   * @param model the calendar model to add the event series to
   * @param subject the subject for all events in the series
   * @param startDT the start date and time for the first event
   * @param endDT the end date and time for the first event
   * @param weekdays string for days to repeat on
   * @param times the number of times to repeat the event
   * @throws CommandExecutionException if the event series can't be created
   */
  public CreateEventSeriesCommand(CalendarModel model,
                                  String subject, LocalDateTime startDT,
                                  LocalDateTime endDT, String weekdays, int times)
          throws CommandExecutionException {
    this.model = model;
    Event event = new Event(subject, startDT, endDT);
    this.eventSeries = new EventSeries(event, weekdays, times);

  }

  /**
   * Constructs an {@code calendar.controller.commands.createEventSeriesCommand}
   * for an all-day recurring event series.
   *
   * @param model the CalendarModel instance that this command will operate on
   * @param subject the title or description for all events in the series
   * @param startDate the date of the first event in the series (all-day event)
   * @param weekdays string specifying which days of the week to repeat on
   * @param untilDate the last date on which events should be created
   * @throws CommandExecutionException if there is an error during event series creation
   *                                   or if the provided parameters are invalid
   */
  public CreateEventSeriesCommand(CalendarModel model,
                                  String subject, LocalDate startDate, String weekdays,
                                  LocalDate untilDate)
          throws CommandExecutionException {
    this.model = model;
    Event event = new Event(subject, startDate);
    this.eventSeries = new EventSeries(event, weekdays, untilDate);
  }

  /**
   * Constructs aan {@code calendar.controller.commands.createEventSeriesCommand}
   * for an all-day recurring event series with times.
   *
   * @param model the CalendarModel instance that this command will operate on
   * @param subject the title or description for all events in the series
   * @param startDate the date of the first event in the series (all-day event)
   * @param weekdays string specifying which days of the week to repeat on
   * @param times the number of occurrences to create in the series
   * @throws CommandExecutionException if there is an error during event series creation
   *                                   or if the provided parameters are invalid
   */
  public CreateEventSeriesCommand(CalendarModel model,
                                  String subject, LocalDate startDate, String weekdays, int times)
          throws CommandExecutionException {
    this.model = model;
    Event event = new Event(subject, startDate);
    this.eventSeries = new EventSeries(event, weekdays, times);
  }

  /**
   * Constructs an {@code calendar.controller.commands.createEventSeriesCommand}
   * for a timed recurring event series.
   *
   * @param model the CalendarModel instance that this command will operate on
   * @param subject the title or description for all events in the series
   * @param startDT the start date and time for the first event in the series
   * @param endDT the end date and time for the first event in the series
   * @param weekdays string specifying which days of the week to repeat on
   * @param untilDate the last date on which events should be created
   * @throws CommandExecutionException if there is an error during event series creation
   *                                   or if the provided parameters are invalid
   */
  public CreateEventSeriesCommand(CalendarModel model,
                                  String subject, LocalDateTime startDT,
                                  LocalDateTime endDT, String weekdays, LocalDate untilDate)
          throws CommandExecutionException {
    this.model = model;
    Event event = new Event(subject, startDT, endDT);
    this.eventSeries = new EventSeries(event, weekdays, untilDate);
  }

  /**
   * Executes the command to create and add the event series to the calendar.
   */
  public void execute() throws CommandExecutionException {
    this.model.addEventSeries(this.eventSeries);
  }
}

package calendar.controller.commands;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import calendar.model.CalendarModel;
import calendar.model.Event;

/**
 * Command class that creates a new single event in the calendar.
 */
public class CreateEventCommand implements Command {
  private Event event;
  private CalendarModel model;

  /**
   * Constructs a CreateEventCommand object.
   *
   * @param model the calendar model to add the event to
   * @param subject the event subject
   * @param startDateTime the start date and time
   * @param endDateTime the end date and time
   */
  public CreateEventCommand(CalendarModel model,
                            String subject, String startDateTime, String endDateTime)
          throws CommandExecutionException {
    this.model = model;
    try {
      this.event = new Event(subject, LocalDateTime.parse(startDateTime),
              LocalDateTime.parse(endDateTime));
    }
    catch (DateTimeParseException e) {
      throw new CommandExecutionException();

    }
  }

  /**
   * Constructs a  Constructs an {@code calendar.controller.commands.createEventCommand} object
   * with the given calendar model, event subject, and date.
   *
   * @param model the CalendarModel instance will be changed
   * @param subject the title or description of the event that should be created
   * @param date the date the event will happen
   * @throws CommandExecutionException if there is an error while creating event or
   *                                   if the provided parameters are invalid
   */
  public CreateEventCommand(CalendarModel model, String subject, LocalDate date)
          throws CommandExecutionException {
    this.model = model;
    this.event = new Event(subject, date);

  }

  /**
   * Executes the command to create and add the event to the calendar.
   */
  public void execute() throws CommandExecutionException {
    this.model.addEvent(this.event);

  }
}

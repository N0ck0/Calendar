package calendar.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

import calendar.controller.commands.Command;
import calendar.controller.commands.CommandExecutionException;
import calendar.controller.commands.CopyEventCommand;
import calendar.controller.commands.CreateCalendarCommand;
import calendar.controller.commands.CreateEventCommand;
import calendar.controller.commands.CreateEventSeriesCommand;
import calendar.controller.commands.EditCalendarCommand;
import calendar.controller.commands.EditEventCommand;
import calendar.controller.commands.PrintEventOnDateCommand;
import calendar.controller.commands.PrintEventsDTIntervalCommand;
import calendar.controller.commands.QuitCommand;
import calendar.controller.commands.ShowStatusOnDTCommand;
import calendar.controller.commands.UseCalendarCommand;
import calendar.model.CalendarManager;
import calendar.view.CalendarView;

/**
 * Represents the implementation of CommandParser that handles calendar command syntax.
 */
public class CommandParserImpl implements CommandParser {
  CalendarManager manager;
  CalendarView view;

  /**
   * Constructs a command parser with the given model and view.
   *
   * @param manager CalendarManager.
   * @param view    CalendarView.
   */
  public CommandParserImpl(CalendarManager manager, CalendarView view) {
    this.manager = manager;
    this.view = view;
  }

  /**
   * Parses the given input command and returns the corresponding
   * Command object.
   *
   * @param input the text command to parse
   * @return a Command.
   * @throws CommandExecutionException if this failed to parse the given
   *                                   command.
   */
  @Override
  public Command parse(String input) throws CommandExecutionException {

    try {
      if (input.startsWith("create calendar") || input.startsWith("use calendar")
              || input.startsWith("edit calendar")) {
        if (this.isCreateCalendarCommand(input)) {
          return this.makeCreateCalendarCommand(input);
        } else if (this.isUseCalendarCommand(input)) {
          return this.makeUseCalendarCommand(input);
        } else if (this.isEditCalendarCommand(input)) {
          return this.makeEditCalendarCommand(input);
        }
      }

      if (manager.getActiveCalendar() == null) {
        throw new CommandExecutionException("No active calendar, " +
                "please create/select a calendar first");
      }

      // Identify and create the correct Command subclass
      else if (input.toLowerCase().startsWith("create event")) {
        if (this.isSimpleCreateCommand(input)) {
          return this.makeSimpleCreateCommand(input);
        } else if (this.isForNTimesDateTimeCommand(input)) {
          return this.makeForNTimesDateTimeCommand(input);
        } else if (this.isEventSeriesUntilDateCommand(input)) {
          return this.makeEventSeriesUntilDateCommand(input);
        } else if (this.isSingleAllDayEventCommand(input)) {
          return this.makeSingleAllDayEventCommand(input);
        } else if (this.isAllDayEventSeriesForNTimesCommand(input)) {
          return this.makeAllDayEventSeriesForNTimesCommand(input);
        } else if (this.isAllDayEventSeriesUntilDateCommand(input)) {
          return this.makeAllDayEventSeriesUntilDateCommand(input);
        }
      } else if (input.toLowerCase().startsWith("edit event")
              || input.toLowerCase().startsWith("edit series")) {
        if (this.isSingleEventEditCommand(input)) {
          return this.makeSingleEventEditCommand(input);
        } else if (this.isEditAllAfterInSeriesCommand(input)) {
          return this.makeEditAllAfterInSeriesCommand(input);
        } else if (this.isEditAllInSeriesCommand(input)) {
          return this.makeEditAllInSeriesCommand(input);
        }
      } else if (input.toLowerCase().startsWith("print events")) {
        if (input.toLowerCase().startsWith("print events on")) {
          return this.makePrintEventOnDateCommand(input);
        } else if (input.toLowerCase().contains("from")) {
          return this.makePrintEventsDTInterval(input);
        }
      } else if (input.toLowerCase().startsWith("show status")) {
        return this.makeShowStatusOnDT(input);
      } else if (input.toLowerCase().startsWith("copy event")) {
        if (this.isCopySingleEventCommand(input)) {
          return this.makeCopySingleEventCommand(input);
        } else if (this.isCopyAllEventsOnDayCommand(input)) {
          return this.makeCopyAllEventsOnDayCommand(input);
        } else if (this.isCopyAllEventsBetweenDatesCommand(input)) {
          return this.makeCopyAllEventsBetweenDatesCommand(input);
        }
      } else if (input.toLowerCase().equals("exit")
              || input.toLowerCase().equals("quit")) {
        return this.makeQuitCommand();
      }
      throw new CommandExecutionException("Unknown command: " + input);
    } catch (Exception e) {
      throw new CommandExecutionException("Invalid command");
    }
  }

  private Command makeEditCalendarCommand(String input) throws CommandExecutionException {
    try {
      String calName = this.parseMultiWordSubject(input.substring(0,
              input.indexOf("--property")), 3);
      String newValue = this.parseMultiWordSubject(input.substring(input.indexOf("--property")),
              2);
      String property;
      if (input.contains("timezone")) {
        property = "timezone";
      } else if (input.contains("name")) {
        property = "name";
      } else {
        throw new CommandExecutionException("Unknown property: " + input);
      }
      return new EditCalendarCommand(manager, calName, property, newValue);
    } catch (Exception e) {
      throw new CommandExecutionException("Invalid command");
    }
  }


  private boolean isEditCalendarCommand(String input) {
    return input.toLowerCase().startsWith("edit calendar");
  }

  private Command makeCopyAllEventsBetweenDatesCommand(String input)
          throws CommandExecutionException {
    try {
      String calendarName = this.parseMultiWordSubject(input, 7);
      String[] split = input.split(" ");
      LocalDate startingDate = LocalDate.parse(split[3]);
      LocalDate endingDate = LocalDate.parse(split[5]);
      LocalDate toDate = LocalDate.parse(split[split.length - 1]);
      return new CopyEventCommand(manager, startingDate, endingDate, calendarName, toDate);
    } catch (Exception e) {
      throw new CommandExecutionException("Invalid command");
    }
  }

  private boolean isCopyAllEventsBetweenDatesCommand(String input) {
    return input.toLowerCase().startsWith("copy events between");
  }

  private Command makeCopyAllEventsOnDayCommand(String input) throws CommandExecutionException {
    try {
      String calendarName = this.parseMultiWordSubject(input, 5);
      String[] split = input.split(" ");
      LocalDate onDate = LocalDate.parse(split[3]);
      LocalDate toDate = LocalDate.parse(split[split.length - 1]);
      return new CopyEventCommand(manager, onDate, calendarName, toDate);
    } catch (Exception e) {
      throw new CommandExecutionException("Invalid command");
    }
  }

  private boolean isCopyAllEventsOnDayCommand(String input) {
    return input.toLowerCase().startsWith("copy events on");
  }

  private Command makeCopySingleEventCommand(String input) throws CommandExecutionException {
    try {
      String eventSubject = this.parseMultiWordSubject(input, 2);
      String shortenedString = input.substring(input.indexOf("on"));
      String calendarName = this.parseMultiWordSubject(shortenedString, 3);
      String[] split = shortenedString.split(" ");
      LocalDateTime eventStart = LocalDateTime.parse(split[1]);
      LocalDateTime newStart = LocalDateTime.parse(split[split.length - 1]);
      return new CopyEventCommand(manager, eventSubject, eventStart, calendarName, newStart);
    } catch (Exception e) {
      throw new CommandExecutionException("Invalid command");
    }
  }

  private boolean isCopySingleEventCommand(String input) {
    return input.toLowerCase().startsWith("copy event ");
  }


  /**
   * Constructs and returns a UseCalendarCommand.
   *
   * @param input the parameters for the UseCalendarCommand.
   * @return Command.
   */
  private Command makeUseCalendarCommand(String input) {
    String calName = this.parseMultiWordSubject(input, 3);
    return new UseCalendarCommand(manager, calName);
  }


  /**
   * Determines if the given input corresponds to a command that tries to
   * use a certain calendar.
   *
   * @param input the given command.
   * @return t/f depending on input.
   */
  private boolean isUseCalendarCommand(String input) {
    return input.toLowerCase().startsWith("use calendar");
  }


  /**
   * Returns a command that creates a calendar based on the specifications
   * in the input.
   *
   * @param input the given command.
   * @return A command
   * @throws CommandExecutionException if the given command is invalid.
   */
  private Command makeCreateCalendarCommand(String input) throws CommandExecutionException {
    String name = this.parseMultiWordSubject(input, 3);
    String[] split = input.split(" ");
    ZoneId zoneId = ZoneId.of(split[split.length - 1]);
    return new CreateCalendarCommand(manager, name, zoneId);
  }

  /**
   * Determines if the given input corresponds to a command that creates a calendar.
   *
   * @param input the input.
   * @return t/f depending on input.
   */
  private boolean isCreateCalendarCommand(String input) {
    return input.toLowerCase().startsWith("create calendar");
  }

  /**
   * Returns a QuitCommand.
   *
   * @return QuitCommand.
   */
  private Command makeQuitCommand() {
    return new QuitCommand(view);
  }

  /**
   * Makes a EditCommand that edits every Event in a Series.
   *
   * @param input given command.
   * @return EventEditCommand.
   * @throws CommandExecutionException if the given command is invalid.
   */
  private Command makeEditAllInSeriesCommand(String input) throws CommandExecutionException {
    String subject = this.parseMultiWordSubject(input.substring(0,
            input.indexOf("from")), 3);
    String[] arr = input.split(" ");
    int len = arr.length;
    String newValue = this.parseMultiWordSubject(input.substring(input.indexOf("from")),
            3);
    String property = arr[2];
    String shortened = input.substring(input.indexOf("from"));
    String[] shortenedSplit = shortened.split(" ");
    String dateTime = shortenedSplit[1];
    return new EditEventCommand(manager.getActiveCalendar(), subject, property,
            LocalDateTime.parse(dateTime), newValue, true);
  }

  /**
   * Determines if the given command corresponds to editing all
   * the events in a series.
   *
   * @param input given command.
   * @return boolean.
   */
  private boolean isEditAllInSeriesCommand(String input) {
    return input.toLowerCase().startsWith("edit series");
  }


  /**
   * Makes an Edit command that edits every event in the series after the corresponding
   * given event.
   *
   * @param input the command.
   * @return EditEventCommand.
   * @throws CommandExecutionException if the given command is invalid.
   */
  private Command makeEditAllAfterInSeriesCommand(String input) throws CommandExecutionException {
    String subject = this.parseMultiWordSubject(input.substring(0,
            input.indexOf("from")), 3);
    String[] arr = input.split(" ");
    int len = arr.length;
    String newValue = this.parseMultiWordSubject(input.substring(input.indexOf("from")),
            3);
    String property = arr[2];
    String shortened = input.substring(input.indexOf("from"));
    String[] shortenedSplit = shortened.split(" ");
    String dateTime = shortenedSplit[1];
    LocalDateTime date = LocalDateTime.parse(dateTime);
    return new EditEventCommand(manager.getActiveCalendar(), subject, property, date, newValue);
  }

  /**
   * Determines if the given command corresponds to editing all the events
   * after the given one in a series.
   *
   * @param input the given command.
   * @return boolean.
   */
  private boolean isEditAllAfterInSeriesCommand(String input) {
    return input.toLowerCase().startsWith("edit events");
  }

  /**
   * Constructs a command to edit a single event.
   *
   * @param input the user input
   * @return an EditEventCommand instance
   * @throws CommandExecutionException if parsing fails
   */
  private Command makeSingleEventEditCommand(String input) throws CommandExecutionException {

    String subject = this.parseMultiWordSubject(input.substring(0,
            input.indexOf("from")), 3);
    String[] arr = input.split(" ");
    int len = arr.length;
    String newValue = this.parseMultiWordSubject(input.substring(input.indexOf("from")),
            5);
    String property = arr[2];
    String shortened = input.substring(input.indexOf("from"));
    String[] shortenedSplit = shortened.split(" ");
    String dateTime1 = shortenedSplit[1];
    LocalDateTime dateFrom = LocalDateTime.parse(dateTime1);
    String dateTime2 = shortenedSplit[3];
    LocalDateTime dateTo = LocalDateTime.parse(dateTime2);
    return new EditEventCommand(manager.getActiveCalendar(),
            subject, property, dateFrom, dateTo, newValue);
  }

  /**
   * Determines if the input is an edit command for a single event.
   *
   * @param input the user input
   * @return true if it is a valid command of this type
   */
  private boolean isSingleEventEditCommand(String input) {
    return input.toLowerCase().startsWith("edit event ");
  }


  /**
   * Makes a command to create an event series consisting of all-day events
   * until the given date.
   *
   * @param input the command.
   * @return an EditEventCommand.
   * @throws CommandExecutionException if parsing fails.
   */
  private Command makeAllDayEventSeriesUntilDateCommand(String input) throws
          CommandExecutionException {
    String subject = this.parseMultiWordSubject(input);
    String[] split = input.split(" ");
    int len = split.length;
    LocalDate untilDate = LocalDate.parse(split[len - 1]);
    String weekdays = split[len - 3];
    LocalDate startDate = LocalDate.parse(split[len - 5]);
    return new CreateEventSeriesCommand(manager.getActiveCalendar(),
            subject, startDate, weekdays, untilDate);
  }

  /**
   * Determines if the given command corresponds to creating an event series
   * of all-day events until a given date.
   *
   * @param input the command.
   * @return boolean describing result.
   */
  private boolean isAllDayEventSeriesUntilDateCommand(String input) {
    String[] split = input.split(" ");
    if (split[split.length - 2].toLowerCase().equals("until")
            && split[split.length - 4].toLowerCase().equals("repeats")) {
      try {
        LocalDate.parse(split[split.length - 5]);
        return true;
      } catch (DateTimeParseException e) {
        return false;
      }
    }
    return false;
  }

  /**
   * Constructs a command for creating an all-day event that repeats
   * on specific weekdays for a set number of times.
   */
  private Command makeAllDayEventSeriesForNTimesCommand(String input) throws
          CommandExecutionException {
    String subject = this.parseMultiWordSubject(input);
    String[] split = input.split(" ");
    int len = split.length;
    int repeatTimes = Integer.parseInt(split[len - 2]);
    String weekdays = split[len - 4];
    LocalDate startDate = LocalDate.parse(split[len - 6]);
    return new CreateEventSeriesCommand(manager.getActiveCalendar(), subject,
            startDate, weekdays, repeatTimes);

  }

  /**
   * Checks if the input corresponds to a valid all-day repeating event
   * with a specified number of repetitions.
   */
  private boolean isAllDayEventSeriesForNTimesCommand(String input) {
    String[] strArr = input.split(" ");
    int len = strArr.length;

    if (strArr[len - 1].equals("times")) {
      try {
        LocalDate.parse(strArr[len - 6]);
        return true;
      } catch (DateTimeParseException e) {
        return false;
      }
    }
    return false;
  }

  /**
   * Constructs a command for creating a single all-day event on a specific date.
   */
  private Command makeSingleAllDayEventCommand(String input) throws CommandExecutionException {
    String subject = this.parseMultiWordSubject(input);
    String[] split = input.split(" ");
    LocalDate date = LocalDate.parse(split[split.length - 1]);
    return new CreateEventCommand(manager.getActiveCalendar(), subject, date);
  }

  /**
   * Checks if the input represents a valid single all-day event command.
   */
  private boolean isSingleAllDayEventCommand(String input) {
    String[] split = input.split(" ");

    if (split[split.length - 2].toLowerCase().equals("on")) {
      try {
        LocalDate.parse(split[split.length - 1]);
        return true;
      } catch (DateTimeParseException e) {
        return false;
      }
    }
    return false;
  }

  /**
   * Constructs a command for creating a repeating event with start and end datetimes
   * that continues until a specified date.
   */
  private Command makeEventSeriesUntilDateCommand(String input) throws CommandExecutionException {
    String subject = this.parseMultiWordSubject(input);
    String[] split = input.split(" ");
    int len = split.length;
    LocalDate untilDate = LocalDate.parse(split[len - 1]);
    String weekdays = split[len - 3];
    LocalDateTime endDate = LocalDateTime.parse(split[len - 5]);
    LocalDateTime startDate = LocalDateTime.parse(split[len - 7]);
    return new CreateEventSeriesCommand(manager.getActiveCalendar(), subject,
            startDate, endDate, weekdays, untilDate);

  }

  /**
   * Checks if the input represents a valid repeating event command that ends on a specific date.
   */
  private boolean isEventSeriesUntilDateCommand(String input) {
    String[] split = input.split(" ");
    if (split[split.length - 2].toLowerCase().equals("until")
            && split[split.length - 4].toLowerCase().equals("repeats")) {
      try {
        LocalDateTime.parse(split[split.length - 5]);
        return true;
      } catch (DateTimeParseException e) {
        return false;
      }
    }
    return false;
  }

  /**
   * Constructs a command to show the calendar status at a specific datetime.
   */
  private Command makeShowStatusOnDT(String input) throws CommandExecutionException {
    String[] split = input.split(" ");
    try {
      LocalDateTime date = LocalDateTime.parse(split[split.length - 1]);
      return new ShowStatusOnDTCommand(manager.getActiveCalendar(), view, date);
    } catch (DateTimeParseException e) {
      throw new CommandExecutionException();
    }
  }

  /**
   * Constructs a command to print events between two datetime values.
   */
  private Command makePrintEventsDTInterval(String input) throws CommandExecutionException {
    String[] split = input.split(" ");
    try {
      LocalDateTime endDate = LocalDateTime.parse(split[split.length - 1]);
      LocalDateTime startDate = LocalDateTime.parse(split[split.length - 3]);
      return new PrintEventsDTIntervalCommand(manager.getActiveCalendar(),
              view, startDate, endDate);
    } catch (DateTimeParseException e) {
      throw new CommandExecutionException();
    }
  }

  /**
   * Constructs a command to print all events occurring on a specific date.
   */
  private Command makePrintEventOnDateCommand(String input) throws CommandExecutionException {
    String[] strArr = input.split(" ");
    try {
      LocalDate date = LocalDate.parse(strArr[strArr.length - 1]);
      return new PrintEventOnDateCommand(manager.getActiveCalendar(), view, date);
    } catch (DateTimeParseException e) {
      throw new CommandExecutionException();
    }
  }


  /**
   * Determines whether the input corresponds to a basic event creation command
   * with specified start and end datetime values.
   */
  private boolean isSimpleCreateCommand(String input) {

    String[] strArr = input.split(" ");
    try {
      LocalDateTime.parse(strArr[strArr.length - 3]);
      LocalDateTime.parse(strArr[strArr.length - 1]);
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }

  /**
   * Checks if the input matches a command for creating a DateTime-based event
   * that repeats a certain number of times.
   */
  private boolean isForNTimesDateTimeCommand(String input) {
    String[] strArr = input.split(" ");
    int len = strArr.length;

    return strArr[len - 1].equals("times") && strArr[len - 7].equals("to");
  }

  /**
   * Constructs a command for creating a series of events that occur on certain weekdays
   * and repeat for a specified number of times between two datetimes.
   */
  private Command makeForNTimesDateTimeCommand(String input) throws CommandExecutionException {
    String subject = this.parseMultiWordSubject(input);

    String[] strArr = input.split(" ");
    int len = strArr.length;
    int times = Integer.parseInt(strArr[len - 2]);
    String weekdays = strArr[len - 4];
    LocalDateTime endDT = LocalDateTime.parse(strArr[len - 6]);
    LocalDateTime startDT = LocalDateTime.parse(strArr[len - 8]);

    return new CreateEventSeriesCommand(manager.getActiveCalendar(), subject,
            startDT, endDT, weekdays, times);
  }

  /**
   * Extracts the subject from the given input.
   *
   * @param input a command string.
   * @return the subject.
   */
  private String parseMultiWordSubject(String input) {
    return this.parseMultiWordSubject(input, 2);
  }

  /**
   * Extracts the subject from the given input.
   *
   * @param input a command string.
   * @return the subject.
   */
  private String parseMultiWordSubject(String input, int index) {
    if (input.indexOf('\"') != -1) {
      int index1 = input.indexOf('\"');
      int index2 = input.indexOf('\"', index1 + 1);
      String subject = input.substring(index1 + 1, index2);
      return subject;
    } else {
      String[] strArr = input.split(" ");
      return strArr[index];
    }
  }

  /**
   * Creates a Command that constructs a single event.
   *
   * @param input the command.
   * @return a CreateEventCommand.
   * @throws CommandExecutionException if parsing fails.
   */
  private Command makeSimpleCreateCommand(String input) throws CommandExecutionException {
    String subject = this.parseMultiWordSubject(input);
    String[] strArr = input.split(" ");
    return new CreateEventCommand(manager.getActiveCalendar(), subject,
            strArr[strArr.length - 3], strArr[strArr.length - 1]);
  }
}

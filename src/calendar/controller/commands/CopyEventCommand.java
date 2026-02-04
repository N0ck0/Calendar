
package calendar.controller.commands;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import calendar.model.CalendarManager;
import calendar.model.CalendarModel;
import calendar.model.Event;

/**
 * Represents a Command that copies one or more events from
 * one calendar to another.
 */
public class CopyEventCommand implements Command {

  private CalendarManager manager;
  private String eventName;
  LocalDateTime eventStart;
  String calName;
  LocalDateTime newDateTime;
  LocalDate onDate;
  LocalDate startingDate;
  LocalDate endingDate;
  LocalDate toDate;

  /**
   * Constructs a CopyEventCommand.
   *
   * @param manager   CalendarManager.
   * @param eventName Event to be copied.
   * @param startDT   Event start time.
   * @param calName   Calendar to be copied to.
   * @param newDT     New start time of copied event.
   */
  public CopyEventCommand(CalendarManager manager, String eventName,
                          LocalDateTime startDT, String calName, LocalDateTime newDT) {
    this.manager = manager;
    this.eventName = eventName;
    this.eventStart = startDT;
    this.calName = calName;
    this.newDateTime = newDT;
  }

  /**
   * Constructs a CopyEventCommand.
   *
   * @param manager CalendarManager.
   * @param onDate  Date to copy events from.
   * @param calName Calendar to be copied to.
   * @param toDate  New start date of copied event.
   */
  public CopyEventCommand(CalendarManager manager, LocalDate onDate, String calName,
                          LocalDate toDate) {
    this.manager = manager;
    this.onDate = onDate;
    this.calName = calName;
    this.toDate = toDate;
  }

  /**
   * Constructs a CopyEventCommand.
   *
   * @param manager      CalendarManager.
   * @param startingDate Date to start copying events from.
   * @param endingDate   Date to end copying events from.
   * @param calName      Calendar to be copied to.
   * @param toDate       New start date of copied event.
   * @throws CommandExecutionException if startingDate is after endingDate
   */
  public CopyEventCommand(CalendarManager manager, LocalDate startingDate, LocalDate endingDate,
                          String calName, LocalDate toDate) throws CommandExecutionException {
    if (manager == null || startingDate == null || endingDate == null ||
            calName == null || toDate == null) {
      throw new CommandExecutionException("Parameters can't be null");
    }

    if (startingDate.isAfter(endingDate)) {
      throw new CommandExecutionException("Start date can't be after end date");
    }

    this.manager = manager;
    this.startingDate = startingDate;
    this.endingDate = endingDate;
    this.calName = calName;
    this.toDate = toDate;
  }

  /**
   * Executes this command to copy event(s).
   *
   * @throws CommandExecutionException if command execution fails.
   */
  public void execute() throws CommandExecutionException {
    List<Event> foundEvents = new ArrayList<>();
    CalendarModel newCal = manager.getCalendar(calName);
    if (this.eventName != null) {
      List<Event> eventList = manager.getActiveCalendar().getEvents();
      for (Event event : eventList) {
        if (event.getSubject().equals(this.eventName)
                && event.getStartDateTime().isEqual(this.eventStart)) {
          foundEvents.add(event);
        }
      }
      if (foundEvents.size() == 1) {
        Event newEvent = foundEvents.get(0).cloneMe();
        this.shiftEventStart(newEvent, newDateTime);
        newCal.addEvent(newEvent);
        return;
      } else {
        throw new CommandExecutionException("There is more than one event with same name/time");
      }
    } else if (this.onDate != null) {
      List<Event> eventList = manager.getActiveCalendar().getEvents();
      for (Event event : eventList) {
        if (event.overlapsDate(onDate)) {
          foundEvents.add(event);
        }
      }
      for (Event event : foundEvents) {
        Event newEvent = event.cloneMe();
        this.updateEventDTRange(newEvent, this.onDate, toDate,
                manager.getActiveCalendar().getZoneId(), newCal.getZoneId());
        newCal.addEvent(newEvent);
      }
    } else if (this.startingDate != null) {

      List<Event> eventList = manager.getActiveCalendar().getEvents();
      for (Event event : eventList) {
        if (event.fallsBetweenDates(startingDate, endingDate)) {
          foundEvents.add(event);
        }
      }
      for (Event event : foundEvents) {
        Event newEvent = event.cloneMe();
        this.updateEventDTRange(newEvent, this.startingDate, toDate,
                manager.getActiveCalendar().getZoneId(), newCal.getZoneId());
        newCal.addEvent(newEvent);
      }
    }
  }

  private void shiftEventStart(Event newEvent, LocalDateTime newDateTime)
          throws CommandExecutionException {
    long eventDuration = ChronoUnit.MINUTES.between(newEvent.getStartDateTime(),
            newEvent.getEndDateTime());
    LocalDateTime newEnd = newDateTime.plusMinutes(eventDuration);
    newEvent.modifyProperty("start", newDateTime.toString());
    newEvent.modifyProperty("end", newEnd.toString());
  }

  private void updateEventDT(Event newEvent, LocalDate toDate, ZoneId ogZone, ZoneId newZone)
          throws CommandExecutionException {
    LocalDateTime newDTSameTZ = LocalDateTime.of(toDate, newEvent.getStartDateTime().toLocalTime());
    ZonedDateTime newZDT = newDTSameTZ.atZone(ogZone);
    Instant instant = newZDT.toInstant();
    LocalDateTime newDT = LocalDateTime.ofInstant(instant, newZone);
    this.shiftEventStart(newEvent, newDT);
  }

  private void updateEventDTRange(Event newEvent, LocalDate startDt,
                                  LocalDate toDate, ZoneId ogZone, ZoneId newZone)
          throws CommandExecutionException {
    long daysBetweenCopyDates = ChronoUnit.DAYS.between(startDt, toDate);
    LocalDate newToDate = newEvent.getStartDateTime().toLocalDate().plusDays(daysBetweenCopyDates);
    this.updateEventDT(newEvent, newToDate, ogZone, newZone);
  }
}
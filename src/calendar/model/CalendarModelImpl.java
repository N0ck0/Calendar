
package calendar.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import calendar.controller.commands.CommandExecutionException;

/**
 * Implementation of CalendarModel that stores events and event series.
 */
public class CalendarModelImpl implements CalendarModel {

  private String name;
  private List<Event> events;
  private List<EventSeries> eventSeries;
  private ZoneId zoneId;

  /**
   * Constructs a new CalendarModelImpl object with empty event lists.
   */
  public CalendarModelImpl(String name, ZoneId zoneId) {
    this.name = name;
    this.events = new ArrayList<Event>();
    this.eventSeries = new ArrayList<EventSeries>();
    this.zoneId = zoneId;
  }

  /**
   * Includes the given event in the calendar.
   *
   * @param event the event to be included
   */
  public void addEvent(Event event) throws CommandExecutionException {
    for (Event e : events) {
      if (e.equals(event)) {
        throw new CommandExecutionException("Event already exists");
      }
    }

    this.events.add(event);
  }

  /**
   * Adds a new event series to the calendar.
   *
   * @param eventSeries the event series to add
   */
  public void addEventSeries(EventSeries eventSeries) throws CommandExecutionException {
    for (Event e : eventSeries.getEvents()) {
      for (Event e2 : this.events) {
        if (e.equals(e2)) {
          throw new CommandExecutionException("At least one event in this series conflicts"
                  +
                  "with a preexisting event");
        }
      }
    }
    this.eventSeries.add(eventSeries);
  }

  /**
   * Gets all the events in this calendar.
   *
   * @return List of events
   */
  public List<Event> getEvents() {
    List<Event> returnList = new ArrayList<>();
    returnList.addAll(events);
    for (EventSeries eventSeries : eventSeries) {
      returnList.addAll(eventSeries.getEvents());
    }

    return returnList;
  }


  /**
   * Gets all the event series in this calendar.
   *
   * @return List of EventSeries objects
   */
  public List<EventSeries> getEventSeries() {
    return this.eventSeries;
  }

  /**
   * Gets all the individual events in this calendar except for series ones.
   *
   * @return List of single Event objects not belonging to a series
   */
  public List<Event> getSingleEvents() {
    return this.events;
  }

  /**
   * Gets the timezone/ZoneId of this calendar.
   *
   * @return ZoneId
   */
  public ZoneId getZoneId() {
    return this.zoneId;
  }

  /**
   * Gets the name of this calendar.
   *
   * @return String of name.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Updates the name of this calendar.
   *
   * @param name new name.
   */
  public void updateName(String name) {
    this.name = name;
  }

  /**
   * Updates the timezone of this calendar and converts the existing events
   * to have the same correct time in the new timezone.
   *
   * @param zoneId new timezone.
   * @throws CommandExecutionException if the timezone is invalid or the conversion won't work.
   */
  public void updateZoneId(String zoneId) throws CommandExecutionException {
    try {
      ZoneId newZoneId = ZoneId.of(zoneId);
      ZoneId oldZoneId = this.zoneId;

      this.zoneId = newZoneId;
      this.convertEventsToNewTimezone(this.events, oldZoneId, newZoneId);
      for (EventSeries series : this.eventSeries) {
        this.convertEventsToNewTimezone(series.getEvents(), oldZoneId, newZoneId);
      }
    } catch (Exception e) {
      throw new CommandExecutionException("Invalid timezone: " + zoneId);
    }
  }

  /**
   * Converts a list of events from one timezone to another while keeping
   * the overall time.
   *
   * @param eventsToConvert list of events to convert
   * @param fromZone original timezone
   * @param toZone target timezone
   * @throws CommandExecutionException if conversion fails
   */
  private void convertEventsToNewTimezone(List<Event> eventsToConvert,
                                          ZoneId fromZone, ZoneId toZone)
          throws CommandExecutionException {
    for (Event event : eventsToConvert) {
      // for converting start time
      LocalDateTime oldStart = event.getStartDateTime();
      ZonedDateTime zonedStart = oldStart.atZone(fromZone);
      ZonedDateTime convertedStart = zonedStart.withZoneSameInstant(toZone);
      LocalDateTime newStart = convertedStart.toLocalDateTime();

      // for converting end time
      LocalDateTime oldEnd = event.getEndDateTime();
      ZonedDateTime zonedEnd = oldEnd.atZone(fromZone);
      ZonedDateTime convertedEnd = zonedEnd.withZoneSameInstant(toZone);
      LocalDateTime newEnd = convertedEnd.toLocalDateTime();

      event.modifyProperty("start", newStart.toString());
      event.modifyProperty("end", newEnd.toString());
    }
  }

  /**
   * Finds all events that occur on a specific date.
   *
   * @param date the date to search for
   * @return list of events occurring on that date
   */
  public List<Event> getEventsOnDate(java.time.LocalDate date) {
    List<Event> eventsOnDate = new ArrayList<>();
    for (Event event : this.getEvents()) {
      if (event.overlapsDate(date)) {
        eventsOnDate.add(event);
      }
    }
    return eventsOnDate;
  }

  /**
   * Finds all events that happen within a date range.
   *
   * @param startDate start of the range (inclusive)
   * @param endDate end of the range (inclusive)
   * @return list of events in the range
   */
  public List<Event> getEventsInRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
    List<Event> eventsInRange = new ArrayList<>();
    for (Event event : this.getEvents()) {
      if (event.fallsBetweenDates(startDate, endDate)) {
        eventsInRange.add(event);
      }
    }
    return eventsInRange;
  }

  /**
   * Checks if the calendar has any events at a specific date and time.
   *
   * @param dateTime the specific moment to check
   * @return true if there are events at that time, false otherwise
   */
  public boolean isBusyAt(LocalDateTime dateTime) {
    for (Event event : this.getEvents()) {
      if (event.occursDuring(dateTime)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Removes an event from the calendar if it exists.
   *
   * @param eventToRemove the event to remove
   * @return true if event was removed, false if not found
   */
  public boolean removeEvent(Event eventToRemove) {
    if (this.events.remove(eventToRemove)) {
      return true;
    }
    for (EventSeries series : this.eventSeries) {
      if (series.getEvents().remove(eventToRemove)) {
        return true;
      }
    }

    return false;
  }

}
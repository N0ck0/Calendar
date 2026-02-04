package calendar.model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import calendar.controller.commands.CommandExecutionException;

/**
 * Represents a single calendar that is able to store and manage single or
 * series events and can get all events, get single events, or get the EventSeries.
 */
public interface CalendarModel {

  /**
   * Includes the given event in the calendar.
   *
   * @param event the event to be included.
   */
  void addEvent(Event event) throws CommandExecutionException;

  /**
   * Includes the given event series in the calendar.
   *
   * @param series the series to be included.
   */
  void addEventSeries(EventSeries series) throws CommandExecutionException;

  /**
   * Gets all the events in this calendar.
   *
   * @return List of events;
   */
  List<Event> getEvents();

  /**
   * Gets all the single events (not in event series) in this calendar.
   *
   * @return List of Events.
   */
  List<Event> getSingleEvents();

  /**
   * Gets all the EventSeries in this calendar.
   *
   * @return List of EventSeries.
   */
  List<EventSeries> getEventSeries();


  /**
   * Gets the timezone/ZoneId of this Calendar.
   *
   * @return ZoneId.
   */
  ZoneId getZoneId();

  /**
   * Gets the name of this calendar.
   *
   * @return String of name.
   */
  public String getName();

  /**
   * Updates the name of this calendar.
   * @param name new name.
   */
  public void updateName(String name);

  /**
   * Updates the timezone of this calendar.
   * @param zoneId new timezone.
   */
  public void updateZoneId(String zoneId) throws CommandExecutionException;

  /**
   * Finds all events that happen on a specific date.
   *
   * @param date the date to search for
   * @return list of events happening on that date
   */
  List<Event> getEventsOnDate(LocalDate date);

  /**
   * Removes an event from the calendar.
   *
   * @param event the event to remove
   * @return true if the event was found and removed, false otherwise
   */
  boolean removeEvent(Event event);
}

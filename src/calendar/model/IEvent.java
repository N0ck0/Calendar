package calendar.model;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import calendar.controller.commands.CommandExecutionException;

/**
 * Represents an Event with at least a subject, start, and end.
 */
public interface IEvent {

  /**
   * Returns a new Event similar to this Event that occurs on the specified date.
   *
   * @param date new date to recreate this Event on.
   * @return Event.
   */
  public Event onThisDate(LocalDateTime date) throws CommandExecutionException;

  /**
   * Modifies the property of this Event to contain the given value.
   *
   * @param property property to be modified.
   * @param value    value to change property to.
   * @throws CommandExecutionException if the command that called this method contained
   *                                   faulty arguments.
   */
  public void modifyProperty(String property, String value) throws CommandExecutionException;

  /**
   * Returns the dateTime this Event starts on.
   *
   * @return LocalDateTime.
   */
  public LocalDateTime getStartDateTime();

  /**
   * Returns the dateTime this Event ends on.
   *
   * @return LocalDateTime.
   */
  public LocalDateTime getEndDateTime();

  /**
   * Returns the subject of this Event.
   *
   * @return String.
   */
  public String getSubject();

  /**
   * Checks if the event conflicts with another event by having the same information.
   *
   * @param other another event to check against
   * @return true if the events conflict and false otherwise
   */
  public boolean conflictsWith(Event other);

  /**
   * Overrides the equals method to indicate if another object is equal to this one based on a
   * new definition.
   *
   * @param obj the object to be compared with
   * @return true if this object is the same as the obj input and false otherwise
   */
  @Override
  public boolean equals(Object obj);

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for this object
   */
  @Override
  public int hashCode();

  /**
   * Appends a String that represents this Event.
   *
   * @param out Where to append this Event's string.
   * @throws IOException If append fails.
   */
  public void toString(Appendable out) throws IOException;

  /**
   * Gets the date of this Event's startTime.
   *
   * @return LocalDate.
   */
  public LocalDate getDate();


  /**
   * Determines if this Event occurs during the given LocalDateTime.
   *
   * @param dt LocalDateTime
   * @return boolean describing result.
   */
  public boolean occursDuring(LocalDateTime dt);

  /**
   * Determines if this Event overlaps with the given date.
   *
   * @param date The specified date.
   * @return boolean describing result.
   */
  public boolean overlapsDate(LocalDate date);

  /**
   * Determines if this Event overlaps at all with the given start/end dates.
   *
   * @param start start date of range.
   * @param end   end date of range.
   * @return boolean describing result.
   */
  public boolean fallsBetweenDates(LocalDate start, LocalDate end);

  /**
   * Returns a copy of this Event.
   *
   * @return Event.
   * @throws CommandExecutionException if modifyProperty fails.
   */
  public Event cloneMe() throws CommandExecutionException;
}

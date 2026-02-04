package calendar.model;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import calendar.controller.commands.CommandExecutionException;

/**
 * Represents a calendar event.
 */
public class Event implements IEvent, Comparable<Event> {

  private String subject;
  private LocalDateTime startDateTime;
  private String description;
  private LocalDateTime endDateTime;
  private String location;
  private String status;

  /**
   * Constructs an {@code calendar.model.Event} object with specified times within a date.
   *
   * @param subject       the event subject
   * @param startDateTime when the event starts
   * @param endDateTime   when the event ends
   */
  public Event(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime) {

    if (this.validDates(startDateTime, endDateTime)) {
      this.subject = subject;
      this.startDateTime = startDateTime;
      this.endDateTime = endDateTime;
    }
  }

  /**
   * Constructs an {@code calendar.model.Event} object that is all day.
   *
   * @param subject the event subject
   * @param date    the date of the all day event
   */
  public Event(String subject, LocalDate date) {
    this.subject = subject;
    this.startDateTime = LocalDateTime.of(date, LocalTime.parse("08:00"));
    this.endDateTime = LocalDateTime.of(date, LocalTime.parse("17:00"));
  }

  /**
   * Returns a new Event similar to this Event that occurs on the specified date.
   *
   * @param date new date to recreate this Event on.
   * @return Event.
   */
  public Event onThisDate(LocalDateTime date) {
    return new Event(this.subject, LocalDateTime.of(date.getYear(),
            date.getMonth(), date.getDayOfMonth(), this.startDateTime.getHour(),
            this.startDateTime.getMinute()), LocalDateTime.of(date.getYear(),
            date.getMonth(), date.getDayOfMonth(), this.endDateTime.getHour(),
            this.endDateTime.getMinute()));
  }

  /**
   * Compares two Events based on their startDateTime.
   *
   * @param other the other Event to be compared.
   * @return negative int if this 'before' other, 0 if this 'equal to' other, positive int if this
   *         is 'after' other.
   */
  public int compareTo(Event other) {
    return this.startDateTime.compareTo(other.startDateTime);
  }

  /**
   * Modifies the property of this Event to contain the given value.
   *
   * @param property property to be modified.
   * @param value    value to change property to.
   * @throws CommandExecutionException if the command that called this method contained
   *                                   faulty arguments.
   */
  public void modifyProperty(String property, String value) throws CommandExecutionException {
    try {
      switch (property) {
        case "subject":
          this.subject = value;
          break;
        case "start":
          this.startDateTime = LocalDateTime.parse(value);
          break;
        case "end":
          this.endDateTime = LocalDateTime.parse(value);
          break;
        case "description":
          this.description = value;
          break;
        case "location":
          this.location = value;
          break;
        case "status":
          this.status = value;
          break;
        default:
          throw new CommandExecutionException("Unknown property: " + property);

      }
    } catch (DateTimeParseException e) {
      throw new CommandExecutionException("Invalid date: " + value);
    }
  }

  /**
   * Returns the dateTime this Event starts on.
   *
   * @return LocalDateTime.
   */
  public LocalDateTime getStartDateTime() {
    return this.startDateTime;
  }

  /**
   * Returns the dateTime this Event ends on.
   *
   * @return LocalDateTime.
   */
  public LocalDateTime getEndDateTime() {
    return this.endDateTime;
  }

  /**
   * Returns the subject of this Event.
   *
   * @return String.
   */
  public String getSubject() {
    return this.subject;
  }

  /**
   * Checks if the event conflicts with another event by having the same information.
   *
   * @param other another event to check against
   * @return true if the events conflict and false otherwise
   */
  public boolean conflictsWith(Event other) {
    return this.subject.equals(other.subject)
            &&
            this.startDateTime.isEqual(other.startDateTime)
            &&
            this.endDateTime.isEqual(other.endDateTime);
  }

  private boolean validDates(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    if (startDateTime.isAfter(endDateTime)) {
      throw new IllegalArgumentException("Start date cannot be after end date");
    } else {
      return true;
    }
  }

  /**
   * Overrides the equals method to indicate if another object is equal to this one based on a
   * new definition.
   *
   * @param obj the object to be compared with
   * @return true if this object is the same as the obj input and false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Event)) {
      return false;
    }
    Event that = (Event) obj;
    return this.conflictsWith(that);
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for this object
   */
  @Override
  public int hashCode() {
    int endTimeHash;
    if (this.endDateTime != null) {
      endTimeHash = this.endDateTime.hashCode();
    } else {
      endTimeHash = 0;
    }
    return this.subject.hashCode() * 1000 + this.startDateTime.hashCode() * 100 + endTimeHash;
  }

  /**
   * Appends a String that represents this Event.
   *
   * @param out Where to append this Event's string.
   * @throws IOException If append fails.
   */
  public void toString(Appendable out) throws IOException {
    out.append("• ").append(this.subject);
    if (this.endDateTime != null) {
      out.append(" from ").append(this.startDateTime.toLocalTime().toString())
              .append(" to ").append(this.endDateTime.toLocalTime().toString())
              .append(" on ").append(this.startDateTime.toLocalDate().toString());
    } else {
      out.append(" (All day)");
    }

    if (this.location != null && !this.location.trim().isEmpty()) {
      out.append(" at ").append(this.location);
    }
  }

  /**
   * Gets the date of this Event's startTime.
   *
   * @return LocalDate.
   */
  public LocalDate getDate() {
    return this.startDateTime.toLocalDate();
  }

  /**
   * Determines if this Event occurs during the given LocalDateTime.
   *
   * @param dt LocalDateTime
   * @return boolean describing result.
   */
  public boolean occursDuring(LocalDateTime dt) {
    return (this.startDateTime.isBefore(dt) || this.startDateTime.isEqual(dt))
            && (this.endDateTime.isEqual(dt) || this.endDateTime.isAfter(dt));
  }

  /**
   * Determines if this Event overlaps with the given date.
   *
   * @param date The specified date.
   * @return boolean describing result.
   */
  public boolean overlapsDate(LocalDate date) {
    return this.startDateTime.isBefore(LocalDateTime.of(date,
            LocalTime.parse("00:00")).plusDays(1))
            && this.endDateTime.isAfter(LocalDateTime.of(date,
            LocalTime.parse("23:59")).minusDays(1));
  }

  /**
   * Determines if this Event overlaps at all with the given start/end dates.
   *
   * @param start start date of range.
   * @param end   end date of range.
   * @return boolean describing result.
   */
  public boolean fallsBetweenDates(LocalDate start, LocalDate end) {
    return this.startDateTime.isBefore(LocalDateTime.of(end,
            LocalTime.parse("00:00")).plusDays(1))
            && this.endDateTime.isAfter(LocalDateTime.of(start,
            LocalTime.parse("23:59")).minusDays(1));
  }

  /**
   * Returns a copy of this Event.
   *
   * @return Event.
   * @throws CommandExecutionException if modifyProperty fails.
   */
  public Event cloneMe() throws CommandExecutionException {
    Event newEvent = new Event(this.subject, this.startDateTime, this.endDateTime);
    newEvent.modifyProperty("description", this.description);
    newEvent.modifyProperty("location", this.location);
    newEvent.modifyProperty("status", this.status);
    return newEvent;
  }
}
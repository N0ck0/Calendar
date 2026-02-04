package calendar.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import calendar.controller.commands.CommandExecutionException;

/**
 * Represents a related Series of events on a calendar.
 */
public class EventSeries {

  private List<Event> events;
  private String onDays;

  /**
   * Creates an event series object.
   *
   * @param event     the event that is being modified.
   * @param onDays    the string for the day being represented.
   * @param untilDate the date that is the limit.
   */
  public EventSeries(Event event, String onDays, LocalDate untilDate) {
    if (this.validDayString(onDays)) {
      this.events = this.createEventListUntilDate(event, onDays, untilDate);
      this.onDays = onDays;
    }
  }

  /**
   * Creates an event series object.
   *
   * @param event       the event that is being modified.
   * @param onDays      the string for the day being represented.
   * @param repeatTimes the number of times the event is being repeated.
   */
  public EventSeries(Event event, String onDays, int repeatTimes) {
    if (this.validDayString(onDays)) {
      this.events = this.createEventListRepeatTimes(event, onDays, repeatTimes);
      this.onDays = onDays;
    }
  }

  /**
   * Builds a List of all the Event contained within this EventSeries.
   *
   * @param event     the initial event that is to be repeated.
   * @param onDays    the String representing the repeat days.
   * @param untilDate the date that the Event should repeat until.
   * @return A List of Events.
   */
  private List<Event> createEventListUntilDate(Event event, String onDays, LocalDate untilDate) {
    List<Event> eventList = new ArrayList<>();
    //eventList.add(event);
    LocalDateTime curDate = event.getStartDateTime();

    List<DayOfWeek> dayList = this.getDayListFromString(onDays);

    while (curDate.isBefore(untilDate.atStartOfDay().plusDays(1))) {
      if (dayList.contains(curDate.getDayOfWeek())) {
        eventList.add(event.onThisDate(curDate));
      }
      curDate = curDate.plusDays(1);
    }

    return eventList;
  }

  /**
   * Changes the start time of the event corresponding to the given arguments.
   *
   * @param model     CalendarModel.
   * @param subject   Event subject.
   * @param startTime event starttime.
   * @param endTime   event endtime.
   * @param newValue  new starttime.
   * @throws CommandExecutionException if execution fails.
   */
  public void changeStartTime(CalendarModel model, String subject,
                              LocalDateTime startTime, LocalDateTime endTime,
                              String newValue) throws CommandExecutionException {
    Event tempEvent = new Event(subject, startTime, endTime);
    for (Event event : this.events) {
      if (event.conflictsWith(tempEvent)) {
        event.modifyProperty("start", newValue);
        this.events.remove(event);
        model.addEvent(event);
      }
    }
  }

  /**
   * Determines if this EventSeries contains an event corresponding to the given
   * arguments.
   *
   * @param subject   event subject.
   * @param startTime event starttime.
   * @return boolean describing result.
   */
  public boolean containsEvent(String subject, LocalDateTime startTime) {
    for (Event event : this.events) {
      if (event.getSubject().equals(subject) && event.getStartDateTime().isEqual(startTime)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Modifies the event in this EventSeries that corresponds to the given
   * arguments. Also modifies all events that come after that event in the same manner.
   *
   * @param model    calendar model.
   * @param subject  event subject.
   * @param dateTime event date time.
   * @param property event property to modify.
   * @param newValue new value of modifying property.
   * @throws CommandExecutionException if execution fails.
   */
  public void changeAllAfter(CalendarModel model, String subject, LocalDateTime dateTime,
                             String property, String newValue) throws CommandExecutionException {
    if (property.equals("start")) {
      boolean reached = false;
      for (Event event : this.events) {
        if (reached) {
          event.modifyProperty(property, newValue);
          this.events.remove(event);
          model.addEvent(event);
        } else if (event.getStartDateTime().isEqual(dateTime)
                && event.getSubject().equals(subject)) {
          reached = true;
          event.modifyProperty(property, newValue);
          this.events.remove(event);
          model.addEvent(event);
        }
      }
    } else {
      boolean reached = false;
      for (Event event : this.events) {
        if (reached) {
          event.modifyProperty(property, newValue);
        } else if (event.getStartDateTime().isEqual(dateTime)
                && event.getSubject().equals(subject)) {
          reached = true;
          event.modifyProperty(property, newValue);
        }
      }
    }
  }

  /**
   * Modifies the event in this EventSeries that corresponds to the given
   * arguments. Also modifies all other events in the same manner.
   *
   * @param model    calendar model.
   * @param subject  event subject.
   * @param dateTime event date time.
   * @param property event property to modify.
   * @param newValue new value of modifying property.
   * @throws CommandExecutionException if execution fails.
   */
  public void changeAll(CalendarModel model, String subject, LocalDateTime dateTime,
                        String property, String newValue) throws CommandExecutionException {
    if (property.equals("start")) {
      for (Event event : this.events) {
        event.modifyProperty(property, newValue);
      }
      this.dissolveSeries(model);
    } else {
      for (Event event : this.events) {
        event.modifyProperty(property, newValue);
      }
    }
  }

  /**
   * Converts all Events in this EventSeries into singular Events in the model.
   *
   * @param model the CalendarModel.
   * @throws CommandExecutionException if adding event fails.
   */
  private void dissolveSeries(CalendarModel model) throws CommandExecutionException {
    for (int i = 0; i < this.events.size(); i += 0) {
      Event event = this.events.get(i);
      this.events.remove(i);
      model.addEvent(event);
    }
  }


  /**
   * Builds a List of all the Events contained within this EventSeries.
   *
   * @param event       the initial event that is to be repeated.
   * @param onDays      the String representing the repeat days.
   * @param repeatTimes the amount of times this day should be repeated.
   * @return A List of Events.
   */
  private List<Event> createEventListRepeatTimes(Event event, String onDays, int repeatTimes) {
    int toRepeat = repeatTimes;
    List<Event> eventList = new ArrayList<>();
    //eventList.add(event);
    LocalDateTime curDate = event.getStartDateTime();

    List<DayOfWeek> dayList = this.getDayListFromString(onDays);

    while (toRepeat > 0) {
      if (dayList.contains(curDate.getDayOfWeek())) {
        eventList.add(event.onThisDate(curDate));
        toRepeat -= 1;
      }
      curDate = curDate.plusDays(1);
    }

    return eventList;
  }

  /**
   * Creates a list of DayOfWeek describing which days an EventSeries is to repeat on.
   *
   * @param onDays A String representing the days this EventSeries is to repeat on,
   *               e.g. "MRU".
   * @return List of DayOfWeek enum.
   */
  private List<DayOfWeek> getDayListFromString(String onDays) throws IllegalArgumentException {
    String[] letterList = {"M", "T", "W", "R", "F", "S", "U"};
    List<String> dayStringList = Arrays.asList(letterList);
    if (onDays.isEmpty()) {
      throw new IllegalArgumentException("OnDays cannot be empty");
    }
    for (Character c : onDays.toCharArray()) {
      String str = c.toString();
      if (!dayStringList.contains(str)) {
        throw new IllegalArgumentException("Given invalid dayString");
      }
    }
    List<DayOfWeek> dayList = new ArrayList<>();
    if (onDays.contains("M")) {
      dayList.add(DayOfWeek.MONDAY);
    }
    if (onDays.contains("T")) {
      dayList.add(DayOfWeek.TUESDAY);
    }
    if (onDays.contains("W")) {
      dayList.add(DayOfWeek.WEDNESDAY);
    }
    if (onDays.contains("R")) {
      dayList.add(DayOfWeek.THURSDAY);
    }
    if (onDays.contains("F")) {
      dayList.add(DayOfWeek.FRIDAY);
    }
    if (onDays.contains("S")) {
      dayList.add(DayOfWeek.SATURDAY);
    }
    if (onDays.contains("U")) {
      dayList.add(DayOfWeek.SUNDAY);
    }

    return dayList;
  }


  /**
   * Gets all the Events in this EventSeries.
   *
   * @return List of Events.
   */
  public List<Event> getEvents() {
    return this.events;
  }

  private boolean validDayString(String onDays) throws IllegalArgumentException {
    String[] letterList = {"M", "T", "W", "R", "F", "S", "U"};
    List<String> dayStringList = Arrays.asList(letterList);
    if (onDays.isEmpty()) {
      throw new IllegalArgumentException("OnDays cannot be empty");
    }
    for (Character c : onDays.toCharArray()) {
      String str = c.toString();
      if (!dayStringList.contains(str)) {
        throw new IllegalArgumentException("Given invalid dayString");
      }
    }
    return true;
  }

}

package calendar.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;

import calendar.controller.commands.CommandExecutionException;
import calendar.controller.commands.CreateEventSeriesCommand;

/**
 * Represents testing for CreateEventSeriesCommand class.
 */
public class CreateEventSeriesCommandTest {

  private CalendarModel model;

  @Before
  public void setUp() {
    this.model = new CalendarModelImpl("cal1", ZoneId.of("America/New_York"));
  }

  @Test
  public void testCreateEventSeriesCommand() throws CommandExecutionException {
    LocalDateTime start = LocalDateTime.of(2024, 2, 17, 20, 0);
    LocalDateTime end = LocalDateTime.of(2024, 2, 17, 22, 30);
    CreateEventSeriesCommand weeklyCommand = new CreateEventSeriesCommand(this.model,
            "Movie night in dorm", start, end, "F", 6);

    weeklyCommand.execute();
    assertEquals(6, this.model.getEvents().size());
    assertEquals("Movie night in dorm", this.model.getEvents().get(0).getSubject());
  }

  @Test
  public void testCreateEventSeriesCommandInvalidDays() throws CommandExecutionException {
    try {
      LocalDateTime start = LocalDateTime.of(2024, 2, 17, 20, 0);
      LocalDateTime end = LocalDateTime.of(2024, 2, 17, 22, 30);
      CreateEventSeriesCommand weeklyCommand = new CreateEventSeriesCommand(this.model,
              "Movie night in dorm", start, end, "X", 6);
      fail("shouldn't get here");
    } catch (IllegalArgumentException e) {
      //Should get here and pass.
    }
  }

  @Test
  public void testCreateEventSeriesWeekdays() throws CommandExecutionException {
    LocalDateTime start = LocalDateTime.of(2023, 12, 10, 20,
            0);
    LocalDateTime end = LocalDateTime.of(2023, 12, 10, 23, 0);
    CreateEventSeriesCommand dailyCommand = new CreateEventSeriesCommand(this.model,
            "Study for finals", start, end, "MTRF", 3);

    dailyCommand.execute();
    assertEquals(3, this.model.getEvents().size());
    assertEquals(DayOfWeek.MONDAY, this.model.getEvents().get(0).getStartDateTime().getDayOfWeek());
    assertEquals(DayOfWeek.TUESDAY, this.model.getEvents().get(1).
            getStartDateTime().getDayOfWeek());
    assertEquals(DayOfWeek.THURSDAY, this.model.getEvents().get(2).
            getStartDateTime().getDayOfWeek());
    assertEquals("Study for finals", this.model.getEvents().get(0).getSubject());
  }

  @Test
  public void testMondayWednesdayFridayGymSessions() throws CommandExecutionException {
    LocalDateTime start = LocalDateTime.of(2024, 3, 18, 16, 0);
    LocalDateTime end = LocalDateTime.of(2024, 3, 18, 17, 30);
    CreateEventSeriesCommand gymCommand = new CreateEventSeriesCommand(this.model,
            "Workout at gym", start, end, "MWF", 4);

    gymCommand.execute();
    assertEquals(4, this.model.getEvents().size());
    assertEquals("Workout at gym", this.model.getEvents().get(0).getSubject());
  }

  @Test
  public void testTuesdayThursdayLectures() throws CommandExecutionException {
    LocalDateTime start = LocalDateTime.of(2024, 4, 2, 11, 0);
    LocalDateTime end = LocalDateTime.of(2024, 4, 2, 12, 15);
    CreateEventSeriesCommand lectureCommand = new CreateEventSeriesCommand(this.model,
            "Organic Chemistry", start, end, "TR", 8);

    lectureCommand.execute();
    assertEquals(8, this.model.getEvents().size());
    assertEquals("Organic Chemistry", this.model.getEvents().get(0).getSubject());
  }

  @Test
  public void testSundayOnlyEvents() throws CommandExecutionException {
    LocalDateTime start = LocalDateTime.of(2024, 3, 10, 10, 0);
    LocalDateTime end = LocalDateTime.of(2024, 3, 10, 11, 30);
    CreateEventSeriesCommand sundayCommand = new CreateEventSeriesCommand(this.model,
            "Sunday brunch with friends", start, end, "U", 5);

    sundayCommand.execute();
    assertEquals(5, this.model.getEvents().size());
    assertEquals("Sunday brunch with friends",
            this.model.getEvents().get(0).getSubject());
  }

  @Test
  public void testSingleTimeCommand() throws CommandExecutionException {
    LocalDateTime start = LocalDateTime.of(2024, 5, 15, 14, 0);
    LocalDateTime end = LocalDateTime.of(2024, 5, 15, 15, 0);
    CreateEventSeriesCommand singleCommand = new CreateEventSeriesCommand(this.model,
            "Final exam review", start, end, "W", 1);

    singleCommand.execute();
    assertEquals(1, this.model.getEvents().size());
    assertEquals("Final exam review", this.model.getEvents().get(0).getSubject());
  }

  @Test
  public void testLongSeries() throws CommandExecutionException {
    LocalDateTime start = LocalDateTime.of(2024, 1, 8, 9, 0);
    LocalDateTime end = LocalDateTime.of(2024, 1, 8, 10, 0);
    CreateEventSeriesCommand longCommand = new CreateEventSeriesCommand(this.model,
            "Morning jog", start, end, "MTWRFSU", 15);

    longCommand.execute();
    assertEquals(15, this.model.getEvents().size());
    assertEquals("Morning jog", this.model.getEvents().get(0).getSubject());
  }

  @Test
  public void testWeekendOnlyStudyGroup() throws CommandExecutionException {
    LocalDateTime start = LocalDateTime.of(2024, 3, 16, 14, 0);
    LocalDateTime end = LocalDateTime.of(2024, 3, 16, 16, 0);
    CreateEventSeriesCommand weekendCommand = new CreateEventSeriesCommand(this.model,
            "Weekend study group", start, end, "SU", 6);

    weekendCommand.execute();
    assertEquals(6, this.model.getEvents().size());
    assertEquals("Weekend study group", this.model.getEvents().get(0).getSubject());
  }

  @Test
  public void testMultipleSeriesInSame() throws CommandExecutionException {
    //first series
    LocalDateTime start1 = LocalDateTime.of(2024, 3, 18, 10,
            0);
    LocalDateTime end1 = LocalDateTime.of(2024, 3, 18, 11, 0);
    CreateEventSeriesCommand firstCommand = new CreateEventSeriesCommand(this.model,
            "CS lecture", start1, end1, "MWF", 3);

    //second series
    LocalDateTime start2 = LocalDateTime.of(2024, 3, 19, 15,
            0);
    LocalDateTime end2 = LocalDateTime.of(2024, 3, 19, 16, 0);
    CreateEventSeriesCommand secondCommand = new CreateEventSeriesCommand(this.model,
            "Office hours", start2, end2, "TR", 2);

    firstCommand.execute();
    secondCommand.execute();

    assertEquals(5, this.model.getEvents().size());
  }

  @Test
  public void testQuotedSubjectInSeries() throws CommandExecutionException {
    LocalDateTime start = LocalDateTime.of(2024, 4, 1, 19, 0);
    LocalDateTime end = LocalDateTime.of(2024, 4, 1, 21, 0);
    CreateEventSeriesCommand quotedCommand = new CreateEventSeriesCommand(this.model,
            "\"Game night\" with roommates", start, end, "MW", 4);

    quotedCommand.execute();
    assertEquals(4, this.model.getEvents().size());
    assertEquals("\"Game night\" with roommates",
            this.model.getEvents().get(0).getSubject());
  }

  @Test
  public void testSpecialCharsInSubject() throws CommandExecutionException {
    LocalDateTime start = LocalDateTime.of(2024, 2, 20, 13, 0);
    LocalDateTime end = LocalDateTime.of(2024, 2, 20, 14, 0);
    CreateEventSeriesCommand specialCommand = new CreateEventSeriesCommand(this.model,
            "Dr. Johnson's Lab @ Room #301", start, end, "TR", 6);

    specialCommand.execute();
    assertEquals(6, this.model.getEvents().size());
    assertEquals("Dr. Johnson's Lab @ Room #301",
            this.model.getEvents().get(0).getSubject());
  }

  @Test
  public void testMidnightEventSeries() throws CommandExecutionException {
    LocalDateTime start = LocalDateTime.of(2023, 10,
            31, 0, 0);
    LocalDateTime end = LocalDateTime.of(2024, 11, 1, 0, 0);
    CreateEventSeriesCommand midnightCommand = new CreateEventSeriesCommand(this.model,
            "New Year celebration", start, end, "U", 2);

    midnightCommand.execute();
    assertEquals(2, this.model.getEvents().size());
    assertEquals("New Year celebration", this.model.getEvents().get(0).getSubject());
  }

  @Test
  public void testEveryDayOfWeekSeries() throws CommandExecutionException {
    LocalDateTime start = LocalDateTime.of(2024, 6, 3, 7, 0);
    LocalDateTime end = LocalDateTime.of(2024, 6, 3, 7, 45);
    CreateEventSeriesCommand dailyCommand = new CreateEventSeriesCommand(this.model,
            "Summer workout routine", start, end, "MTWRFSU", 7);

    dailyCommand.execute();
    assertEquals(7, this.model.getEvents().size());
    assertEquals("Summer workout routine", this.model.getEvents().get(0).getSubject());
  }

  @Test
  public void testLongEventName() throws CommandExecutionException {
    LocalDateTime start = LocalDateTime.of(2024, 4, 15, 18, 0);
    LocalDateTime end = LocalDateTime.of(2024, 4, 15, 20, 0);
    CreateEventSeriesCommand longNameCommand = new CreateEventSeriesCommand(this.model,
            "Super important weekly group project meeting for advanced math class",
            start, end, "M", 5);

    longNameCommand.execute();
    assertEquals(5, this.model.getEvents().size());
    assertEquals("Super important weekly group project meeting for advanced math class",
            this.model.getEvents().get(0).getSubject());
  }

  @Test
  public void testEarlyMorningStudySessions() throws CommandExecutionException {
    LocalDateTime start = LocalDateTime.of(2024, 5, 6, 6, 30);
    LocalDateTime end = LocalDateTime.of(2024, 5, 6, 8, 0);
    CreateEventSeriesCommand earlyCommand = new CreateEventSeriesCommand(this.model,
            "Early morning study sessions", start, end, "MTWRF", 4);

    earlyCommand.execute();
    assertEquals(4, this.model.getEvents().size());
    assertEquals("Early morning study sessions",
            this.model.getEvents().get(0).getSubject());
    assertEquals(6, this.model.getEvents().get(0).getStartDateTime().getHour());
    assertEquals(30, this.model.getEvents().get(0).getStartDateTime().getMinute());
  }

  @Test
  public void testTwoOccurrences() throws CommandExecutionException {
    LocalDateTime start = LocalDateTime.of(2024, 3, 20, 16, 0);
    LocalDateTime end = LocalDateTime.of(2024, 3, 20, 17, 30);
    CreateEventSeriesCommand shortCommand = new CreateEventSeriesCommand(this.model,
            "Job interview prep", start, end, "W", 2);

    shortCommand.execute();
    assertEquals(2, this.model.getEvents().size());
    assertEquals("Job interview prep", this.model.getEvents().get(0).getSubject());
    assertEquals("Job interview prep", this.model.getEvents().get(1).getSubject());
  }
}
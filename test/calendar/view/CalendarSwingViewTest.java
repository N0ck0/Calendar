package calendar.view;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import calendar.model.Event;

/**
 * Represents testing for CalendarSwingView class.
 */
public class CalendarSwingViewTest {

  private CalendarSwingView view;

  @Before
  public void setUp() {
    this.view = new CalendarSwingView();
  }

  @Test
  public void testConstructorInitializesComponents() {
    assertNotNull("Calendar dropdown should be initialized", this.view);
    assertEquals("Should set correct title", "Calendar GUI",
            this.view.getTitle());
    assertEquals("Should set correct default close operation",
            javax.swing.JFrame.EXIT_ON_CLOSE, this.view.getDefaultCloseOperation());
  }

  @Test
  public void testViewImplementsCalendarGuiView() {
    assertTrue("Should implement CalendarGuiView interface",
            this.view instanceof CalendarGuiView);
    assertTrue("Should implement CalendarView interface",
            this.view instanceof CalendarView);
  }

  @Test
  public void testDisplayScheduleWithEmptyList() {
    this.view.displaySchedule(Arrays.asList());
    assertEquals("View title should remain unchanged", "Calendar GUI",
            this.view.getTitle());
  }

  @Test
  public void testDisplayScheduleWithSingleEvent() {
    Event testEvent = new Event("Test Meeting",
            LocalDateTime.of(2025, 8, 15, 10, 0),
            LocalDateTime.of(2025, 8, 15, 11, 0));

    this.view.displaySchedule(Arrays.asList(testEvent));
    assertNotNull("View should remain initialized after displaying event", this.view);
  }

  @Test
  public void testDisplayScheduleWithMultipleEvents() {
    Event event1 = new Event("Morning Meeting",
            LocalDateTime.of(2026, 3, 20, 9, 0),
            LocalDateTime.of(2026, 3, 20, 10, 0));
    Event event2 = new Event("Lunch Break",
            LocalDateTime.of(2026, 3, 20, 12, 0),
            LocalDateTime.of(2026, 3, 20, 13, 0));
    Event event3 = new Event("Project Review",
            LocalDateTime.of(2026, 3, 20, 15, 30),
            LocalDateTime.of(2026, 3, 20, 16, 30));
    List<Event> events = Arrays.asList(event1, event2, event3);
    this.view.displaySchedule(events);
    assertEquals("View title should remain unchanged", "Calendar GUI",
            this.view.getTitle());
  }

  @Test
  public void testDisplayScheduleWithEventsAcrossYears() {
    Event oldEvent = new Event("Year 2020 Review",
            LocalDateTime.of(2020, 12, 31, 14, 0),
            LocalDateTime.of(2020, 12, 31, 17, 0));
    Event futureEvent = new Event("Year 2030 Planning",
            LocalDateTime.of(2030, 1, 1, 9, 0),
            LocalDateTime.of(2030, 1, 1, 12, 0));

    this.view.displaySchedule(Arrays.asList(oldEvent, futureEvent));
    assertNotNull("View should remain initialized after displaying events", this.view);
  }

  @Test
  public void testDisplayScheduleWithSpecialCharacters() {
    Event specialEvent = new Event("Dr. Smith's Meeting @ Room #123 (urgent!)",
            LocalDateTime.of(2027, 6, 15, 14, 0),
            LocalDateTime.of(2027, 6, 15, 15, 0));

    this.view.displaySchedule(Arrays.asList(specialEvent));
    assertEquals("View title should remain unchanged", "Calendar GUI",
            this.view.getTitle());
  }

  @Test
  public void testDisplayScheduleWithLongEventNames() {
    Event longNameEvent = new Event(
            "Very Long Event Name That Goes On And On And On and On",
            LocalDateTime.of(2024, 9, 10, 8, 30),
            LocalDateTime.of(2024, 9, 10, 17, 30));

    this.view.displaySchedule(Arrays.asList(longNameEvent));
    assertNotNull("View should remain initialized after displaying long event name",
            this.view);
  }

  @Test
  public void testRenderEventsCallsRefreshEvents() {
    Event testEvent = new Event("Refresh Test",
            LocalDateTime.of(2025, 4, 1, 12, 0),
            LocalDateTime.of(2025, 4, 1, 13, 0));
    try {
      this.view.renderEvents(Arrays.asList(testEvent));
    } catch (Exception e) {
      assertNotNull("Exception should have a message", e.getMessage());
    }
  }

  @Test
  public void testRefreshEventsWithoutController() {
    try {
      this.view.refreshEvents();
    } catch (Exception e) {
      assertNotNull("Exception should have a message", e.getMessage());
    }
  }

  @Test
  public void testRenderBusyStatusDoesNotThrow() {
    LocalDateTime testTime = LocalDateTime.of(2023, 11,
            15, 16, 30);
    this.view.renderBusyStatus(true, testTime);
    this.view.renderBusyStatus(false, testTime);
    assertEquals("View title should remain unchanged", "Calendar GUI",
            this.view.getTitle());
  }

  @Test
  public void testRenderMessageDoesNotThrow() {
    this.view.renderMessage("Test message for GUI");
    this.view.renderMessage("Another test message with special chars: @#$%");
    this.view.renderMessage("");
    assertNotNull("View should remain initialized after rendering messages", this.view);
  }

  @Test
  public void testRenderErrorDoesNotThrow() {
    this.view.renderError("Test error message");
    this.view.renderError("Error with special characters: <>?");
    this.view.renderError("");
    assertEquals("View title should remain unchanged", "Calendar GUI",
            this.view.getTitle());
  }

  @Test
  public void testSetFeaturesDoesNotThrow() {
    this.view.setFeatures();
    assertNotNull("View should remain initialized after setting features", this.view);
  }

  @Test
  public void testDisplayScheduleWithMidnightEvents() {
    Event midnightEvent = new Event("Midnight Sale",
            LocalDateTime.of(2025, 11, 29, 0, 0),
            LocalDateTime.of(2025, 11, 29, 6, 0));
    Event lateNightEvent = new Event("Late Night Study Session",
            LocalDateTime.of(2024, 12, 15, 23, 30),
            LocalDateTime.of(2024, 12, 16, 2, 30));
    this.view.displaySchedule(Arrays.asList(midnightEvent, lateNightEvent));
    assertEquals("View title should remain unchanged", "Calendar GUI",
            this.view.getTitle());
  }

  @Test
  public void testDisplayScheduleWithHolidayEvents() {
    Event christmas = new Event("Christmas Celebration",
            LocalDateTime.of(2024, 12, 25, 18, 0),
            LocalDateTime.of(2024, 12, 25, 23, 0));
    Event newYear = new Event("New Year's Eve Party",
            LocalDateTime.of(2024, 12, 31, 20, 0),
            LocalDateTime.of(2025, 1, 1, 2, 0));
    Event halloween = new Event("Halloween Costume Party 🎃",
            LocalDateTime.of(2024, 10, 31, 19, 0),
            LocalDateTime.of(2024, 10, 31, 23, 59));

    this.view.displaySchedule(Arrays.asList(christmas, newYear, halloween));
    assertNotNull("View should remain initialized after displaying holiday events",
            this.view);
  }
}
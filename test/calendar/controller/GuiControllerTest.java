package calendar.controller;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import calendar.controller.commands.CommandExecutionException;
import calendar.model.CalendarManager;
import calendar.model.Event;
import calendar.model.GuiCalendarManager;
import calendar.model.GuiCalendarModel;
import calendar.model.GuiCalendarModelImpl;
import calendar.view.CalendarGuiView;
import calendar.view.CalendarSwingView;

/**
 * Represents testing for GuiController class.
 */
public class GuiControllerTest {

  private GuiController controller;
  private GuiCalendarManager manager;
  private MockGuiView mockView;

  private static class MockGuiView implements CalendarGuiView {
    private String lastError;
    private String lastMessage;
    private List<Event> lastEvents;
    private boolean featuresSet = false;
    private boolean eventsRefreshed = false;

    //for mocks
    @Override
    public void setFeatures() {
      this.featuresSet = true;
    }

    @Override
    public void refreshEvents() {
      this.eventsRefreshed = true;
    }

    @Override
    public void displaySchedule(List<Event> events) {
      this.lastEvents = events;
    }

    /**
     * This method is intentionally empty for mock testing.
     *
     * @param controller the GUI controller
     * @throws CommandExecutionException if the controller info is invalid
     */
    @Override
    public void setController(GuiController controller) throws CommandExecutionException {
      // This method is intentionally empty because the mock view does not need to
      // store or interact with the controller reference. The mock is designed to
      // track method calls rather than perform actual controller instructions.
    }

    @Override
    public void renderMessage(String message) {
      this.lastMessage = message;
    }

    @Override
    public void renderError(String error) {
      this.lastError = error;
    }

    @Override
    public void renderEvents(List<Event> events) {
      this.lastEvents = events;
    }

    /**
     * This method is intentionally left empty for mock testing.
     *
     * @param busy     whether the user is busy
     * @param dateTime the date and time being checked
     */
    @Override
    public void renderBusyStatus(boolean busy, LocalDateTime dateTime) {
      // This method is intentionally empty because the mock view doesn't need to
      // display busy status to a user interface. The mock is only used to test controller
      // logic, and not view capabilities.
    }

    public String getLastError() {
      return lastError;
    }

    public String getLastMessage() {
      return lastMessage;
    }

    public List<Event> getLastEvents() {
      return lastEvents;
    }

    public boolean isFeaturesSet() {
      return featuresSet;
    }

    public boolean isEventsRefreshed() {
      return eventsRefreshed;
    }
  }

  @Before
  public void setUp() throws CommandExecutionException {
    this.manager = new GuiCalendarManager();
    this.mockView = new MockGuiView();
    GuiCalendarModel model = new GuiCalendarModelImpl("test", ZoneId.systemDefault());
    this.manager.addCalendar(model);
    this.manager.switchToCalendar("test");
    this.controller = new GuiController(this.manager, this.mockView);
  }

  @Test
  public void testGUIHasDefaultCalendar() {
    try {
      CalendarGuiView guiView;
      guiView = new CalendarSwingView();
      CalendarManager manager = new GuiCalendarManager();
      GuiController controller = new GuiController(manager, guiView);
      guiView.setController(controller);
      assertEquals("default", manager.getActiveCalendar().getName());
    } catch (Exception e) {
      fail("Something went wrong");
    }
  }

  @Test
  public void testRunInitializesFeatures() {
    this.controller.run();
    assertTrue("Features should be set after run", this.mockView.isFeaturesSet());
    assertTrue("Events should be refreshed after run", this.mockView.isEventsRefreshed());
  }

  @Test
  public void testTenMaxEventsInScheduleView() throws CommandExecutionException {
    LocalDateTime start = LocalDateTime.of(2024, 6, 15, 10, 0);
    LocalDateTime end = LocalDateTime.of(2024, 6, 15, 11, 0);

    int i = 1;
    for (int index = 0; index < 15; index += 1) {
      this.controller.addEvent("Test Meeting" + " " + i, start, end);
      i += 1;
    }

    List<Event> events = this.controller.getEventSchedule();
    assertEquals(10, events.size());
    assertEquals("Test Meeting 1", events.get(0).getSubject());
  }

  @Test
  public void testEditViewFromDate() throws CommandExecutionException {
    LocalDateTime start = LocalDateTime.of(2024, 6, 15, 10, 0);
    LocalDateTime end = LocalDateTime.of(2024, 6, 15, 11, 0);

    int i = 1;
    for (int index = 0; index < 15; index += 1) {
      this.controller.addEvent("Test Meeting" + " " + i, start, end);
      i += 1;
    }

    List<Event> events = this.controller.getEventSchedule();
    assertEquals(10, events.size());
    assertEquals("Test Meeting 1", events.get(0).getSubject());

    this.controller.addEvent("Test Meeting After", start.plusDays(1), end.plusDays(1));

    this.controller.setScheduleStartDate(LocalDate.of(2024, 6, 16));
    assertEquals(1, this.controller.getEventSchedule().size());
    assertEquals("Test Meeting After",
            this.controller.getEventSchedule().get(0).getSubject());
  }

  @Test
  public void testAddValidEvent() throws CommandExecutionException {
    LocalDateTime start = LocalDateTime.of(2024, 6, 15, 10, 0);
    LocalDateTime end = LocalDateTime.of(2024, 6, 15, 11, 0);

    this.controller.addEvent("Test Meeting", start, end);

    List<Event> events = this.manager.getActiveCalendar().getEvents();
    assertEquals(1, events.size());
    assertEquals("Test Meeting", events.get(0).getSubject());
  }

  @Test
  public void testAddEventWithNullSubject() {
    LocalDateTime start = LocalDateTime.of(2024, 6, 15, 10, 0);
    LocalDateTime end = LocalDateTime.of(2024, 6, 15, 11, 0);

    try {
      this.controller.addEvent(null, start, end);
      fail("Should throw CommandExecutionException for null subject");
    } catch (CommandExecutionException e) {
      assertTrue(e.getMessage().contains("Event subject can't be empty"));
    }
  }

  @Test
  public void testAddEventWithEmptySubject() {
    LocalDateTime start = LocalDateTime.of(2007, 7, 3, 10, 0);
    LocalDateTime end = LocalDateTime.of(2007, 7, 3, 12, 0);

    try {
      this.controller.addEvent("  ", start, end);
      fail("Should throw CommandExecutionException for empty subject");
    } catch (CommandExecutionException e) {
      assertTrue(e.getMessage().contains("Event subject can't be empty"));
    }
  }

  @Test
  public void testAddEventWithEqualTimes() {
    LocalDateTime time = LocalDateTime.of(1990, 10, 29, 1, 0);

    try {
      this.controller.addEvent("Test", time, time);
      fail("Should throw CommandExecutionException for equal times");
    } catch (CommandExecutionException e) {
      assertTrue(e.getMessage().contains("Event start time has to be before end time"));
    }
  }

  @Test
  public void testEditEventSuccessfully() throws CommandExecutionException {
    LocalDateTime start = LocalDateTime.of(2001, 2, 15, 10, 0);
    LocalDateTime end = LocalDateTime.of(2024, 6, 15, 11, 0);
    Event originalEvent = new Event("Original", start, end);
    this.manager.getActiveCalendar().addEvent(originalEvent);

    LocalDateTime newStart = LocalDateTime.of(2001, 2,
            15, 14, 0);
    LocalDateTime newEnd = LocalDateTime.of(2024, 6,
            15, 15, 0);
    Event editedEvent = new Event("Edited", newStart, newEnd);

    this.controller.editEvent(originalEvent, editedEvent);

    List<Event> events = this.manager.getActiveCalendar().getEvents();
    assertEquals(1, events.size());
    assertEquals("Edited", events.get(0).getSubject());
  }

  @Test
  public void testEditEventWithNullOriginal() {
    Event editedEvent = new Event("Test", LocalDateTime.now(),
            LocalDateTime.now().plusHours(1));

    try {
      this.controller.editEvent(null, editedEvent);
      fail("Should throw CommandExecutionException for null original");
    } catch (CommandExecutionException e) {
      assertTrue(e.getMessage().contains("Original and edited events can't be null"));
    }
  }

  @Test
  public void testEditEventWithNullEdited() {
    Event originalEvent = new Event("Test", LocalDateTime.now(),
            LocalDateTime.now().plusHours(1));

    try {
      this.controller.editEvent(originalEvent, null);
      fail("Should throw CommandExecutionException for null edited");
    } catch (CommandExecutionException e) {
      assertTrue(e.getMessage().contains("Original and edited events can't be null"));
    }
  }

  @Test
  public void testEditEventWithEmptySubject() throws CommandExecutionException {
    Event originalEvent = new Event("Original", LocalDateTime.now(),
            LocalDateTime.now().plusHours(1));
    this.manager.getActiveCalendar().addEvent(originalEvent);

    Event editedEvent = new Event("", LocalDateTime.now(),
            LocalDateTime.now().plusHours(1));

    try {
      this.controller.editEvent(originalEvent, editedEvent);
      fail("Should throw CommandExecutionException for empty subject");
    } catch (CommandExecutionException e) {
      assertTrue(e.getMessage().contains("Event subject can't be empty"));
    }
  }

  @Test
  public void testEditNonExistentEvent() {
    Event originalEvent = new Event("Non-existent",
            LocalDateTime.now(), LocalDateTime.now().plusHours(1));
    Event editedEvent = new Event("Edited",
            LocalDateTime.now(), LocalDateTime.now().plusHours(1));

    try {
      this.controller.editEvent(originalEvent, editedEvent);
      fail("Should throw CommandExecutionException for non-existent event");
    } catch (CommandExecutionException e) {
      assertTrue(e.getMessage().contains("Original event not found in calendar"));
    }
  }

  @Test
  public void testSetScheduleStartDate() {
    LocalDate testDate = LocalDate.of(2005, 6, 15);
    this.controller.setScheduleStartDate(testDate);
    assertNull(this.mockView.getLastError());
  }

  @Test
  public void testSetScheduleStartDateWithNull() {
    this.controller.setScheduleStartDate(null);
    assertNotNull("Should render error for null date", this.mockView.getLastError());
  }

  @Test
  public void testCreateCalendar() throws CommandExecutionException {
    this.controller.createCalendar("New Calendar");
    assertNotNull(this.manager.getCalendar("New Calendar"));
    assertEquals("New Calendar",
            this.manager.getCalendar("New Calendar").getName());
  }

  @Test
  public void testCreateCalendarWithNullName() {
    try {
      this.controller.createCalendar(null);
      fail("Should throw CommandExecutionException for null name");
    } catch (CommandExecutionException e) {
      assertTrue(e.getMessage().contains("Calendar name can't be empty"));
    }
  }

  @Test
  public void testCreateCalendarWithEmptyName() {
    try {
      this.controller.createCalendar("  ");
      fail("Should throw CommandExecutionException for empty name");
    } catch (CommandExecutionException e) {
      assertTrue(e.getMessage().contains("Calendar name can't be empty"));
    }
  }
}
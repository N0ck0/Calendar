
package calendar.view;

import java.util.List;

import calendar.controller.GuiController;
import calendar.controller.commands.CommandExecutionException;
import calendar.model.Event;

/**
 * An interface representing the view for the CalendarGUI.
 */
public interface CalendarGuiView extends CalendarView {

  /**
   * Sets up the features and event listeners for the GUI.
   */
  void setFeatures();

  /**
   * Refresh displayed events (e.g., after adding new one).
   */
  void refreshEvents();

  /**
   * Display the schedule of events in the GUI.
   *
   * @param events the list of events to display
   */
  void displaySchedule(List<Event> events);

  /**
   * Sets the controller for this view.
   *
   * @param controller the GUI controller
   */
  void setController(GuiController controller) throws CommandExecutionException;
}

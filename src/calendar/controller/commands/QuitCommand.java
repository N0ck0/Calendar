package calendar.controller.commands;

import calendar.view.CalendarView;

/**
 * Command class that ends the calendar application.
 */
public class QuitCommand implements Command {

  /**
   * Constructs an {@code calendar.controller.commands.quitCommand} object and displays
   * an exit message.
   *
   * @param view the CalendarView to use for displaying the quit message
   */
  public QuitCommand(CalendarView view) {
    view.renderMessage("Received exit command: Quitting program...");
  }

  /**
   * Executes the quit command by terminating the application with exit code 0.
   */
  public void execute() {
    return;
  }
}

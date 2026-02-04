package calendar.controller.commands;

import calendar.model.CalendarManager;

/**
 * Represents a Command that switches the calendar a user is operating with.
 */
public class UseCalendarCommand implements Command {

  private CalendarManager manager;
  private String name;

  /**
   * Constructs a UseCalendarCommand.
   *
   * @param manager CalendarManager.
   * @param name    The name of the calendar to switch to.
   */
  public UseCalendarCommand(CalendarManager manager, String name) {
    this.manager = manager;
    this.name = name;
  }

  /**
   * Executes this command.
   *
   * @throws CommandExecutionException if command execution fails.
   */
  public void execute() throws CommandExecutionException {
    try {
      manager.switchToCalendar(name);
    } catch (IllegalArgumentException e) {
      throw new CommandExecutionException("Calendar does not exist");
    }
  }
}

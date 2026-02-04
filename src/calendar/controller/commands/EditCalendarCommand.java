package calendar.controller.commands;

import calendar.model.CalendarManager;

/**
 * Represents a command that edits a property of a Calendar.
 */
public class EditCalendarCommand implements Command {

  private CalendarManager manager;
  private String calName;
  private String property;
  private String newValue;

  /**
   * Constructs a EditCalendarCommand.
   * @param manager CalendarManager.
   * @param calName Calendar name.
   * @param property property of calendar.
   * @param newValue new value of property.
   */
  public EditCalendarCommand(CalendarManager manager, String calName,
                             String property, String newValue) {
    this.manager = manager;
    this.calName = calName;
    this.property = property;
    this.newValue = newValue;
  }

  /**
   * Executes this command.
   * @throws CommandExecutionException if command execution fails.
   */
  public void execute() throws CommandExecutionException {
    try {
      if (this.property.equals("name")) {
        this.manager.updateName(calName, newValue);
      } else {
        this.manager.updateTimezone(calName, newValue);
      }
    } catch (Exception e) {
      throw new CommandExecutionException("Failed to execute command");
    }
  }
}

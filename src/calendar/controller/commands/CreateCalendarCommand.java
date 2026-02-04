
package calendar.controller.commands;

import java.time.ZoneId;

import calendar.model.CalendarManager;

/**
 * Represents a command that creates a new calendar with a given name and timezone.
 */
public class CreateCalendarCommand implements Command {

  private CalendarManager manager;
  private String name;
  private ZoneId zoneId;

  /**
   * Constructs a CreateCalendarCommand.
   *
   * @param manager CalendarManager.
   * @param name    Name of new calendar.
   * @param zoneId  Timezone of new calendar.
   */
  public CreateCalendarCommand(CalendarManager manager, String name, ZoneId zoneId) {
    this.manager = manager;
    this.name = name;
    this.zoneId = zoneId;
  }


  /**
   * Executes this command creating a new calendar.
   *
   * @throws CommandExecutionException If command execution fails.
   */
  public void execute() throws CommandExecutionException {
    try {
      this.manager.addCalendar(this.name, this.zoneId);
    } catch (CommandExecutionException e) {
      throw new CommandExecutionException(e.getMessage());
    }
  }
}
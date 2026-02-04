package calendar.controller.commands;

/**
 * Represents a command that can be executed on the calendar system.
 */
public interface Command {
  /**
   * Executes the command.
   */
  void execute() throws CommandExecutionException;
}
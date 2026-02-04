package calendar.controller.commands;


/**
 * Exception thrown when a calendar command fails to execute.
 */
public class CommandExecutionException extends Exception {

  /**
   * Constructs a new {@code calendar.controller.commands.commandExecutionException} with no detail
   * message.
   */
  public CommandExecutionException() {
    super();
  }

  /**
   * Constructs a new {@code calendar.controller.commands.commandExecutionException} object
   * with the specified detail message.
   *
   * @param s the detail message explaining why the command execution failed
   */
  public CommandExecutionException(String s) {
    super(s);
  }
}

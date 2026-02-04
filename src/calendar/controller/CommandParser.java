package calendar.controller;

import calendar.controller.commands.Command;
import calendar.controller.commands.CommandExecutionException;

/**
 * Represents a parser that can convert text commands into Command objects.
 */
public interface CommandParser {

  /**
   * Parses a text command into a Command object.
   *
   * @param cmd the text command to parse
   * @return the parsed Command object
   * @throws CommandExecutionException if the command is invalid or malformed
   */
  Command parse(String cmd) throws CommandExecutionException;
}

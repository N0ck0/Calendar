
package calendar.controller;


import java.util.Scanner;

import calendar.controller.commands.Command;
import calendar.controller.commands.CommandExecutionException;
import calendar.controller.commands.QuitCommand;
import calendar.view.CalendarView;

/**
 * Headless implementation of CalendarController for file-based commands.
 */
public class HeadlessController implements CalendarController {
  private final CalendarView view;
  private final Readable in;
  private final CommandParser parser;

  /**
   * Constructs a Headless command-line calendar controller object.
   *
   * @param view   the calendar view
   * @param in     the input source
   * @param parser the command parser
   */
  public HeadlessController(CalendarView view, Readable in,
                            CommandParser parser) {
    this.view = view;
    this.in = in;
    this.parser = parser;
  }

  /**
   * Reads commands from file input and executes them sequentially until completion.
   */
  public void run() {
    Scanner scanner = new Scanner(this.in);
    boolean hasExit = false;

    while (scanner.hasNextLine()) {
      String input = scanner.nextLine();

      if (input.trim().isEmpty()) {
        continue;
      }

      try {
        Command command = this.parser.parse(input);
        if (command instanceof QuitCommand) {
          hasExit = true;
          break;
        }
        command.execute();
      } catch (CommandExecutionException | IllegalArgumentException e) {
        this.view.renderError(e.getMessage());
      }
    }

    if (!hasExit) {
      this.view.renderError("File must end with 'exit' command");
    }
  }
}
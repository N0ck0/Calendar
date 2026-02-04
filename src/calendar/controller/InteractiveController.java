package calendar.controller;

import java.util.Scanner;

import calendar.controller.commands.Command;
import calendar.controller.commands.CommandExecutionException;
import calendar.view.CalendarView;

/**
 * Command-line implementation of CalendarController.
 */
public class InteractiveController implements CalendarController {

  private final CalendarView view;
  private final Readable in;
  private final CommandParser parser;

  /**
   * Constructs an Interactive command-line calendar controller object.
   *
   * @param view the calendar view
   * @param in the input source
   * @param parser the command parser
   */
  public InteractiveController(CalendarView view, Readable in,
                               CommandParser parser) {
    this.view = view;
    this.in = in;
    this.parser = parser;
  }

  /**
   * Reads user input and executes commands until exit.
   */
  public void run() {

    Scanner scanner = new Scanner(in);
    while (scanner.hasNextLine()) {
      String input = scanner.nextLine();
      if (input.trim().equalsIgnoreCase("exit")) {
        break;
      }
      try {
        Command command = parser.parse(input);
        command.execute();

      } catch (IllegalArgumentException | CommandExecutionException e) {
        view.renderError(e.getMessage());
      }
    }
  }
}

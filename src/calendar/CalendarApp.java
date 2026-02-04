package calendar;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;


import calendar.controller.GuiController;
import calendar.controller.HeadlessController;
import calendar.controller.InteractiveController;
import calendar.model.CalendarManager;
import calendar.model.CalendarModel;
import calendar.controller.CommandParser;
import calendar.controller.CommandParserImpl;
import calendar.model.GuiCalendarManager;
import calendar.view.CalendarGuiView;
import calendar.view.CalendarSwingView;
import calendar.view.CalendarTextView;
import calendar.view.CalendarView;

/**
 * Represents the main application class for the calendar.
 */
public class CalendarApp {

  /**
   * Main entry point for the calendar.
   *
   * @param args command line arguments specifying mode and optional filename
   * @throws IOException if file reading fails in headless mode
   */
  public static void main(String[] args) throws IOException {
    try {

      CalendarManager manager = new CalendarManager();
      CalendarModel model;
      CalendarView view;
      Readable in;

      if (args.length == 0) {
        CalendarGuiView guiView;
        guiView = new CalendarSwingView();
        GuiController controller = new GuiController(new GuiCalendarManager(), guiView);
        guiView.setController(controller);
        controller.run();
      } else if (args.length < 2 || !args[0].equalsIgnoreCase("--mode")) {
        System.out.println("Usage: --mode interactive/headless");
        return;
      } else if (args[1].equalsIgnoreCase("interactive")) {
        in = new InputStreamReader(System.in);
        view = new CalendarTextView(System.out);
        CommandParser parser = new CommandParserImpl(manager, view);
        new InteractiveController(view, in, parser).run();
      } else if (args[1].equalsIgnoreCase("headless") && args.length == 3) {
        in = new FileReader(args[2]);
        view = new CalendarTextView(System.out);
        CommandParser parser = new CommandParserImpl(manager, view);
        new HeadlessController(view, in, parser).run();
      } else {
        System.out.println("Invalid mode or missing file for headless mode.");
        return;
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

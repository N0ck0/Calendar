package calendar.view;

import calendar.controller.GuiController;
import calendar.controller.commands.CommandExecutionException;
import calendar.model.Event;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Swing-based GUI implementation of CalendarGuiView that provides a graphical interface
 * for calendar operations.
 */
public class CalendarSwingView extends JFrame implements CalendarGuiView {


  private GuiController controller;
  private final JComboBox<String> calendarDropdown;
  private final JButton newCalendarButton;
  private final JButton addButton;
  private final JButton setDateButton;
  private final JTextField dateField;
  private final JPanel eventListPanel;
  //private final JScrollPane eventScrollPane;

  //for extra credit
  private final JButton editButton;


  /**
   * Constructs this CalendarSwingView and initializes components.
   */
  public CalendarSwingView() {
    super("Calendar GUI");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(600, 500);

    calendarDropdown = new JComboBox<>();
    calendarDropdown.addItem("default");
    newCalendarButton = new JButton("New Calendar");
    dateField = new JTextField(10);
    eventListPanel = new JPanel();
    eventListPanel.setLayout(new BoxLayout(eventListPanel, BoxLayout.Y_AXIS));

    JScrollPane eventScrollPane = new JScrollPane(eventListPanel);
    eventScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    this.add(eventScrollPane, BorderLayout.CENTER);
    addButton = new JButton("Add Event");
    setDateButton = new JButton("Set View From Date");

    //for extra credit
    editButton = new JButton("Edit Event");

    JPanel topPanel = new JPanel(new GridLayout(4, 2));
    topPanel.add(new JLabel("Calendar:"));
    topPanel.add(calendarDropdown);
    topPanel.add(newCalendarButton);
    topPanel.add(setDateButton);
    topPanel.add(addButton);

    topPanel.add(editButton);

    topPanel.add(new JLabel("Event Schedule:"), BorderLayout.WEST);
    this.add(topPanel, BorderLayout.NORTH);
  }

  /**
   * Sets the controller for this view and initializes the default calendar.
   *
   * @param controller the GUI controller to handle user interactions
   * @throws CommandExecutionException if the default calendar can't be created
   */
  public void setController(GuiController controller) throws CommandExecutionException {
    this.controller = controller;
    controller.createCalendar("default");
    controller.selectCalendar("default");
  }

  /**
   * Sets up event listeners and action handlers for all GUI components.
   */
  @Override
  public void setFeatures() {

    calendarDropdown.addActionListener(e -> {
      String selected = (String) calendarDropdown.getSelectedItem();
      if (selected != null) {
        try {
          controller.selectCalendar(selected);
          System.out.println("Selected calendar: " + selected);
          refreshEvents();
        } catch (Exception ex) {
          renderError("Could not switch to calendar: " + ex.getMessage());
        }
      }
    });

    newCalendarButton.addActionListener(e -> {
      String name = JOptionPane.showInputDialog(this,
              "Enter new calendar name:");
      if (name != null && !name.trim().isEmpty()) {
        try {
          controller.createCalendar(name.trim());
          calendarDropdown.addItem(name.trim());
          calendarDropdown.setSelectedItem(name.trim());
          refreshEvents();
        } catch (Exception ex) {
          renderError("Could not create calendar: " + ex.getMessage());
        }
      }
    });

    addButton.addActionListener(e -> openAddEventDialog());

    setDateButton.addActionListener(e -> openSetViewDateDialog());

    //for extra credit
    editButton.addActionListener(e -> openEditEventDialog());
  }

  private void openSetViewDateDialog() {
    JDialog dialog = new JDialog(this, "Set View Date", true);
    dialog.setSize(400, 200);
    dialog.setLocationRelativeTo(this);
    JPanel panel = new JPanel(new GridLayout(2, 1));
    panel.add(new JLabel("View From Date (YYYY-MM-DD):"));
    panel.add(dateField);

    JButton dateButton = new JButton("Confirm Date");
    panel.add(dateButton);

    dateButton.addActionListener(ae -> {
      try {
        controller.setScheduleStartDate(LocalDate.parse(dateField.getText()));
        dialog.dispose();
        refreshEvents();
      } catch (Exception ex) {
        renderError("Invalid date format: " + ex.getMessage());
      }
    });
    dialog.add(panel);
    dialog.setVisible(true);


  }

  private void openAddEventDialog() {
    JDialog dialog = new JDialog(this, "Create Event", true);
    dialog.setSize(400, 400);
    dialog.setLocationRelativeTo(this);

    JPanel panel = new JPanel(new GridLayout(10, 1));
    JTextField subjectField = new JTextField(15);
    JComboBox<Integer> startYear = new JComboBox<>();
    JComboBox<Integer> endYear = new JComboBox<>();
    JComboBox<Integer> startMonth = new JComboBox<>();
    JComboBox<Integer> endMonth = new JComboBox<>();
    JComboBox<Integer> startDate = new JComboBox<>();
    JComboBox<Integer> endDate = new JComboBox<>();
    JTextField startTime = new JTextField(5);
    JTextField endTime = new JTextField(5);

    int currentYear = LocalDate.now().getYear();
    startYear.addItem(currentYear);
    endYear.addItem(currentYear);
    for (int i = currentYear - 20; i <= currentYear + 20; i++) {
      startYear.addItem(i);
      endYear.addItem(i);
    }
    for (int i = 1; i <= 12; i++) {
      startMonth.addItem(i);
      endMonth.addItem(i);
    }

    for (int i = 1; i <= 31; i++) {
      startDate.addItem(i);
      endDate.addItem(i);
    }

    panel.add(new JLabel("Subject:"));
    panel.add(subjectField);
    panel.add(new JLabel("Start Year:"));
    panel.add(startYear);
    panel.add(new JLabel("Start Month:"));
    panel.add(startMonth);
    panel.add(new JLabel("Start Day (DD):"));
    panel.add(startDate);
    panel.add(new JLabel("Start Time (HH:MM):"));
    panel.add(startTime);
    panel.add(new JLabel("End Year:"));
    panel.add(endYear);
    panel.add(new JLabel("End Month:"));
    panel.add(endMonth);
    panel.add(new JLabel("End Day (DD):"));
    panel.add(endDate);
    panel.add(new JLabel("End Time (HH:MM):"));
    panel.add(endTime);

    JButton createButton = new JButton("Create Event");
    panel.add(createButton);

    createButton.addActionListener(ae -> {
      try {
        LocalDateTime startDateTime = LocalDateTime.of(
                LocalDate.of((int) startYear.getSelectedItem(),
                        (int) startMonth.getSelectedItem(),
                        (Integer) startDate.getSelectedItem()),
                LocalTime.parse(startTime.getText()));

        LocalDateTime endDateTime = LocalDateTime.of(
                LocalDate.of((int) endYear.getSelectedItem(),
                        (int) endMonth.getSelectedItem(),
                        (int) endDate.getSelectedItem()),
                LocalTime.parse(endTime.getText()));

        controller.addEvent(subjectField.getText(), startDateTime, endDateTime);
        dialog.dispose();
        refreshEvents();
      } catch (Exception ex) {
        renderError("Invalid input: " + ex.getMessage());
      }
    });
    dialog.add(panel);
    dialog.setVisible(true);
  }

  private void openEditEventDialog() {
    // Get current events
    List<Event> events = controller.getEventSchedule();
    if (events.isEmpty()) {
      renderError("No events to edit.");
      return;
    }

    String[] eventDescriptions = new String[events.size()];
    for (int i = 0; i < events.size(); i++) {
      Event e = events.get(i);
      eventDescriptions[i] = String.format("%d: %s at %s",
              i + 1, e.getSubject(),
              e.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    String selected = (String) JOptionPane.showInputDialog(
            this,
            "Select event to edit:",
            "Edit Event",
            JOptionPane.PLAIN_MESSAGE,
            null,
            eventDescriptions,
            eventDescriptions[0]
    );

    if (selected == null) {
      return;
    }

    int eventIndex = Integer.parseInt(selected.split(":")[0]) - 1;
    Event selectedEvent = events.get(eventIndex);

    JDialog dialog = new JDialog(this, "Edit Event", true);
    dialog.setSize(400, 400);
    dialog.setLocationRelativeTo(this);

    JPanel panel = new JPanel(new GridLayout(10, 1));
    JTextField subjectField = new JTextField(selectedEvent.getSubject());

    LocalDateTime startDT = selectedEvent.getStartDateTime();
    LocalDateTime endDT = selectedEvent.getEndDateTime();

    JComboBox<Integer> startYear = new JComboBox<>();
    JComboBox<Integer> endYear = new JComboBox<>();
    JComboBox<Integer> startMonth = new JComboBox<>();
    JComboBox<Integer> endMonth = new JComboBox<>();
    JComboBox<Integer> startDate = new JComboBox<>();
    JComboBox<Integer> endDate = new JComboBox<>();
    JTextField startTime = new JTextField(startDT.toLocalTime().toString());
    JTextField endTime = new JTextField(endDT.toLocalTime().toString());

    int currentYear = LocalDate.now().getYear();
    for (int i = currentYear - 20; i <= currentYear + 20; i++) {
      startYear.addItem(i);
      endYear.addItem(i);
    }
    startYear.setSelectedItem(startDT.getYear());
    endYear.setSelectedItem(endDT.getYear());

    for (int i = 1; i <= 12; i++) {
      startMonth.addItem(i);
      endMonth.addItem(i);
    }
    startMonth.setSelectedItem(startDT.getMonthValue());
    endMonth.setSelectedItem(endDT.getMonthValue());

    for (int i = 1; i <= 31; i++) {
      startDate.addItem(i);
      endDate.addItem(i);
    }
    startDate.setSelectedItem(startDT.getDayOfMonth());
    endDate.setSelectedItem(endDT.getDayOfMonth());

    panel.add(new JLabel("Subject:"));
    panel.add(subjectField);
    panel.add(new JLabel("Start Year:"));
    panel.add(startYear);
    panel.add(new JLabel("Start Month:"));
    panel.add(startMonth);
    panel.add(new JLabel("Start Day (DD):"));
    panel.add(startDate);
    panel.add(new JLabel("Start Time (HH:MM):"));
    panel.add(startTime);
    panel.add(new JLabel("End Year:"));
    panel.add(endYear);
    panel.add(new JLabel("End Month:"));
    panel.add(endMonth);
    panel.add(new JLabel("End Day (DD):"));
    panel.add(endDate);
    panel.add(new JLabel("End Time (HH:MM):"));
    panel.add(endTime);

    JButton updateButton = new JButton("Update Event");
    panel.add(updateButton);

    updateButton.addActionListener(ae -> {
      try {
        LocalDateTime newStartDateTime = LocalDateTime.of(
                LocalDate.of((int) startYear.getSelectedItem(),
                        (int) startMonth.getSelectedItem(),
                        (int) startDate.getSelectedItem()),
                LocalTime.parse(startTime.getText()));

        LocalDateTime newEndDateTime = LocalDateTime.of(
                LocalDate.of((int) endYear.getSelectedItem(),
                        (int) endMonth.getSelectedItem(),
                        (int) endDate.getSelectedItem()),
                LocalTime.parse(endTime.getText()));

        Event editedEvent = new Event(subjectField.getText(), newStartDateTime, newEndDateTime);
        controller.editEvent(selectedEvent, editedEvent);
        dialog.dispose();
        refreshEvents();
      } catch (Exception ex) {
        renderError("Invalid input: " + ex.getMessage());
      }
    });
    dialog.add(panel);
    dialog.setVisible(true);
  }

  /**
   * Displays an error message to the user in a dialog box.
   *
   * @param message the error message to display
   */
  @Override
  public void renderError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Displays an informational message to the user in a dialog box.
   *
   * @param message the message to display
   */
  @Override
  public void renderMessage(String message) {
    JOptionPane.showMessageDialog(this, message);
  }

  /**
   * Refreshes the event display by getting the latest schedule from the controller.
   */
  @Override
  public void refreshEvents() {
    this.displaySchedule(controller.getEventSchedule());
  }

  /**
   * Renders a list of events by refreshing the entire display.
   *
   * @param events the list of events to render
   */
  @Override
  public void renderEvents(List<Event> events) {
    this.refreshEvents();
  }

  /**
   * Renders the busy status at a specific date and time.
   *
   * @param b whether the calendar is busy at the specified time
   * @param localDateTime the date and time to check
   */
  @Override
  public void renderBusyStatus(boolean b, LocalDateTime localDateTime) {
    return;
  }

  /**
   * Displays the schedule of events in the main text area.
   *
   * @param events the list of events to display in the schedule
   */
  public void displaySchedule(List<Event> events) {
    eventListPanel.removeAll();
    this.setVisible(true);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    for (Event e : events) {
      JPanel eventPanel = new JPanel() {
        @Override
        public Dimension getMaximumSize() {
          return new Dimension(500, 80);
        }
      };
      eventPanel.setLayout(new BorderLayout());
      eventPanel.setBorder(BorderFactory.createCompoundBorder(
              BorderFactory.createLineBorder(Color.GRAY),
              BorderFactory.createEmptyBorder(10, 10, 10, 10)));

      String info = String.format("%s from %s to %s\n",
              e.getSubject(),
              e.getStartDateTime().format(formatter),
              e.getEndDateTime().format(formatter));

      JLabel eventLabel = new JLabel(info);
      eventPanel.add(eventLabel, BorderLayout.CENTER);

      eventListPanel.add(eventPanel);
    }

    eventListPanel.revalidate();
    eventListPanel.repaint();
  }
}
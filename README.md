Assignment 6 Readme:

Instructions to run program:
Open Terminal and navigate to /res folder.
The program can be run in three different modes:
1. GUI Mode (default): java -jar CalendarAppGUI.jar
2. Interactive Mode: java -jar CalendarAppGUI.jar --mode interactive
3. Headless Mode: java -jar CalendarAppGUI.jar --mode headless <filename.txt>

Design Changes for Assignment 6:
1. Introduced CalendarSwingView class: Created a new GUI view using Java Swing that implements CalendarGuiView interface. This provides a graphical interface for users to interact with the calendar application instead of just command-line interface. The view includes buttons for creating events, managing calendars, and setting schedule dates.

2. Added GuiController class: Created a new controller specifically for handling GUI 
   interactions. This controller implements both CalendarController and Features interfaces so it 
   can manage GUI-specific operations like event creation through dialog boxes and calendar 
   switching through dropdown menus.

3. Implemented GuiCalendarModel and GuiCalendarModelImpl: Extended the existing calendar model to support GUI-specific features like schedule view that shows up to 10 events from a specified start date.

4. Added Features interface: Created an interface that defines the main features available for 
   calendar management in the GUI, including addEvent and setScheduleStartDate methods. This 
   choice was made to separate GUI-specific operations from general calendar operations.

5.  CalendarModelImpl with timezone conversion: Updated the updateZoneId method to properly 
    convert existing events to new timezones while maintaining the correct overall time. This 
    ensures that when a calendar's timezone changes, all events are appropriately adjusted.

6. Added event editing (Extra Credit): Implemented editEvent method in GuiController and removeEvent method in CalendarModelImpl to support editing existing events through the GUI. Users can select an event and modify its details.

7. Added multiple calendar support (Extra Credit): Added dropdown menu in GUI for switching between 
   calendars and button for creating new calendars. This allows users to manage multiple calendars within the same application session.

Working Features:
All features from previous assignments work correctly and feedback was taken into consideration 
when making improvements on previous design for access and MVC comments. New GUI features include 
event 
creation, event editing (extra 
credit), 
multiple 
calendar management (extra credit), schedule view with configurable start date.

Contributions For Assignment 6:

Nicholas: 90% of GUI implementation, enhanced CalendarSwingView with better event display, 50% 
testing.

Chayil: 10% of GUI implementation, 100% extra credit features (event editing and enhanced multiple 
calendar support), 50% testing.
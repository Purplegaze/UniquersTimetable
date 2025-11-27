package view;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class TimetableView extends JPanel {

    public static class TimetableSlotItem {
        private final String courseCode;
        private final String sectionCode;
        private final String location;
        private final Color color;
        private final boolean hasConflict;

        public TimetableSlotItem(String courseCode, String sectionCode,
                                 String location, Color color, boolean hasConflict) {
            this.courseCode = courseCode;
            this.sectionCode = sectionCode;
            this.location = location;
            this.color = color;
            this.hasConflict = hasConflict;
        }

        public String getCourseCode() { return courseCode; }
        public String getSectionCode() { return sectionCode; }
        public String getLocation() { return location; }
        public Color getColor() { return color; }
        public boolean hasConflict() { return hasConflict; }
    }

    private static final int START_TIME = 9;
    private static final int END_TIME = 21;
    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    private Map<String, JPanel> slotPanels;

    private Map<String, List<TimetableSlotItem>> slotCourses;

    public TimetableView() {
        slotPanels = new HashMap<>();
        slotCourses = new HashMap<>();
        setLayout(new BorderLayout());

        initializeComponents();
    }

    public void displayCourse(String day, int startHour, int endHour, TimetableSlotItem item) {
        java.util.List<String> slotKeys = generateSlotKeys(day, startHour, endHour);

        for (String key : slotKeys) {
            JPanel slot = slotPanels.get(key);
            if (slot != null) {
                // Get or create list of courses for this slot
                List<TimetableSlotItem> courses = slotCourses.computeIfAbsent(key, k -> new ArrayList<>());

                // Add the new course
                courses.add(item);

                // Update the slot display
                updateSlotWithMultipleCourses(slot, courses);
            }
        }
    }

    public void clearAll() {
        slotCourses.clear();

        for (JPanel slot : slotPanels.values()) {
            slot.removeAll();
            slot.setBackground(Color.WHITE);
            slot.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            slot.revalidate();
            slot.repaint();
        }
    }


    public void showConflictWarning(String conflictMessage) {
        JOptionPane.showMessageDialog(
                this,
                conflictMessage,
                "Schedule Conflict",
                JOptionPane.WARNING_MESSAGE
        );
    }

    public void showErrorMessage(String errorMessage) {
        JOptionPane.showMessageDialog(
                this,
                errorMessage,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }


    private java.util.List<String> generateSlotKeys(String day, int startHour, int endHour) {
        java.util.List<String> keys = new ArrayList<>();
        for (int hour = startHour; hour < endHour; hour++) {
            keys.add(day + "-" + hour);
        }
        return keys;
    }

    /**
     * Update a slot that may contain multiple courses (conflict).
     * Splits the slot horizontally if there are 2+ courses.
     */
    private void updateSlotWithMultipleCourses(JPanel slot, List<TimetableSlotItem> courses) {
        slot.removeAll();

        if (courses.size() == 1) {
            // Single course - use entire slot
            TimetableSlotItem item = courses.get(0);
            slot.setBackground(item.getColor());
            slot.setLayout(new BorderLayout());

            JLabel label = createCourseLabel(item);
            slot.add(label, BorderLayout.CENTER);

            if (item.hasConflict()) {
                slot.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            } else {
                slot.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            }
        } else {
            // Multiple courses - split the slot
            slot.setLayout(new GridLayout(1, courses.size()));
            slot.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

            for (TimetableSlotItem item : courses) {
                JPanel coursePanel = new JPanel(new BorderLayout());
                coursePanel.setBackground(item.getColor());
                coursePanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));

                JLabel label = createCourseLabel(item);
                coursePanel.add(label, BorderLayout.CENTER);

                slot.add(coursePanel);
            }
        }

        slot.revalidate();
        slot.repaint();
    }

    /**
     * Update a single slot with course information.
     */
    private void updateSlot(JPanel slot, TimetableSlotItem item) {
        slot.removeAll();
        slot.setBackground(item.getColor());
        slot.setLayout(new BorderLayout());

        JLabel label = createCourseLabel(item);
        slot.add(label, BorderLayout.CENTER);

        if (item.hasConflict()) {
            slot.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        } else {
            slot.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        }

        slot.revalidate();
        slot.repaint();
    }


    private JLabel createCourseLabel(TimetableSlotItem item) {
        String labelText = "<html><center>" + item.getCourseCode() + "<br>"
                + item.getSectionCode() + "<br>" + item.getLocation() + "</center></html>";
        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 10));
        return label;
    }

    private void initializeComponents() {
        int numRows = END_TIME - START_TIME;

        // Create a container for headers (time header + day headers)
        JPanel headerContainer = new JPanel(new BorderLayout());

        // Time header (top-left corner)
        JLabel timeHeader = new JLabel("Time", SwingConstants.CENTER);
        timeHeader.setFont(new Font("Arial", Font.BOLD, 12));
        timeHeader.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        timeHeader.setPreferredSize(new Dimension(60, 25));
        headerContainer.add(timeHeader, BorderLayout.WEST);

        // Day headers
        JPanel dayHeaderPanel = new JPanel(new GridLayout(1, DAYS.length));
        for (String day : DAYS) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 12));
            label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            dayHeaderPanel.add(label);
        }
        headerContainer.add(dayHeaderPanel, BorderLayout.CENTER);

        // Time column
        JPanel timePanel = new JPanel(new GridLayout(numRows, 1));
        timePanel.setPreferredSize(new Dimension(60, 0));
        for (int hour = START_TIME; hour < END_TIME; hour++) {
            JLabel timeLabel = new JLabel(hour + ":00", SwingConstants.CENTER);
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            timeLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            timePanel.add(timeLabel);
        }

        // Timetable grid
        JPanel gridPanel = new JPanel(new GridLayout(numRows, DAYS.length));
        for (int hour = START_TIME; hour < END_TIME; hour++) {
            for (String day : DAYS) {
                JPanel slot = new JPanel();
                slot.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                slot.setBackground(Color.WHITE);

                // Store reference with key like "Monday-9"
                String key = day + "-" + hour;
                slotPanels.put(key, slot);
                slotCourses.put(key, new ArrayList<>());

                gridPanel.add(slot);
            }
        }

        add(headerContainer, BorderLayout.NORTH);
        add(timePanel, BorderLayout.WEST);
        add(gridPanel, BorderLayout.CENTER);
    }
}
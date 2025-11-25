package view;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
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

    public TimetableView(){
        slotPanels = new HashMap<>();
        setLayout(new BorderLayout());

        initializeComponents();
    }

    public void displayCourse(String day, int startHour, int endHour, TimetableSlotItem item){
        List<String> slotKeys = generateSlotKeys(day, startHour, endHour);

        for (String key : slotKeys) {
            JPanel slot = slotPanels.get(key);
            if (slot != null) {
                updateSlot(slot, item);
            }
        }
    }

    private List<String> generateSlotKeys(String day, int startHour, int endHour) {
        List<String> keys = new ArrayList<>();
        for (int hour = startHour; hour < endHour; hour++) {
            keys.add(day + "-" + hour);
        }
        return keys;
    }

    private void updateSlot(JPanel slot, TimetableSlotItem item) {
        slot.removeAll();
        slot.putClientProperty("courseCode", item.getCourseCode());
        slot.setBackground(item.getColor());
        slot.setLayout(new BorderLayout());

        JLabel label = createLabel(item);
        slot.add(label, BorderLayout.CENTER);

        if (item.hasConflict()) {
            slot.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        }

        slot.revalidate();
        slot.repaint();
    }

    private JLabel createLabel(TimetableSlotItem item) {
        String labelText = "<html><center>" + item.getCourseCode() + "<br>"
                + item.getSectionCode() + "<br>" + item.getLocation() + "</center></html>";
        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 10));
        return label;
    }

    private void initializeComponents() {
        String[] weeklyTimetable = DAYS; // {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"}

        JPanel headerPanel = new JPanel(new GridLayout(1, weeklyTimetable.length + 1)); // +1 for empty corner if needed, or just length

        JPanel headerContainer = new JPanel(new BorderLayout());
        JPanel emptyCorner = new JPanel(); // Space above time column
        emptyCorner.setPreferredSize(new Dimension(50, 0)); // Approx width of time column
        JPanel daysHeader = new JPanel(new GridLayout(1, weeklyTimetable.length));

        for (String day : weeklyTimetable) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            daysHeader.add(dayLabel);
        }

        headerContainer.add(emptyCorner, BorderLayout.WEST);
        headerContainer.add(daysHeader, BorderLayout.CENTER);

        JPanel timePanel = new JPanel();
        int numRows = END_TIME - START_TIME + 1;
        timePanel.setLayout(new GridLayout(numRows, 1));

        JPanel timeSlots = new JPanel();
        timeSlots.setLayout(new GridLayout(numRows, weeklyTimetable.length)); // 5 Columns (Mon-Fri)

        for (int i = 9; i <= END_TIME; i++) {

            JLabel timeLabel = new JLabel(i + ":00", SwingConstants.RIGHT);
            timeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
            timePanel.add(timeLabel);

            for (String day : weeklyTimetable) {
                JPanel slot = new JPanel();
                slot.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                slot.setBackground(Color.WHITE);

                slot.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        if (slotClickListener != null) {
                            String courseCode = (String) slot.getClientProperty("courseCode");
                            if (courseCode != null) {
                                slotClickListener.onSlotClick(courseCode);
                            }
                        }
                    }
                });
                // -----------------------------------

                timeSlots.add(slot);

                slotPanels.put(day + "-" + i, slot);
            }
        }

        add(headerContainer, BorderLayout.NORTH);
        add(timePanel, BorderLayout.WEST);
        add(timeSlots, BorderLayout.CENTER);
    }

    public interface SlotClickListener {
        void onSlotClick(String courseCode);
    }

    private SlotClickListener slotClickListener;

    public void setSlotClickListener(SlotClickListener listener) {
        this.slotClickListener = listener;
    }

}

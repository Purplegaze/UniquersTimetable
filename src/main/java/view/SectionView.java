package view;
import javax.swing.*;
import java.awt.*;
import entity.*;
import usecase.addcourse.AddCourseInputBoundary;
import usecase.addcourse.AddCourseInputData;
import app.TimeSlotHelper;

public class SectionView extends JDialog {

    public SectionView(Course course, AddCourseInputBoundary addCourseInteractor) {
        setTitle("Section Details - " + course.getCourseCode());
        setSize(600, 500);
        setModal(true);
        setLayout(new BorderLayout(10, 10));

        // Main content
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Section Details"));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title of window
        JLabel header = new JLabel("Section Information");
        header.setFont(new Font(header.getFont().getFontName(), Font.BOLD, 12));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(header);
        panel.add(Box.createVerticalStrut(10));

        // Real sections
        for (Section s : course.getSections()) {
            JPanel sectionPanel = new JPanel();
            sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
            sectionPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            sectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            sectionPanel.add(new JLabel("Section: " + s.getSectionId()));
            sectionPanel.add(Box.createVerticalStrut(5));

            String timeDisplay = formatTimes(s.getTimes());
            sectionPanel.add(new JLabel("Time: " + timeDisplay));
            sectionPanel.add(Box.createVerticalStrut(5));

            String instructorDisplay = formatInstructors(s.getInstructors());
            sectionPanel.add(new JLabel("Instructor: " + instructorDisplay));
            sectionPanel.add(Box.createVerticalStrut(10));

            // Add course button
            JButton addButton = new JButton("Add Course");
            addButton.setAlignmentX(Component.LEFT_ALIGNMENT);

            addButton.addActionListener(e -> {
                addSectionToTimetable(course, s, addCourseInteractor);
                dispose();
            });

            sectionPanel.add(addButton);

            panel.add(sectionPanel);
            panel.add(Box.createVerticalStrut(10));
        }

        add(new JScrollPane(panel), BorderLayout.CENTER);
        setLocationRelativeTo(null);

        setVisible(true);
    }

    /**
     * Add section to timetable by creating an AddCourseInputData for each time slot.
     */
    private void addSectionToTimetable(Course course, Section section,
                                       AddCourseInputBoundary addCourseInteractor) {
        if (section.getTimes() == null || section.getTimes().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Cannot add section: No time information available",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Add each time slot
        for (TimeSlot timeSlot : section.getTimes()) {
            String day = TimeSlotHelper.getDayName(timeSlot.getDayOfWeek());
            int startHour = TimeSlotHelper.getStartHour(timeSlot);
            int endHour = TimeSlotHelper.getEndHour(timeSlot);
            String location = timeSlot.getBuilding() != null
                    ? timeSlot.getBuilding().getBuildingCode()
                    : "TBD";

            AddCourseInputData inputData = new AddCourseInputData(
                    course.getCourseCode(),
                    section.getSectionId(),
                    day,
                    startHour,
                    endHour,
                    location
            );
            addCourseInteractor.execute(inputData);
        }
    }

    /**
     * Format time slots for display.
     */
    private String formatTimes(java.util.List<TimeSlot> timeSlots) {
        if (timeSlots == null || timeSlots.isEmpty()) {
            return "TBD";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < timeSlots.size(); i++) {
            TimeSlot ts = timeSlots.get(i);
            String day = TimeSlotHelper.getDayName(ts.getDayOfWeek());
            int startHour = TimeSlotHelper.getStartHour(ts);
            int endHour = TimeSlotHelper.getEndHour(ts);
            String location = ts.getBuilding() != null ? ts.getBuilding().getBuildingCode() : "TBD";

            sb.append(String.format("%s %02d:00-%02d:00 @ %s", day, startHour, endHour, location));

            if (i < timeSlots.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * Format instructors for display.
     */
    private String formatInstructors(java.util.List<String> instructors) {
        if (instructors == null || instructors.isEmpty()) {
            return "TBA";
        }
        return String.join(", ", instructors);
    }
}
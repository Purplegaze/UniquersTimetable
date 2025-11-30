package view;

import interface_adapter.controller.AddCourseController;
import interface_adapter.controller.AddCourseController.TimeSlotData;
import entity.Course;
import entity.Section;
import entity.TimeSlot;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * View for displaying and selecting course sections.
 */
public class SectionView extends JDialog {

    private final Course course;
    private final AddCourseController controller;

    public SectionView(Course course, AddCourseController controller) {
        if (course == null || controller == null) {
            throw new IllegalArgumentException("Course and Controller cannot be null");
        }

        this.course = course;
        this.controller = controller;

        setTitle("Section Details - " + course.getCourseCode());
        setSize(700, 600);
        setModal(true);
        setLayout(new BorderLayout(10, 10));

        // Main content panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Course code and name header
        JLabel courseHeader = new JLabel(course.getCourseCode() + " - " + course.getCourseName());
        courseHeader.setFont(new Font(courseHeader.getFont().getFontName(), Font.BOLD, 16));
        courseHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(courseHeader);
        panel.add(Box.createVerticalStrut(5));

        JLabel subHeader = new JLabel("Section Information");
        subHeader.setFont(new Font(subHeader.getFont().getFontName(), Font.PLAIN, 12));
        subHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(subHeader);
        panel.add(Box.createVerticalStrut(5));

        // Course term info
        String courseTerm = course.getTerm();
        String termDisplay = getTermDisplay(courseTerm);
        JLabel termLabel = new JLabel("Term: " + termDisplay);
        termLabel.setFont(new Font(termLabel.getFont().getFontName(), Font.ITALIC, 11));
        termLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(termLabel);
        panel.add(Box.createVerticalStrut(15));

        // Separate sections by type
        List<Section> lectures = new ArrayList<>();
        List<Section> tutorials = new ArrayList<>();
        List<Section> practicals = new ArrayList<>();

        for (Section s : course.getSections()) {
            String sectionId = s.getSectionId().toUpperCase();
            if (sectionId.startsWith("LEC")) {
                lectures.add(s);
            } else if (sectionId.startsWith("TUT")) {
                tutorials.add(s);
            } else if (sectionId.startsWith("PRA")) {
                practicals.add(s);
            } else {
                lectures.add(s);
            }
        }

        if (!lectures.isEmpty()) {
            addSectionTypePanel(panel, "Lectures", lectures);
        }

        if (!tutorials.isEmpty()) {
            addSectionTypePanel(panel, "Tutorials", tutorials);
        }

        if (!practicals.isEmpty()) {
            addSectionTypePanel(panel, "Practicals", practicals);
        }

        // Scroll
        add(new JScrollPane(panel), BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }

    /**
     * Add a panel for a specific section type.
     */
    private void addSectionTypePanel(JPanel parentPanel, String typeTitle, List<Section> sections) {
        // Type header
        JLabel typeLabel = new JLabel(typeTitle);
        typeLabel.setFont(new Font(typeLabel.getFont().getFontName(), Font.BOLD, 13));
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        parentPanel.add(typeLabel);
        parentPanel.add(Box.createVerticalStrut(8));

        for (Section section : sections) {
            JPanel sectionPanel = createSectionPanel(section);
            parentPanel.add(sectionPanel);
            parentPanel.add(Box.createVerticalStrut(10));
        }

        parentPanel.add(Box.createVerticalStrut(10));
    }

    /**
     * Create a panel for a single section.
     */
    private JPanel createSectionPanel(Section section) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        sectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionPanel.setMaximumSize(new Dimension(650, Integer.MAX_VALUE));

        // Section ID
        sectionPanel.add(new JLabel("Section: " + section.getSectionId()));
        sectionPanel.add(Box.createVerticalStrut(5));

        // Time display
        String timeDisplay = formatTimes(section.getTimes());
        sectionPanel.add(new JLabel("Time: " + timeDisplay));
        sectionPanel.add(Box.createVerticalStrut(5));

        // Instructor display
        String instructorDisplay = formatInstructors(section.getInstructors());
        sectionPanel.add(new JLabel("Instructor: " + instructorDisplay));
        sectionPanel.add(Box.createVerticalStrut(5));

        // Enrollment info
        String enrollmentInfo = String.format("Enrollment: %d/%d",
                section.getEnrolledStudents(),
                section.getCapacity());
        JLabel enrollmentLabel = new JLabel(enrollmentInfo);
        if (section.isFull()) {
            enrollmentLabel.setForeground(Color.RED);
            enrollmentLabel.setText(enrollmentInfo + " (FULL)");
        }
        sectionPanel.add(enrollmentLabel);
        sectionPanel.add(Box.createVerticalStrut(10));

        // Add course button
        JButton addButton = new JButton("Add to Timetable");
        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addButton.addActionListener(e -> onAddButtonClicked(section));

        sectionPanel.add(addButton);

        return sectionPanel;
    }

    private void onAddButtonClicked(Section section) {
        try {
            // Validate section has time slots
            if (section.getTimes() == null || section.getTimes().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Cannot add section: No time information available",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Collect instructor name
            String instructor = "TBA";
            if (section.getInstructors() != null && !section.getInstructors().isEmpty()) {
                instructor = section.getInstructors().get(0);
            }

            // Convert TimeSlots to TimeSlotData (primitives for controller)
            List<TimeSlotData> timeDatas = new ArrayList<>();
            for (TimeSlot timeSlot : section.getTimes()) {
                String day = getDayName(timeSlot.getDayOfWeek());
                int startHour = timeSlot.getStartTime().getHour();
                int endHour = timeSlot.getEndTime().getHour();
                String location = timeSlot.getBuilding() != null
                        ? timeSlot.getBuilding().getBuildingCode()
                        : "TBD";

                timeDatas.add(new TimeSlotData(day, startHour, endHour, location));
            }

            // Call Controller (View → Controller - Clean Architecture!)
            controller.addCourse(
                    course.getCourseCode(),
                    section.getSectionId(),
                    instructor,
                    timeDatas
            );

            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error adding section: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Format time slots for display.
     */
    private String formatTimes(List<TimeSlot> timeSlots) {
        if (timeSlots == null || timeSlots.isEmpty()) {
            return "TBD";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < timeSlots.size(); i++) {
            TimeSlot ts = timeSlots.get(i);
            String day = getDayName(ts.getDayOfWeek());
            int startHour = ts.getStartTime().getHour();
            int endHour = ts.getEndTime().getHour();
            String location = ts.getBuilding() != null
                    ? ts.getBuilding().getBuildingCode()
                    : "TBD";

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
    private String formatInstructors(List<String> instructors) {
        if (instructors == null || instructors.isEmpty()) {
            return "TBA";
        }
        return String.join(", ", instructors);
    }

    /**
     * Get term display name.
     */
    private String getTermDisplay(String term) {
        if (term == null) {
            return "Unknown";
        }
        switch (term) {
            case "F": return "Fall";
            case "S": return "Winter";
            case "Y": return "Year-long";
            default: return "Unknown";
        }
    }

    /**
     * Convert day of week number to name.
     */
    private String getDayName(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1: return "Monday";
            case 2: return "Tuesday";
            case 3: return "Wednesday";
            case 4: return "Thursday";
            case 5: return "Friday";
            case 6: return "Saturday";
            case 7: return "Sunday";
            default: return "Unknown";
        }
    }

    /**
     * Display the dialog.
     */
    public void display() {
        setVisible(true);
    }
}
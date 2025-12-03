package view;

import interface_adapter.addcourse.AddCourseController;
import interface_adapter.addcourse.AddCourseController.TimeSlotData;
import interface_adapter.viewcourse.ViewCourseViewModel;
import interface_adapter.viewcourse.ViewCourseViewModel.SectionViewModel;
import interface_adapter.viewcourse.ViewCourseViewModel.TimeSlotViewModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * View for displaying and selecting course sections.
 */
public class SectionView extends JDialog {

    private final ViewCourseViewModel viewModel;
    private final AddCourseController controller;

    public SectionView(ViewCourseViewModel viewModel, AddCourseController controller) {
        if (viewModel == null || controller == null) {
            throw new IllegalArgumentException("ViewModel and Controller cannot be null");
        }

        this.viewModel = viewModel;
        this.controller = controller;

        setTitle("Section Details - " + viewModel.getCourseCode());
        setSize(700, 600);
        setModal(true);
        setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Course header
        JLabel courseHeader = new JLabel(viewModel.getCourseCode() + " - " + viewModel.getCourseName());
        courseHeader.setFont(new Font(courseHeader.getFont().getFontName(), Font.BOLD, 16));
        courseHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(courseHeader);
        panel.add(Box.createVerticalStrut(5));

        // Ratings Display
        if (viewModel.hasRating()) {
            Float rec = viewModel.getRecommendation();
            Float work = viewModel.getWorkload();
            float recVal = rec != null ? rec : 0.0f;
            float workVal = work != null ? work : 0.0f;

            JLabel ratingLabel = new JLabel(String.format("Ratings: Recommendation: %.1f/5.0 | Workload: %.1f/5.0",
                    recVal, workVal));
            ratingLabel.setFont(new Font(ratingLabel.getFont().getFontName(), Font.BOLD, 12));
            ratingLabel.setForeground(new Color(0, 100, 0));
            ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(ratingLabel);
            panel.add(Box.createVerticalStrut(5));
        } else {
            JLabel noRatingLabel = new JLabel("No ratings available for this course.");
            noRatingLabel.setFont(new Font(noRatingLabel.getFont().getFontName(), Font.ITALIC, 12));
            noRatingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(noRatingLabel);
            panel.add(Box.createVerticalStrut(5));
        }

        JLabel subHeader = new JLabel("Section Information");
        subHeader.setFont(new Font(subHeader.getFont().getFontName(), Font.PLAIN, 12));
        subHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(subHeader);
        panel.add(Box.createVerticalStrut(5));

        // Term info
        String termDisplay = getTermDisplay(viewModel.getTerm());
        JLabel termLabel = new JLabel("Term: " + termDisplay);
        termLabel.setFont(new Font(termLabel.getFont().getFontName(), Font.ITALIC, 11));
        termLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(termLabel);
        panel.add(Box.createVerticalStrut(15));

        // Get sections from ViewModel
        List<SectionViewModel> sections = viewModel.getSectionViewModels();

        // Separate by type
        List<SectionViewModel> lectures = new ArrayList<>();
        List<SectionViewModel> tutorials = new ArrayList<>();
        List<SectionViewModel> practicals = new ArrayList<>();

        for (SectionViewModel section : sections) {
            String sectionId = section.getSectionId().toUpperCase();
            if (sectionId.startsWith("LEC")) {
                lectures.add(section);
            } else if (sectionId.startsWith("TUT")) {
                tutorials.add(section);
            } else if (sectionId.startsWith("PRA")) {
                practicals.add(section);
            } else {
                lectures.add(section);
            }
        }

        if (!lectures.isEmpty()) addSectionTypePanel(panel, "Lectures", lectures);
        if (!tutorials.isEmpty()) addSectionTypePanel(panel, "Tutorials", tutorials);
        if (!practicals.isEmpty()) addSectionTypePanel(panel, "Practicals", practicals);

        add(new JScrollPane(panel), BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }

    private void addSectionTypePanel(JPanel parentPanel, String typeTitle, List<SectionViewModel> sections) {
        JLabel typeLabel = new JLabel(typeTitle);
        typeLabel.setFont(new Font(typeLabel.getFont().getFontName(), Font.BOLD, 13));
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        parentPanel.add(typeLabel);
        parentPanel.add(Box.createVerticalStrut(8));

        for (SectionViewModel section : sections) {
            JPanel sectionPanel = createSectionPanel(section);
            parentPanel.add(sectionPanel);
            parentPanel.add(Box.createVerticalStrut(10));
        }

        parentPanel.add(Box.createVerticalStrut(10));
    }

    private JPanel createSectionPanel(SectionViewModel section) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        sectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionPanel.setMaximumSize(new Dimension(650, Integer.MAX_VALUE));

        sectionPanel.add(new JLabel("Section: " + section.getSectionId()));
        sectionPanel.add(Box.createVerticalStrut(5));

        String timeDisplay = formatTimes(section.getTimeSlotViewModels());
        sectionPanel.add(new JLabel("Time: " + timeDisplay));
        sectionPanel.add(Box.createVerticalStrut(5));

        String instructorDisplay = formatInstructors(section.getInstructors());
        sectionPanel.add(new JLabel("Instructor: " + instructorDisplay));
        sectionPanel.add(Box.createVerticalStrut(5));

        String enrollmentInfo = String.format("Enrollment: %d/%d",
                section.getEnrolledStudents(), section.getCapacity());
        JLabel enrollmentLabel = new JLabel(enrollmentInfo);
        if (section.isFull()) {
            enrollmentLabel.setForeground(Color.RED);
            enrollmentLabel.setText(enrollmentInfo + " (FULL)");
        }
        sectionPanel.add(enrollmentLabel);
        sectionPanel.add(Box.createVerticalStrut(10));

        JButton addButton = new JButton("Add to Timetable");
        addButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addButton.addActionListener(e -> onAddButtonClicked(section));
        sectionPanel.add(addButton);

        return sectionPanel;
    }

    private void onAddButtonClicked(SectionViewModel section) {
        try {
            List<TimeSlotViewModel> timeSlots = section.getTimeSlotViewModels();
            if (timeSlots.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Cannot add section: No time information available",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String instructor = "TBA";
            List<String> instructors = section.getInstructors();
            if (instructors != null && !instructors.isEmpty()) {
                instructor = instructors.get(0);
            }

            List<TimeSlotData> timeDatas = new ArrayList<>();
            for (TimeSlotViewModel ts : timeSlots) {
                timeDatas.add(new TimeSlotData(
                        ts.getDayName(),
                        ts.getStartHour(),
                        ts.getEndHour(),
                        ts.getLocation()
                ));
            }

            dispose();

            controller.addCourse(
                    viewModel.getCourseCode(),
                    section.getSectionId(),
                    instructor,
                    timeDatas
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error adding section: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatTimes(List<TimeSlotViewModel> timeSlots) {
        if (timeSlots == null || timeSlots.isEmpty()) {
            return "TBD";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < timeSlots.size(); i++) {
            TimeSlotViewModel ts = timeSlots.get(i);
            sb.append(String.format("%s %02d:00-%02d:00 @ %s",
                    ts.getDayName(), ts.getStartHour(), ts.getEndHour(), ts.getLocation()));

            if (i < timeSlots.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private String formatInstructors(List<String> instructors) {
        if (instructors == null || instructors.isEmpty()) {
            return "TBA";
        }
        return String.join(", ", instructors);
    }

    private String getTermDisplay(String term) {
        if (term == null) {
            return "Unknown";
        }
        return switch (term) {
            case "F" -> "Fall";
            case "S" -> "Winter";
            case "Y" -> "Year-long";
            default -> "Unknown";
        };
    }

    public void display() {
        setVisible(true);
    }
}
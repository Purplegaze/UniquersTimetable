package view;

import entity.Course;
import entity.Section;
import entity.TimeSlot;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SearchPanel - right side, search, search result...
 * Displays search bar, results list, and handles interactions
 */
public class SearchPanel extends JPanel {
    private JTextField searchField;
    private JButton searchButton;
    private JList<String> resultsList;
    private DefaultListModel<String> listModel;
    private List<Course> filteredCourses;
    private List<Course> courses; // sample data (placeholder)

    public SearchPanel(TimetableView timetableView) {
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(350, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initializeSampleData();

        JLabel title = new JLabel("Course Search", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        JPanel resultsPanel = new JPanel(new BorderLayout(5, 5));
        JLabel resultsLabel = new JLabel("Results:");
        resultsLabel.setFont(new Font("Arial", Font.BOLD, 14));

        listModel = new DefaultListModel<>();
        resultsList = new JList<>(listModel);
        resultsList.setFont(new Font("Arial", Font.PLAIN, 13));
        resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(resultsList);

        resultsPanel.add(resultsLabel, BorderLayout.NORTH);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel centerPanel = new JPanel(new BorderLayout(5, 10));
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(resultsPanel, BorderLayout.CENTER);
        centerPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());

        resultsList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    openCoursePopup();
                }
            }
        });

        performSearch();
    }

    private void initializeSampleData() {
        courses = new ArrayList<>();
        // Sample courses - will be replaced with API data later
        courses.add(new Course("CSC207", "Software Design",
                "Learn software design patterns and clean architecture",
                0f, null, "", new ArrayList<>(), null, 0));
        courses.add(new Course("CSC236", "Theory of Computation",
                "Introduction to computational theory and algorithms",
                0f, null, "", new ArrayList<>(), null, 0));
        courses.add(new Course("MAT237", "Multivariable Calculus",
                "Advanced calculus in multiple dimensions",
                0f, null, "", new ArrayList<>(), null, 0));
    }

    private void performSearch() {
        String query = searchField.getText().toLowerCase().trim();
        listModel.clear();
        filteredCourses = new ArrayList<>();

        for (Course course : courses) {
            String code = course.getCourseCode() != null ? course.getCourseCode() : "";
            String name = course.getCourseName() != null ? course.getCourseName() : "";

            if (query.isEmpty() || code.toLowerCase().contains(query) || name.toLowerCase().contains(query)) {
                filteredCourses.add(course);
                listModel.addElement(code + " - " + name);
            }
        }

        if (listModel.isEmpty()) {
            listModel.addElement("No results found");
        }
    }

    private void openCoursePopup() {
        int selectedIndex = resultsList.getSelectedIndex();
        if (selectedIndex >= 0 && filteredCourses != null && selectedIndex < filteredCourses.size()) {
            Course selectedCourse = filteredCourses.get(selectedIndex);
            if (selectedCourse == null) return;

            // TODO: Implement CoursePopup
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Course: " + selectedCourse.getCourseCode() + " - " + selectedCourse.getCourseName()));
            panel.add(new JLabel("Sections: " + selectedCourse.getSections()));
            panel.add(new JLabel(""));

            String[] options = { "Section Details", "Close" };

            int choice = JOptionPane.showOptionDialog(
                    this, panel, "Course Details",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );


            if (choice == 0) {
                new SectionView(selectedCourse);
            }

        }

    }
}

package view;

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
    private TimetableView timetableView;
    private List<Course> courses; // sample data (placeholder)

    public SearchPanel(TimetableView timetableView) {
        this.timetableView = timetableView;
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
        courses.add(new Course("CSC207", "Software Design", "LEC0101",
                "Learn software design patterns and clean architecture", 2, 10, 2));
        courses.add(new Course("CSC236", "Theory of Computation", "LEC0101",
                "Introduction to computational theory and algorithms", 3, 11, 2));
        courses.add(new Course("MAT237", "Multivariable Calculus", "LEC0101",
                "Advanced calculus in multiple dimensions", 1, 9, 2));
    }

    private void performSearch() {
        String query = searchField.getText().toLowerCase().trim();
        listModel.clear();

        for (Course course : courses) {
            if (query.isEmpty() ||
                    course.code.toLowerCase().contains(query) ||
                    course.name.toLowerCase().contains(query)) {
                listModel.addElement(course.code + " - " + course.name);
            }
        }

        if (listModel.isEmpty()) {
            listModel.addElement("No results found");
        }
    }

    private void openCoursePopup() {
        int selectedIndex = resultsList.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < courses.size()) {
            Course selectedCourse = null;
            String selectedText = listModel.getElementAt(selectedIndex);

            for (Course course : courses) {
                if (selectedText.startsWith(course.code)) {
                    selectedCourse = course;
                    break;
                }
            }

            if (selectedCourse != null) {
                // TODO: Implement CoursePopup
                JOptionPane.showMessageDialog(this,
                        "Course: " + selectedCourse.code + " - " + selectedCourse.name + "\n" +
                                "Section: " + selectedCourse.section + "\n" +
                                "Time: Day " + selectedCourse.day + ", " + selectedCourse.startHour + ":00\n\n" +
                                "CoursePopup will be implemented later.",
                        "Course Details (Placeholder)",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }


    static class Course {
        String code;
        String name;
        String section;
        String description;
        int day; // 1=Mon, 2=Tue, ..., 5=Fri
        int startHour;
        int duration; // hours

        Course(String code, String name, String section, String description,
               int day, int startHour, int duration) {
            this.code = code;
            this.name = name;
            this.section = section;
            this.description = description;
            this.day = day;
            this.startHour = startHour;
            this.duration = duration;
        }
    }
}
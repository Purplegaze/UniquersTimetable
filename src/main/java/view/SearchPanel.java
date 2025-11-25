package view;

import entity.Course;
import entity.Section;
import entity.TimeSlot;
import entity.Conflict;
import entity.Timetable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import interface_adapter.filter_courses.FilterCoursesController;
import interface_adapter.filter_courses.FilterCoursesPresenter;
import interface_adapter.filter_courses.FilterCoursesViewModel;
import use_case.filter_courses.FilterCoursesInteractor;

/**
 * SearchPanel - right side, search, search result...
 * Displays search bar, results list, and handles interactions
 */
public class SearchPanel extends JPanel {
    private final TimetableView timetableView;
    private JTextField searchField;
    private JButton searchButton;
    private JList<String> resultsList;
    private DefaultListModel<String> listModel;
    private List<Course> filteredCourses;
    private List<Course> courses; // sample data (placeholder)
    private JButton filterButton;
    private Integer selectedBreadth = null;

    private final FilterCoursesController filterController;
    private final FilterCoursesViewModel filterViewModel;

    public SearchPanel(TimetableView timetableView) {
        this.timetableView = timetableView;

        this.filterViewModel = new FilterCoursesViewModel();
        FilterCoursesPresenter presenter = new FilterCoursesPresenter(filterViewModel);
        FilterCoursesInteractor interactor = new FilterCoursesInteractor(presenter);
        this.filterController = new FilterCoursesController(interactor);

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

        filterButton = new JButton("Filter by breadth");
        filterButton.setFont(new Font("Arial", Font.PLAIN, 13));
        buttonsPanel.add(filterButton);
        filterButton.addActionListener(e -> openBreadthFilterDialog());

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
        // TODO: Sample courses... to be replaced with API data later
        // Breadth 1
        courses.add(new Course("ENG140", "Literature for Our Time",
                "Intro to contemporary literature and cultural analysis.",
                0f, null, "", new ArrayList<>(), null, 1));

        // Breadth 2
        courses.add(new Course("PSY100", "Intro to Psychology",
                "Overview of major areas in psychology.",
                0f, null, "", new ArrayList<>(), null, 2));
        courses.add(new Course("SOC100", "Intro to Sociology",
                "Study of society, groups, and institutions.",
                0f, null, "", new ArrayList<>(), null, 2));

        // Breadth 3
        courses.add(new Course("POL101", "Politics in the City",
                "Introduction to political institutions and processes.",
                0f, null, "", new ArrayList<>(), null, 3));

        // Breadth 4
        courses.add(new Course("BIO120", "Adaptation and Biodiversity",
                "Introductory biology focusing on evolution and ecology.",
                0f, null, "", new ArrayList<>(), null, 4));

        // Breadth 5
        courses.add(new Course("CSC207", "Software Design",
                "Object-oriented design and clean architecture.",
                0f, null, "", new ArrayList<>(), null, 5));
        courses.add(new Course("MAT237", "Multivariable Calculus",
                "Advanced calculus in multiple dimensions.",
                0f, null, "", new ArrayList<>(), null, 5));
    }

    private void performSearch() {
        String query = searchField.getText();

        listModel.clear();
        filteredCourses = new ArrayList<>();

        filterController.execute(courses, selectedBreadth, query);

        List<Course> result = filterViewModel.getFilteredCourses();
        filteredCourses.addAll(result);

        for (Course course : result) {
            String code = course.getCourseCode() != null ? course.getCourseCode() : "";
            String name = course.getCourseName() != null ? course.getCourseName() : "";
            listModel.addElement(code + " - " + name);
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

    private void openBreadthFilterDialog() {
        String[] options = {"All", "1", "2", "3", "4", "5"};
        String choice = (String) JOptionPane.showInputDialog(
                this,
                "Select breadth category:",
                "Breadth Filter",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                selectedBreadth == null ? "All" : selectedBreadth.toString()
        );

        if (choice == null) {
            return; // user cancelled
        }
        if (choice.equals("All")) {
            selectedBreadth = null;
        } else {
            selectedBreadth = Integer.parseInt(choice);
        }
        performSearch(); // re-run search with new filter
    }
}

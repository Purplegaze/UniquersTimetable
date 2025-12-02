package view;

import interface_adapter.search.SearchCourseController;
import interface_adapter.search.SearchViewModel;
import interface_adapter.search.SearchViewModel.SearchResult;
import interface_adapter.customtimefilter.CustomTimeFilterController;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * SearchPanel - Pure UI component following Clean Architecture.
 * Implements SearchPanelInterface to receive data from presenter.
 */
public class SearchPanel extends JPanel implements PropertyChangeListener {

    // ==================== Listener Interface ====================

    /**
     * Interface for handling user actions from this panel.
     * The controller implements this.
     */
    public interface SearchPanelListener {
        void onSearchRequested(String query);
        void onCustomTimeFilterRequested(String query,
                                         String dayOfWeek,
                                         String startTime,
                                         String endTime);
        void onResultSelected(String resultId);
    }

    // ==================== UI Components ====================

    private JTextField searchField;
    private JButton searchButton;
    private JList<String> resultsList;
    private DefaultListModel<String> listModel;
    //  components for custom time filter
    private JComboBox<String> dayComboBox;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JButton timeFilterButton;

    private List<SearchResult> currentResults = new ArrayList<>();
    private SearchCourseController controller;
    private SearchViewModel viewModel;
    private SearchPanelListener listener;
    private CustomTimeFilterController customTimeFilterController;

    public SearchPanel() {
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(350, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initializeComponents();
        layoutComponents();
        setupEventHandlers();
    }

    public void setController(SearchCourseController controller) {
        this.controller = controller;
    }

    public void setViewModel(SearchViewModel viewModel) {
        if (this.viewModel != null) {
            this.viewModel.removePropertyChangeListener(this);
        }

        this.viewModel = viewModel;
        viewModel.addPropertyChangeListener(this);
    }
    public void setCustomTimeFilterController(CustomTimeFilterController customTimeFilterController) {
        this.customTimeFilterController = customTimeFilterController;
    }
    public void setListener(SearchPanelListener listener) {
        this.listener = listener;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "results":
                handleResults();
                break;
            case "noResults":
                handleNoResults();
                break;
            case "error":
                handleError();
                break;
        }
    }

    private void handleResults() {
        if (viewModel == null) return;

        List<SearchResult> results = viewModel.getResults();
        this.currentResults = new ArrayList<>(results);
        listModel.clear();

        for (SearchResult result : results) {
            listModel.addElement(result.getDisplayText());
        }
    }

    private void handleNoResults() {
        this.currentResults = new ArrayList<>();
        listModel.clear();
        listModel.addElement("No results found");
    }

    private void handleError() {
        if (viewModel == null) return;

        JOptionPane.showMessageDialog(
                this,
                viewModel.getErrorMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void initializeComponents() {
        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));

        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));

        listModel = new DefaultListModel<>();
        resultsList = new JList<>(listModel);
        resultsList.setFont(new Font("Arial", Font.PLAIN, 13));
        resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        dayComboBox = new JComboBox<>(days);
        dayComboBox.setFont(new Font("Arial", Font.PLAIN, 12));

        startTimeField = new JTextField("13:00");  // default example
        startTimeField.setFont(new Font("Arial", Font.PLAIN, 12));

        endTimeField = new JTextField("14:00");
        endTimeField.setFont(new Font("Arial", Font.PLAIN, 12));

        timeFilterButton = new JButton("Filter by Time");
        timeFilterButton.setFont(new Font("Arial", Font.BOLD, 12));
    }

    private void layoutComponents() {
        JLabel title = new JLabel("Course Search", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        JPanel searchBarPanel = new JPanel(new BorderLayout(5, 5));
        searchBarPanel.add(searchField, BorderLayout.CENTER);
        searchBarPanel.add(searchButton, BorderLayout.EAST);

        // === New time filter panel ===
        JPanel timeFilterPanel = new JPanel(new GridLayout(2, 2, 5, 5));

        // First row — day + start
        timeFilterPanel.add(new JLabel("Day:"));
        timeFilterPanel.add(dayComboBox);

        // Second row — start + end time fields
        JPanel timeInputs = new JPanel(new GridLayout(1, 2, 5, 5));
        timeInputs.add(startTimeField);
        timeInputs.add(endTimeField);

        timeFilterPanel.add(new JLabel("Time Range:"));
        timeFilterPanel.add(timeInputs);

        // Add time filter button below
        JPanel timeFilterButtonPanel = new JPanel(new BorderLayout());
        timeFilterButtonPanel.add(timeFilterButton, BorderLayout.CENTER);

        JPanel resultsPanel = new JPanel(new BorderLayout(5, 5));
        JLabel resultsLabel = new JLabel("Results:");
        resultsLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(resultsList);

        resultsPanel.add(resultsLabel, BorderLayout.NORTH);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel centerPanel = new JPanel(new BorderLayout(5, 10));
        // A new panel to stack search bar + time filter vertically
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        topPanel.add(searchBarPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 8)));  // small spacing
        topPanel.add(timeFilterPanel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        topPanel.add(timeFilterButton);

        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(resultsPanel, BorderLayout.CENTER);
        centerPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        searchButton.addActionListener(e -> notifySearchRequested());
        searchField.addActionListener(e -> notifySearchRequested());
        // New: time filter button
        timeFilterButton.addActionListener(e -> notifyCustomTimeFilterRequested());
        resultsList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    notifyResultSelected();
                }
            }
        });
    }
    private void notifyCustomTimeFilterRequested() {
        if (customTimeFilterController != null) {
            String query = searchField.getText().trim();
            String day = (String) dayComboBox.getSelectedItem();
            String start = startTimeField.getText().trim();
            String end = endTimeField.getText().trim();

            customTimeFilterController.execute(query, day, start, end);
        }
    }

    private void notifyResultSelected() {
        int selectedIndex = resultsList.getSelectedIndex();

        if (selectedIndex >= 0 && selectedIndex < currentResults.size()) {
            String courseCode = currentResults.get(selectedIndex).getCourseCode();

            if (viewModel != null) {
                viewModel.setSelectedCourseCode(courseCode);
            }
        }
    }

    private void notifySearchRequested() {
        if (controller != null) {
            String query = searchField.getText().trim();
            controller.execute(query);
        }
    }
}
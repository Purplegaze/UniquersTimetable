package view;

import interface_adapter.search.SearchCourseController;
import interface_adapter.search.SearchViewModel;
import interface_adapter.search.SearchViewModel.SearchResult;

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

    // ==================== UI Components ====================

    private JTextField searchField;
    private JButton searchButton;
    private JList<String> resultsList;
    private DefaultListModel<String> listModel;

    private List<SearchResult> currentResults = new ArrayList<>();
    private SearchCourseController controller;
    private SearchViewModel viewModel;

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
    }

    private void layoutComponents() {
        JLabel title = new JLabel("Course Search", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        JPanel searchBarPanel = new JPanel(new BorderLayout(5, 5));
        searchBarPanel.add(searchField, BorderLayout.CENTER);
        searchBarPanel.add(searchButton, BorderLayout.EAST);

        JPanel resultsPanel = new JPanel(new BorderLayout(5, 5));
        JLabel resultsLabel = new JLabel("Results:");
        resultsLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(resultsList);

        resultsPanel.add(resultsLabel, BorderLayout.NORTH);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel centerPanel = new JPanel(new BorderLayout(5, 10));
        centerPanel.add(searchBarPanel, BorderLayout.NORTH);
        centerPanel.add(resultsPanel, BorderLayout.CENTER);
        centerPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        searchButton.addActionListener(e -> notifySearchRequested());
        searchField.addActionListener(e -> notifySearchRequested());

        resultsList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    notifyResultSelected();
                }
            }
        });
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
package view;

import interface_adapter.presenter.SearchPanelInterface;
import interface_adapter.viewmodel.SearchResultViewModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SearchPanel - Pure UI component following Clean Architecture.
 * Implements SearchPanelInterface to receive data from presenter.
 */
public class SearchPanel extends JPanel implements SearchPanelInterface {

    // ==================== Listener Interface ====================

    /**
     * Interface for handling user actions from this panel.
     * The controller implements this.
     */
    public interface SearchPanelListener {
        void onSearchRequested(String query);
        void onResultSelected(String resultId);
    }

    // ==================== UI Components ====================

    private JTextField searchField;
    private JButton searchButton;
    private JList<String> resultsList;
    private DefaultListModel<String> listModel;

    private List<SearchResultViewModel> currentResults = new ArrayList<>();
    private SearchPanelListener listener;

    // ==================== Constructor ====================

    public SearchPanel() {
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(350, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initializeComponents();
        layoutComponents();
        setupEventHandlers();
    }

    // ==================== Public Methods ====================

    public void setListener(SearchPanelListener listener) {
        this.listener = listener;
    }

    // ==================== Interface Implementation ====================

    @Override
    public void displaySearchResults(List<SearchResultViewModel> results) {
        this.currentResults = new ArrayList<>(results);
        listModel.clear();

        for (SearchResultViewModel vm : results) {
            listModel.addElement(vm.getDisplayText());
        }
    }

    @Override
    public void showNoResultsMessage() {
        this.currentResults = new ArrayList<>();
        listModel.clear();
        listModel.addElement("No results found");
    }

    @Override
    public void clearResults() {
        this.currentResults = new ArrayList<>();
        listModel.clear();
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // ==================== Additional Helper Methods ====================

    public void clearSearchField() {
        searchField.setText("");
    }

    public String getSearchQuery() {
        return searchField.getText();
    }

    // ==================== Private UI Setup ====================

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

    // ==================== Event Notification ====================

    private void notifySearchRequested() {
        if (listener != null) {
            String query = searchField.getText().trim();
            listener.onSearchRequested(query);
        }
    }

    private void notifyResultSelected() {
        int selectedIndex = resultsList.getSelectedIndex();

        if (selectedIndex < 0 || selectedIndex >= currentResults.size()) {
            return;
        }

        if (listener != null) {
            String selectedId = currentResults.get(selectedIndex).getCourseCode();
            listener.onResultSelected(selectedId);
        }
    }
}
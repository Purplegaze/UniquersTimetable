package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SearchPanel - Pure UI component following Clean Architecture.
 * <p>
 * Responsibilities:
 * - Render the search UI (search bar, results list)
 * - Capture user input and forward to controller
 * - Display data provided by presenter via ViewModels
 * <p>
 * Does NOT:
 * - Know about domain entities (Course, Section, etc.)
 * - Perform search logic
 * - Access data sources
 */
public class SearchPanel extends JPanel {

    // ==================== View Model ====================

    /**
     * Simple data container for displaying search results.
     * This is NOT an entity - just what the UI needs to display.
     */
    public static class SearchResultItem {
        private final String id;
        private final String displayText;

        public SearchResultItem(String id, String displayText) {
            this.id = id;
            this.displayText = displayText;
        }

        public String getId() { return id; }
        public String getDisplayText() { return displayText; }
    }

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

    private List<SearchResultItem> currentResults = new ArrayList<>();
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

    public void displayResults(List<SearchResultItem> results) {
        this.currentResults = new ArrayList<>(results);
        listModel.clear();

        for (SearchResultItem item : results) {
            listModel.addElement(item.getDisplayText());
        }
    }

    public void displayNoResults() {
        this.currentResults = new ArrayList<>();
        listModel.clear();
        listModel.addElement("No results found");
    }

    public void displayError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

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
            String selectedId = currentResults.get(selectedIndex).getId();
            listener.onResultSelected(selectedId);
        }
    }
}

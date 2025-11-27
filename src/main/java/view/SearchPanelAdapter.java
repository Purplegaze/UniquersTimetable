package view;

import interface_adapter.presenter.SearchPanelInterface;
import interface_adapter.viewmodel.SearchResultViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that connects SearchPanel to SearchPanelInterface.
 */
public class SearchPanelAdapter implements SearchPanelInterface {

    private final SearchPanel searchPanel;

    public SearchPanelAdapter(SearchPanel searchPanel) {
        if (searchPanel == null) {
            throw new IllegalArgumentException("SearchPanel cannot be null");
        }
        this.searchPanel = searchPanel;
    }

    @Override
    public void displaySearchResults(List<SearchResultViewModel> results) {
        // Convert SearchResultViewModel (from presenter) to SearchResultItem (for view)
        List<SearchPanel.SearchResultItem> items = new ArrayList<>();

        for (SearchResultViewModel result : results) {
            items.add(new SearchPanel.SearchResultItem(
                    result.getCourseCode(),
                    result.getDisplayText()
            ));
        }

        searchPanel.displayResults(items);
    }

    @Override
    public void showNoResultsMessage() {
        searchPanel.displayNoResults();
    }

    @Override
    public void clearResults() {
        searchPanel.displayResults(new ArrayList<>());
    }

    @Override
    public void showError(String message) {
        searchPanel.displayError(message);
    }
}

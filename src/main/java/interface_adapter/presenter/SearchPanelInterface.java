package interface_adapter.presenter;

import interface_adapter.viewmodel.SearchResultViewModel;
import java.util.List;

/**
 * View Interface for Search Course.
 */
public interface SearchPanelInterface {

    void displaySearchResults(List<SearchResultViewModel> results);

    void showNoResultsMessage();

    void clearResults();

    void showError(String message);
}

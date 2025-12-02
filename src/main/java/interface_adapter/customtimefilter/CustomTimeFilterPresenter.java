package interface_adapter.customtimefilter;

import interface_adapter.search.SearchViewModel;
import interface_adapter.search.SearchViewModel.SearchResult;
import usecase.customtimefilter.CustomTimeFilterOutputBoundary;
import usecase.customtimefilter.CustomTimeFilterOutputData;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for Use Case 3: Custom Time Filter.
 *
 * This presenter reuses the existing SearchViewModel so that
 * custom-time-filter results appear in the same SearchPanel UI
 * as normal search results.
 */
public class CustomTimeFilterPresenter implements CustomTimeFilterOutputBoundary {

    private final SearchViewModel searchViewModel;

    public CustomTimeFilterPresenter(SearchViewModel searchViewModel) {
        if (searchViewModel == null) {
            throw new IllegalArgumentException("SearchViewModel cannot be null");
        }
        this.searchViewModel = searchViewModel;
    }

    @Override
    public void presentResults(CustomTimeFilterOutputData outputData) {
        if (outputData == null || outputData.getResults() == null || outputData.getResults().isEmpty()) {
            presentNoResults();
            return;
        }

        // Convert CustomTimeFilterOutputData into SearchViewModel.SearchResult
        List<SearchResult> results = new ArrayList<>();
        for (CustomTimeFilterOutputData.ResultItem item : outputData.getResults()) {
            results.add(new SearchResult(
                    item.getCourseCode(),
                    item.getCourseName()
            ));
        }

        // Update the shared SearchViewModel so the SearchPanel UI refreshes
        searchViewModel.setResults(results);
    }

    @Override
    public void presentNoResults() {
        // Let the SearchViewModel broadcast a "no results" event
        searchViewModel.setNoResults();
    }

    @Override
    public void presentError(String errorMessage) {
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "Failed to apply custom time filter.";
        }
        searchViewModel.setError(errorMessage);
    }
}
package interface_adapter.customtimefilter;

import interface_adapter.presenter.SearchPanelInterface;
import interface_adapter.viewmodel.SearchResultViewModel;
import usecase.customtimefilter.CustomTimeFilterOutputBoundary;
import usecase.customtimefilter.CustomTimeFilterOutputData;
import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for Use Case 3: Custom Time Filter.
 * Converts OutputData into SearchResultViewModel and updates the SearchPanel via SearchPanelInterface.
 */
public class CustomTimeFilterPresenter implements CustomTimeFilterOutputBoundary {

    private final SearchPanelInterface view;

    public CustomTimeFilterPresenter(SearchPanelInterface view) {
        if (view == null) {
            throw new IllegalArgumentException("View cannot be null");
        }
        this.view = view;
    }

    @Override
    public void presentResults(CustomTimeFilterOutputData outputData) {
        if (outputData == null || outputData.getResults() == null) {
            presentNoResults();
            return;
        }

        List<SearchResultViewModel> viewModels = new ArrayList<>();

        for (CustomTimeFilterOutputData.ResultItem item : outputData.getResults()) {
            // term and hasAvailableSections are not provided in this use case,
            // so we can fill them with reasonable placeholder values.
            String courseCode = item.getCourseCode();
            String courseName = item.getCourseName();
            String term = "";              // or "N/A"
            boolean hasAvailableSections = true;

            viewModels.add(new SearchResultViewModel(
                    courseCode,
                    courseName,
                    term,
                    hasAvailableSections
            ));
        }

        view.displaySearchResults(viewModels);
    }

    @Override
    public void presentNoResults() {
        view.clearResults();
        view.showNoResultsMessage();
    }

    @Override
    public void presentError(String errorMessage) {
        view.showError(errorMessage);
    }
}
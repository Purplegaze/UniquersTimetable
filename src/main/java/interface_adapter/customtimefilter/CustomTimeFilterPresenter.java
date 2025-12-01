package interface_adapter.customtimefilter;

import interface_adapter.viewmodel.SearchResultViewModel;
import usecase.customtimefilter.CustomTimeFilterOutputBoundary;
import usecase.customtimefilter.CustomTimeFilterOutputData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Presenter for Use Case 3: Custom Time Filter.
 * Converts OutputData into SearchResultViewModel and updates the SearchPanel via SearchPanelInterface.
 */
public class CustomTimeFilterPresenter implements CustomTimeFilterOutputBoundary {

    private final CustomTimeFilterViewModel viewModel;

    public CustomTimeFilterPresenter(CustomTimeFilterViewModel viewModel) {
        if (viewModel == null) {
            throw new IllegalArgumentException("ViewModel cannot be null");
        }
        this.viewModel = viewModel;
    }

    @Override
    public void presentResults(CustomTimeFilterOutputData outputData) {
        if (outputData == null || outputData.getResults() == null || outputData.getResults().isEmpty()) {
            presentNoResults();
            return;
        }

        List<SearchResultViewModel> viewModels = new ArrayList<>();

        for (CustomTimeFilterOutputData.ResultItem item : outputData.getResults()) {
            String courseCode = item.getCourseCode();
            String courseName = item.getCourseName();
            String term = "";
            boolean hasAvailableSections = true;

            viewModels.add(new SearchResultViewModel(
                    courseCode,
                    courseName,
                    term,
                    hasAvailableSections
            ));
        }

        viewModel.setResults(viewModels);
        viewModel.setNoResults(false);
        viewModel.setErrorMessage(null);
    }

    @Override
    public void presentNoResults() {
        viewModel.setResults(Collections.emptyList());
        viewModel.setNoResults(true);
        viewModel.setErrorMessage(null);
    }

    @Override
    public void presentError(String errorMessage) {
        viewModel.setErrorMessage(errorMessage);
        viewModel.setNoResults(false);
    }
}
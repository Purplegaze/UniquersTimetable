package interface_adapter.customtimefilter;

import usecase.customtimefilter.CustomTimeFilterOutputBoundary;
import usecase.customtimefilter.CustomTimeFilterOutputData;

/**
 * Presenter for Use Case 3: Custom Time Filter.
 * Converts OutputData into ViewModel updates.
 */
public class CustomTimeFilterPresenter implements CustomTimeFilterOutputBoundary {

    // TODO: later inject a ViewModel here
    // private final CustomTimeFilterViewModel viewModel;

    public CustomTimeFilterPresenter() {
        // this.viewModel = viewModel;
    }

    @Override
    public void presentResults(CustomTimeFilterOutputData outputData) {
        // TODO: Update ViewModel instead of printing
        System.out.println("Presenter: showing " + outputData.getResults().size() + " result(s)");
    }

    @Override
    public void presentNoResults() {
        // TODO: Update ViewModel for "no results"
        System.out.println("Presenter: No results found.");
    }

    @Override
    public void presentError(String errorMessage) {
        // TODO: Update ViewModel with error message
        System.out.println("Presenter Error: " + errorMessage);
    }
}
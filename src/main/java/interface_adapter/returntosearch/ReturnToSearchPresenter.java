package interface_adapter.returntosearch;

import usecase.returntosearch.ReturnToSearchOutputBoundary;
import usecase.returntosearch.ReturnToSearchOutputData;
import view.SearchPanel;

public class ReturnToSearchPresenter implements ReturnToSearchOutputBoundary {
    private final SearchPanel searchPanel;

    public ReturnToSearchPresenter(SearchPanel searchPanel) {
        this.searchPanel = searchPanel;
    }

    @Override
    public void prepareSuccessView(ReturnToSearchOutputData outputData) {
        // 1. Set the search bar text to the course code
        searchPanel.setSearchQuery(outputData.getCourseCode());

        // 2. Automatically trigger the search so the results appear immediately
        searchPanel.performSearch();
    }
}
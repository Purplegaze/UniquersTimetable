package interface_adapter.search;

import usecase.search.SearchCourseInputBoundary;
import usecase.search.SearchCourseInputData;

/**
 * Controller for the Search Course use case.
 */
public class SearchCourseController {

    private final SearchCourseInputBoundary searchCourseInteractor;

    public SearchCourseController(SearchCourseInputBoundary searchCourseInteractor) {
        if (searchCourseInteractor == null) {
            throw new IllegalArgumentException("Interactor cannot be null");
        }
        this.searchCourseInteractor = searchCourseInteractor;
    }

    /**
     * Handle search request from the view.
     */
    public void search(String query) {
        // Create Input Data (simple primitive wrapper)
        SearchCourseInputData inputData = new SearchCourseInputData(query);
        
        // Execute use case through Input Boundary interface
        searchCourseInteractor.execute(inputData);
    }

    /**
     * Handle search with empty query (show all courses).
     */
    public void searchAll() {
        search("");
    }

    /**
     * Handle clear search request.
     */
    public void clearSearch() {
        search("");
    }
}

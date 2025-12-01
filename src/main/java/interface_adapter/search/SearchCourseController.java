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

    public void execute(String query) {
        SearchCourseInputData inputData = new SearchCourseInputData(query);
        searchCourseInteractor.execute(inputData);
    }

    /**
     * Handle search with empty query (show all courses).
     */
    public void searchAll() {
        execute("");
    }

    /**
     * Handle clear search request.
     */
    public void clearSearch() {
        execute("");
    }
}

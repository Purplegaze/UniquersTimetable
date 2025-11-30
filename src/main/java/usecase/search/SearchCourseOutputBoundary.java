package usecase.search;

/**
 * Output boundary for the Search Course use case.
 */
public interface SearchCourseOutputBoundary {
    void presentSearchResults(SearchCourseOutputData outputData);
    void presentNoResults();
    void presentError(String errorMessage);
}

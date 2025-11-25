package usecase.search;

/**
 * Output boundary for the Search Course use case.
 * The presenter implements this interface.
 */
public interface SearchCourseOutputBoundary {
    void presentSearchResults(SearchCourseOutputData outputData);
    void presentNoResults();
    void presentError(String errorMessage);
}

package usecase.search;

/**
 * Input data for the Search Course use case.
 */
public class SearchCourseInputData {
    private final String query;

    public SearchCourseInputData(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}

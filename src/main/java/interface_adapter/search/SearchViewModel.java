package interface_adapter.search;

import interface_adapter.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {

    public static class SearchResult {
        private final String courseCode;
        private final String courseName;

        public SearchResult(String courseCode, String courseName) {
            this.courseCode = courseCode;
            this.courseName = courseName;
        }

        public String getCourseCode() { return courseCode; }
        public String getCourseName() { return courseName; }

        public String getDisplayText() {
            return courseCode + " - " + courseName;
        }
    }

    private List<SearchResult> results = new ArrayList<>();
    private String errorMessage = "";

    public SearchViewModel() {
        super("search");
    }

    public List<SearchResult> getResults() {
        return new ArrayList<>(results);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setResults(List<SearchResult> results) {
        this.results = new ArrayList<>(results);
        firePropertyChanged("results");
    }

    public void setNoResults() {
        this.results = new ArrayList<>();
        firePropertyChanged("noResults");
    }

    public void setError(String message) {
        this.errorMessage = message;
        firePropertyChanged("error");
    }
}
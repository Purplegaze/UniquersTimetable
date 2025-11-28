package interface_adapter.viewmodel;

/**
 * View Model for displaying search results.
 */
public class SearchResultViewModel {
    
    private final String courseCode;
    private final String courseName;
    private final String term;
    private final Boolean hasAvailableSections;

    public SearchResultViewModel(String courseCode, 
                                String courseName,
                                String term,
                                Boolean hasAvailableSections) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.term = term;
        this.hasAvailableSections = hasAvailableSections;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getTerm() {
        return term;
    }

    /**
     * Get display text for search result.
     */
    public String getDisplayText() {
        return String.format("%s - %s",
                courseCode,
                courseName);
    }
}

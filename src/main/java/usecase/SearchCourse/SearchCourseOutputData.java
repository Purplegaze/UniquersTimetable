package usecase.SearchCourse;

import java.util.List;

/**
 * Output data from the Search Course use case.
 */
public class SearchCourseOutputData {

    public static class ResultItem {
        private final String courseCode;
        private final String courseName;

        public ResultItem(String courseCode, String courseName) {
            this.courseCode = courseCode;
            this.courseName = courseName;
        }

        public String getCourseCode() { return courseCode; }
        public String getCourseName() { return courseName; }
    }

    private final List<ResultItem> results;

    public SearchCourseOutputData(List<ResultItem> results) {
        this.results = results;
    }

    public List<ResultItem> getResults() {
        return results;
    }
}

package usecase.customtimefilter;

import java.util.List;

public class CustomTimeFilterOutputData {

    public static class ResultItem {
        private final String courseCode;
        private final String courseName;
        private final String sectionTime; // such as "Mon 10:00–11:00"

        public ResultItem(String courseCode, String courseName, String sectionTime) {
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.sectionTime = sectionTime;
        }

        public String getCourseCode() {
            return courseCode;
        }

        public String getCourseName() {
            return courseName;
        }

        public String getSectionTime() {
            return sectionTime;
        }
    }

    private final List<ResultItem> results;

    public CustomTimeFilterOutputData(List<ResultItem> results) {
        this.results = results;
    }

    public List<ResultItem> getResults() {
        return results;
    }
}
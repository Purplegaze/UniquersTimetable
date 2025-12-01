package usecase.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Output data from the Search Course use case.
 */
public class SearchCourseOutputData {

    public static class CourseData {
        private final String courseCode;
        private final String courseName;

        public CourseData(String courseCode, String courseName) {
            this.courseCode = courseCode;
            this.courseName = courseName;
        }

        public String getCourseCode() {
            return courseCode;
        }

        public String getCourseName() {
            return courseName;
        }
    }

    private final List<CourseData> courses;

    public SearchCourseOutputData(List<CourseData> courses) {
        this.courses = courses != null ? new ArrayList<>(courses) : new ArrayList<>();
    }

    public List<CourseData> getCourses() {
        return new ArrayList<>(courses);
    }

    public int getResultCount() {
        return courses.size();
    }

    public boolean hasResults() {
        return !courses.isEmpty();
    }
}
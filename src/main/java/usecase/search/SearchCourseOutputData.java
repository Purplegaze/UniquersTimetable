package usecase.search;

import entity.Course;
import java.util.ArrayList;
import java.util.List;

/**
 * Output data from the Search Course use case.
 */
public class SearchCourseOutputData {

    private final List<Course> courses;

    public SearchCourseOutputData(List<Course> courses) {
        this.courses = courses != null ? new ArrayList<>(courses) : new ArrayList<>();
    }

    public List<Course> getCourses() {
        return new ArrayList<>(courses);
    }

    public int getResultCount() {
        return courses.size();
    }

    public boolean hasResults() {
        return !courses.isEmpty();
    }
}
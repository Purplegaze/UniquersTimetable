package use_case.filter_courses;

import java.util.List;
import entity.Course;

public class FilterCoursesOutputData {
    private final List<Course> courses;

    public FilterCoursesOutputData(List<Course> courses) {
        this.courses = courses;
    }

    public List<Course> getCourses() {
        return courses;
    }
}

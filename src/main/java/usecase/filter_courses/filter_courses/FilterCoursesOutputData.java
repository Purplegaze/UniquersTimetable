package usecase.filter_courses;

import entity.Course;
import java.util.List;

public class FilterCoursesOutputData {
    private final List<Course> courses;

    public FilterCoursesOutputData(List<Course> courses) {
        this.courses = courses;
    }

    public List<Course> getCourses() {
        return courses;
    }
}
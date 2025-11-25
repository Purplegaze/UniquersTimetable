package interface_adapter.course_filter;

import java.util.List;
import entity.Course;

public class FilterCoursesViewModel {
    private List<Course> courses;

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}


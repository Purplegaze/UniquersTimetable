package interface_adapter.filter_courses;

import entity.Course;
import java.util.List;

public class FilterCoursesViewModel {

    private List<Course> courses;

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}
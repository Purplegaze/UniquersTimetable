package interface_adapter;

import entity.Course;
import java.util.ArrayList;
import java.util.List;

public class CourseFilter {
    public List<Course> filterByBreadth(List<Course> courses, int breadth) {
        List<Course> result = new ArrayList<>();
        for (Course c : courses) {
            if (c.getBreadthCategory() == breadth) {
                result.add(c);
            }
        }
        return result;
    }
}
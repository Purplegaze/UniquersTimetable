package data_access;

import entity.Course;
import java.util.List;

/**
 * Course data access implementation using JSON file with real course data.
 */
public class JSONCourseDataAccess implements CourseDataAccessInterface {

    private final List<Course> courses;

    public JSONCourseDataAccess() {
        JSONParser parser = new JSONParser();
        this.courses = parser.getCourses();
    }

    public JSONCourseDataAccess(String filename) {
        JSONParser parser = new JSONParser(filename);
        this.courses = parser.getCourses();
    }

    @Override
    public List<Course> getAllCourses() {
        return courses;
    }

    @Override
    public Course findByCourseCode(String courseCode) {
        for (Course course : courses) {
            if (course.getCourseCode() != null
                    && course.getCourseCode().equalsIgnoreCase(courseCode)) {
                return course;
            }
        }
        return null;
    }
}

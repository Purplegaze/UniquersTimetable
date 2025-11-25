package data_access;

import entity.Course;
import java.util.List;

/**
 * Data access interface for Course entities.
 */
public interface CourseDataAccessInterface {

    /**
     * Get all available courses.
     * @return list of all courses
     */
    List<Course> getAllCourses();

    /**
     * Find a course by its course code.
     * @param courseCode the course code to search for
     * @return the course if found, null otherwise
     */
    Course findByCourseCode(String courseCode);
}
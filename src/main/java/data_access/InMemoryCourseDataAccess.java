package data_access;

import entity.Course;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory implementation with sample data.
 * Replace with API implementation later。
 */
public class InMemoryCourseDataAccess implements CourseDataAccessInterface {

    private final List<Course> courses;

    public InMemoryCourseDataAccess() {
        this.courses = new ArrayList<>();
        initializeSampleData();
    }

    @Override
    public List<Course> getAllCourses() {
        return new ArrayList<>(courses);
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

    /**
     * Sample data (will be replaced with API calls later)
     */
    private void initializeSampleData() {
        // CSC Courses
        courses.add(new Course("CSC108", "Introduction to Computer Programming",
                "An introduction to programming using Python",
                0f, null, "", new ArrayList<>(), null, 0));

        courses.add(new Course("CSC207", "Software Design",
                "Software design patterns and clean architecture",
                0f, null, "", new ArrayList<>(), null, 0));

        courses.add(new Course("CSC209", "Software Tools and Systems Programming",
                "Unix/Linux, C programming, system calls",
                0f, null, "", new ArrayList<>(), null, 0));

        courses.add(new Course("CSC236", "Introduction to the Theory of Computation",
                "Mathematical induction, correctness proofs, regular languages",
                0f, null, "", new ArrayList<>(), null, 0));

        courses.add(new Course("CSC258", "Computer Organization",
                "Computer hardware, assembly language, MIPS",
                0f, null, "", new ArrayList<>(), null, 0));

        courses.add(new Course("CSC263", "Data Structures and Analysis",
                "Algorithm analysis, heaps, BST, hashing, graphs",
                0f, null, "", new ArrayList<>(), null, 0));

        courses.add(new Course("CSC309", "Programming on the Web",
                "Web development, JavaScript, React, databases",
                0f, null, "", new ArrayList<>(), null, 0));

        courses.add(new Course("CSC311", "Introduction to Machine Learning",
                "Supervised learning, neural networks, clustering",
                0f, null, "", new ArrayList<>(), null, 0));

        // MAT Courses
        courses.add(new Course("MAT135", "Calculus I",
                "Limits, derivatives, integrals",
                0f, null, "", new ArrayList<>(), null, 0));

        courses.add(new Course("MAT223", "Linear Algebra I",
                "Vectors, matrices, linear transformations",
                0f, null, "", new ArrayList<>(), null, 0));

        courses.add(new Course("MAT237", "Multivariable Calculus with Proofs",
                "Rigorous treatment of multivariable calculus",
                0f, null, "", new ArrayList<>(), null, 0));
        // Other
        courses.add(new Course("ECO101", "Principles of Microeconomics",
                "Supply and demand, market structures, consumer theory",
                0f, null, "", new ArrayList<>(), null, 0));

        courses.add(new Course("PSY100", "Introduction to Psychology",
                "Overview of psychological science",
                0f, null, "", new ArrayList<>(), null, 0));
    }
}

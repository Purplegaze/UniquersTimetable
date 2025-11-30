package usecase.viewcourse;

import entity.Course;

public class ViewCourseOutputData {
    private final Course course;

    public ViewCourseOutputData(Course course) {
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }
}
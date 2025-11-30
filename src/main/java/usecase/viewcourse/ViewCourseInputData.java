package usecase.viewcourse;

public class ViewCourseInputData {
    private final String courseCode;

    public ViewCourseInputData(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseCode() {
        return courseCode;
    }
}
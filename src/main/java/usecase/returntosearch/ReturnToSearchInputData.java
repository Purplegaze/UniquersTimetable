package usecase.returntosearch;

public class ReturnToSearchInputData {
    private final String courseCode;

    public ReturnToSearchInputData(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseCode() {
        return courseCode;
    }
}
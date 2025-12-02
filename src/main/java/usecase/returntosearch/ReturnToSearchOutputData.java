package usecase.returntosearch;

public class ReturnToSearchOutputData {
    private final String courseCode;

    public ReturnToSearchOutputData(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseCode() {
        return courseCode;
    }
}
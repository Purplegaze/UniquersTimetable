package usecase.viewcourse;

public interface ViewCourseOutputBoundary {
    void prepareSuccessView(ViewCourseOutputData outputData);
    void prepareFailView(String error);
}
package usecase.addcourse;

/**
 * Output boundary for the Add Course use case.
 */
public interface AddCourseOutputBoundary {
    void presentSuccess(AddCourseOutputData outputData);
    void presentConflict(AddCourseOutputData outputData);
    void presentError(String errorMessage);
}

package usecase.addcourse;

/**
 * Output Boundary for Add Course Use Case.
 */
public interface AddCourseOutputBoundary {

    void presentSuccess(AddCourseOutputData outputData);

    void presentError(String errorMessage);
}

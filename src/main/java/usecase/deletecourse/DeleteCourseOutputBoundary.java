package usecase.deletecourse;

/**
 * Output Boundary for Delete Course use case.
 */
public interface DeleteCourseOutputBoundary {
    /**
     * Present successful deletion result.
     */
    void presentSuccess(DeleteCourseOutputData outputData);
    
    /**
     * Present error message.
     */
    void presentError(String errorMessage);
}

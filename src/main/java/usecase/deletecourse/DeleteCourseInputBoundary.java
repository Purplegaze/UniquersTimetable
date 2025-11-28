package usecase.deletecourse;

/**
 * Input Boundary for Delete Course use case.
 */
public interface DeleteCourseInputBoundary {
    /**
     * Execute the delete course use case.
     */
    void execute(DeleteCourseInputData inputData);
}

package interface_adapter.controller;

import usecase.deletecourse.DeleteCourseInputBoundary;
import usecase.deletecourse.DeleteCourseInputData;

/**
 * Controller for Delete Course use case.
 */
public class DeleteCourseController {
    
    private final DeleteCourseInputBoundary deleteCourseInteractor;
    
    public DeleteCourseController(DeleteCourseInputBoundary deleteCourseInteractor) {
        if (deleteCourseInteractor == null) {
            throw new IllegalArgumentException("Interactor cannot be null");
        }
        this.deleteCourseInteractor = deleteCourseInteractor;
    }
    
    /**
     * Handle delete course request from the view.
     */
    public void deleteSection(String courseCode, String sectionCode) {
        try {
            // Create input data from primitives
            DeleteCourseInputData inputData = new DeleteCourseInputData(courseCode, sectionCode);

            deleteCourseInteractor.execute(inputData);
            
        } catch (IllegalArgumentException e) {
            // Input validation failed
            throw e;
        } catch (Exception e) {
            // Unexpected error
            throw new RuntimeException("Failed to delete section", e);
        }
    }
}

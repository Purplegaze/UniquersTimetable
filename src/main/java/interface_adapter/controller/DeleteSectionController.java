package interface_adapter.controller;

import usecase.deletesection.DeleteSectionInputBoundary;
import usecase.deletesection.DeleteSectionInputData;

/**
 * Controller for Delete Course use case.
 * Handles requests to remove a course section from the timetable.
 */
public class DeleteSectionController {
    
    private final DeleteSectionInputBoundary deleteSectionInteractor;
    
    public DeleteSectionController(DeleteSectionInputBoundary deleteSectionInteractor) {
        if (deleteSectionInteractor == null) {
            throw new IllegalArgumentException("Interactor cannot be null");
        }
        this.deleteSectionInteractor = deleteSectionInteractor;
    }
    
    /**
     * Handle delete course request from the view.
     */
    public void deleteSection(String courseCode, String sectionCode) {
        try {
            // Create input data from primitives
            DeleteSectionInputData inputData = new DeleteSectionInputData(courseCode, sectionCode);

            deleteSectionInteractor.execute(inputData);
            
        } catch (IllegalArgumentException e) {
            // Input validation failed
            throw e;
        } catch (Exception e) {
            // Unexpected error
            throw new RuntimeException("Failed to delete section", e);
        }
    }
}

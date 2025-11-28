package usecase.deletecourse;

import data_access.TimetableDataAccessInterface;
import entity.Section;

import java.util.List;

/**
 * Interactor for Delete Course use case.
 */
public class DeleteCourseInteractor implements DeleteCourseInputBoundary {
    
    private final TimetableDataAccessInterface timetableDataAccess;
    private final DeleteCourseOutputBoundary presenter;
    
    public DeleteCourseInteractor(TimetableDataAccessInterface timetableDataAccess,
                                 DeleteCourseOutputBoundary presenter) {
        if (timetableDataAccess == null) {
            throw new IllegalArgumentException("TimetableDataAccess cannot be null");
        }
        if (presenter == null) {
            throw new IllegalArgumentException("Presenter cannot be null");
        }
        
        this.timetableDataAccess = timetableDataAccess;
        this.presenter = presenter;
    }
    
    @Override
    public void execute(DeleteCourseInputData inputData) {
        try {
            String courseCode = inputData.getCourseCode();
            String sectionCode = inputData.getSectionCode();
            
            // Find the section to delete
            Section sectionToDelete = findSection(courseCode, sectionCode);
            
            if (sectionToDelete == null) {
                presenter.presentError("Section not found: " + courseCode + " " + sectionCode);
                return;
            }
            
            // Delete the section
            boolean deleted = timetableDataAccess.removeSection(sectionToDelete);
            
            if (!deleted) {
                presenter.presentError("Failed to delete section: " + courseCode + " " + sectionCode);
                return;
            }
            
            // Create output data with entities
            DeleteCourseOutputData outputData = new DeleteCourseOutputData(
                courseCode,
                sectionCode
            );
            
            // Present success
            presenter.presentSuccess(outputData);
            
        } catch (Exception e) {
            presenter.presentError("Error deleting section: " + e.getMessage());
        }
    }
    
    /**
     * Find a section by course code and section code.
     */
    private Section findSection(String courseCode, String sectionCode) {
        List<Section> allSections = timetableDataAccess.getAllSections();
        
        for (Section section : allSections) {
            if (section.getCourse().getCourseCode().equals(courseCode) &&
                section.getSectionId().equals(sectionCode)) {
                return section;
            }
        }
        
        return null;
    }
}

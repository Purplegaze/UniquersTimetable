package interface_adapter.deletesection;

import interface_adapter.ViewModel;

/**
 * ViewModel for the Delete Section use case.
 * Manages the state of section deletion from the timetable.
 */
public class DeleteSectionViewModel extends ViewModel {

    private String deletedCourseCode = "";
    private String deletedSectionCode = "";
    private String errorMessage = "";

    public DeleteSectionViewModel() {
        super("deleteSection");
    }

    public String getDeletedCourseCode() {
        return deletedCourseCode;
    }

    public String getDeletedSectionCode() {
        return deletedSectionCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set the section that was deleted.
     */
    public void setSectionDeleted(String courseCode, String sectionCode) {
        this.deletedCourseCode = courseCode;
        this.deletedSectionCode = sectionCode;
        firePropertyChanged("sectionDeleted");
    }

    /**
     * Set error message.
     */
    public void setError(String message) {
        this.errorMessage = message;
        firePropertyChanged("error");
    }
}
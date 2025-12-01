package usecase.deletesection;

/**
 * Output Data for Delete Course use case.
 */
public class DeleteSectionOutputData {

    private final String deletedCourseCode;
    private final String deletedSectionCode;

    public DeleteSectionOutputData(String deletedCourseCode,
                                   String deletedSectionCode) {
        this.deletedCourseCode = deletedCourseCode;
        this.deletedSectionCode = deletedSectionCode;
    }
    
    public String getDeletedCourseCode() {
        return deletedCourseCode;
    }
    
    public String getDeletedSectionCode() {
        return deletedSectionCode;
    }
}

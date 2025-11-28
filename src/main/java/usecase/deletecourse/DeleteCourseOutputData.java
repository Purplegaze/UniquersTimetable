package usecase.deletecourse;

/**
 * Output Data for Delete Course use case.
 */
public class DeleteCourseOutputData {

    private final String deletedCourseCode;
    private final String deletedSectionCode;

    public DeleteCourseOutputData(String deletedCourseCode,
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

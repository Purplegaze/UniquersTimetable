package usecase.deletesection;

/**
 * Input Data for Delete Course use case.
 */
public class DeleteSectionInputData {
    
    private final String courseCode;
    private final String sectionCode;
    
    public DeleteSectionInputData(String courseCode, String sectionCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty");
        }
        if (sectionCode == null || sectionCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Section code cannot be empty");
        }
        
        this.courseCode = courseCode;
        this.sectionCode = sectionCode;
    }
    
    public String getCourseCode() {
        return courseCode;
    }
    
    public String getSectionCode() {
        return sectionCode;
    }
}

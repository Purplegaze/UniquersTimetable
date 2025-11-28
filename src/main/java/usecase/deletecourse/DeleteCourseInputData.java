package usecase.deletecourse;

/**
 * Input Data for Delete Course use case.
 */
public class DeleteCourseInputData {
    
    private final String courseCode;
    private final String sectionCode;
    
    public DeleteCourseInputData(String courseCode, String sectionCode) {
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

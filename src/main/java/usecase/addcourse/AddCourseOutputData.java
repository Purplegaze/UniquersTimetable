package usecase.addcourse;

import java.util.List;

/**
 * Output data from the Add Course use case.
 */
public class AddCourseOutputData {
    private final AddCourseInputData inputData;
    private final boolean hasConflict;
    private final List<String> conflictingCourses;

    public AddCourseOutputData(AddCourseInputData inputData,
                               boolean hasConflict,
                               List<String> conflictingCourses) {
        this.inputData = inputData;
        this.hasConflict = hasConflict;
        this.conflictingCourses = conflictingCourses;
    }

    public String getCourseCode() { return inputData.getCourseCode(); }
    public String getSectionCode() { return inputData.getSectionCode(); }
    public String getDay() { return inputData.getDay(); }
    public int getStartHour() { return inputData.getStartHour(); }
    public int getEndHour() { return inputData.getEndHour(); }
    public String getLocation() { return inputData.getLocation(); }
    public boolean hasConflict() { return hasConflict; }
    public List<String> getConflictingCourses() { return conflictingCourses; }
}

package usecase.addcourse;

import interface_adapter.controller.AddCourseController.TimeSlotData;
import java.util.ArrayList;
import java.util.List;

/**
 * Input Data for Add Course use case.
 */
public class AddCourseInputData {

    private final String courseCode;
    private final String sectionCode;
    private final String instructor;
    private final List<TimeSlotData> timeSlotDataList;

    public AddCourseInputData(String courseCode, String sectionCode,
                              String instructor, List<TimeSlotData> timeSlotDataList) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty");
        }
        if (sectionCode == null || sectionCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Section code cannot be empty");
        }
        if (timeSlotDataList == null || timeSlotDataList.isEmpty()) {
            throw new IllegalArgumentException("Time slots cannot be empty");
        }

        this.courseCode = courseCode;
        this.sectionCode = sectionCode;
        this.instructor = instructor;
        this.timeSlotDataList = new ArrayList<>(timeSlotDataList);
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public String getInstructor() {
        return instructor;
    }

    public List<TimeSlotData> getTimeSlotDataList() {
        return new ArrayList<>(timeSlotDataList);
    }
}
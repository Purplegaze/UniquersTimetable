package interface_adapter.addcourse;

import usecase.addcourse.AddCourseInputBoundary;
import usecase.addcourse.AddCourseInputData;

import java.util.List;

/**
 * Controller for the Add Course use case.
 */
public class AddCourseController {

    private final AddCourseInputBoundary addCourseInteractor;

    public AddCourseController(AddCourseInputBoundary addCourseInteractor) {
        if (addCourseInteractor == null) {
            throw new IllegalArgumentException("Interactor cannot be null");
        }
        this.addCourseInteractor = addCourseInteractor;
    }

    /**
     * Handle add course request from the view.
     */
    public void addCourse(String courseCode, String sectionCode,
                          String instructor, List<TimeSlotData> timeDatas) {
        try {
            validateInput(courseCode, sectionCode, timeDatas);

            // Create input data with primitives
            AddCourseInputData inputData = new AddCourseInputData(
                    courseCode,
                    sectionCode,
                    instructor,
                    timeDatas
            );

            addCourseInteractor.execute(inputData);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to add course", e);
        }
    }

    private void validateInput(String courseCode, String sectionCode,
                               List<TimeSlotData> timeDatas) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty");
        }
        if (sectionCode == null || sectionCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Section code cannot be empty");
        }
        if (timeDatas == null || timeDatas.isEmpty()) {
            throw new IllegalArgumentException("Time slots cannot be empty");
        }
    }

    /**
     * Data class for time slot information from the view.
     */
    public static class TimeSlotData {
        private final String day;
        private final int startHour;
        private final int endHour;
        private final String location;

        public TimeSlotData(String day, int startHour, int endHour, String location) {
            this.day = day;
            this.startHour = startHour;
            this.endHour = endHour;
            this.location = location;
        }

        public String getDay() { return day; }
        public int getStartHour() { return startHour; }
        public int getEndHour() { return endHour; }
        public String getLocation() { return location; }
    }
}
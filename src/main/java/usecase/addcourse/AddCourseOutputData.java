package usecase.addcourse;

import java.util.List;

/**
 * Output data from the Add Course use case.
 */
public class AddCourseOutputData {

    // Nested class for time slot data (primitives only)
    public static class TimeSlotData {
        private final String dayName;
        private final int startHour;
        private final int endHour;
        private final String location;

        public TimeSlotData(String dayName, int startHour, int endHour, String location) {
            this.dayName = dayName;
            this.startHour = startHour;
            this.endHour = endHour;
            this.location = location;
        }

        public String getDayName() {
            return dayName;
        }

        public int getStartHour() {
            return startHour;
        }

        public int getEndHour() {
            return endHour;
        }

        public String getLocation() {
            return location;
        }
    }

    private final String courseCode;
    private final String sectionCode;
    private final String instructor;
    private final List<TimeSlotData> timeSlots;
    private final boolean hasConflict;

    public AddCourseOutputData(String courseCode, String sectionCode,
                               String instructor, List<TimeSlotData> timeSlots,
                               boolean hasConflict) {
        if (courseCode == null || sectionCode == null) {
            throw new IllegalArgumentException("Course code and section code cannot be null");
        }
        this.courseCode = courseCode;
        this.sectionCode = sectionCode;
        this.instructor = instructor;
        this.timeSlots = timeSlots;
        this.hasConflict = hasConflict;
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

    public List<TimeSlotData> getTimeSlots() {
        return timeSlots;
    }

    public boolean hasConflict() {
        return hasConflict;
    }
}
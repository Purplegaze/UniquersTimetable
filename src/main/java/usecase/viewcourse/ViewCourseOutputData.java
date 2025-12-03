package usecase.viewcourse;

import java.util.List;

public class ViewCourseOutputData {
    private final String courseCode;
    private final String courseName;
    private final String term;
    private final Float recommendation;
    private final Float workload;
    private final List<SectionData> sections;

    public ViewCourseOutputData(String courseCode, String courseName, String term,
                                Float recommendation, Float workload, List<SectionData> sections) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.term = term;
        this.recommendation = recommendation;
        this.workload = workload;
        this.sections = sections;
    }

    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public String getTerm() { return term; }
    public Float getRecommendation() { return recommendation; }
    public Float getWorkload() { return workload; }
    public List<SectionData> getSections() { return sections; }

    public static class SectionData {
        private final String sectionId;
        private final List<String> instructors;
        private final int enrolled;
        private final int capacity;
        private final boolean isFull;
        private final List<TimeSlotData> timeSlots;

        public SectionData(String sectionId, List<String> instructors, int enrolled,
                           int capacity, boolean isFull, List<TimeSlotData> timeSlots) {
            this.sectionId = sectionId;
            this.instructors = instructors;
            this.enrolled = enrolled;
            this.capacity = capacity;
            this.isFull = isFull;
            this.timeSlots = timeSlots;
        }

        public String getSectionId() { return sectionId; }
        public List<String> getInstructors() { return instructors; }
        public int getEnrolled() { return enrolled; }
        public int getCapacity() { return capacity; }
        public boolean isFull() { return isFull; }
        public List<TimeSlotData> getTimeSlots() { return timeSlots; }
    }

    public static class TimeSlotData {
        private final String day;
        private final int start;
        private final int end;
        private final String location;

        public TimeSlotData(String day, int start, int end, String location) {
            this.day = day;
            this.start = start;
            this.end = end;
            this.location = location;
        }

        public String getDay() { return day; }
        public int getStart() { return start; }
        public int getEnd() { return end; }
        public String getLocation() { return location; }
    }
}
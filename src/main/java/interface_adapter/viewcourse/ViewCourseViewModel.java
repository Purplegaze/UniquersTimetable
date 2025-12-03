package interface_adapter.viewcourse;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class ViewCourseViewModel {
    // 1. Store primitives
    private String courseCode;
    private String courseName;
    private String term;
    private Float recommendation;
    private Float workload;
    private List<SectionViewModel> sectionViewModels;

    private String error;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void setCourseData(String courseCode, String courseName, String term,
                              Float recommendation, Float workload,
                              List<SectionViewModel> sectionViewModels) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.term = term;
        this.recommendation = recommendation;
        this.workload = workload;
        this.sectionViewModels = sectionViewModels;
        this.error = null;
        support.firePropertyChange("state", null, this);
    }

    public void setError(String error) {
        this.error = error;
        support.firePropertyChange("error", null, error);
    }

    public String getError() {
        return error;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    // 3. Update getters to return the stored primitive fields
    public String getCourseCode() {
        return courseCode != null ? courseCode : "";
    }

    public String getCourseName() {
        return courseName != null ? courseName : "";
    }

    public String getTerm() {
        return term != null ? term : "";
    }

    public boolean hasRating() {
        return recommendation != null || workload != null;
    }

    public Float getRecommendation() {
        return recommendation;
    }

    public Float getWorkload() {
        return workload;
    }

    public List<SectionViewModel> getSectionViewModels() {
        return sectionViewModels != null ? sectionViewModels : new ArrayList<>();
    }

    // 4. Refactor Inner Class: SectionViewModel
    public static class SectionViewModel {
        private final String sectionId;
        private final List<String> instructors;
        private final int enrolledStudents;
        private final int capacity;
        private final boolean isFull;
        private final String courseCode;
        private final List<TimeSlotViewModel> timeSlots;

        public SectionViewModel(String sectionId, List<String> instructors, int enrolledStudents,
                                int capacity, boolean isFull, String courseCode,
                                List<TimeSlotViewModel> timeSlots) {
            this.sectionId = sectionId;
            this.instructors = instructors;
            this.enrolledStudents = enrolledStudents;
            this.capacity = capacity;
            this.isFull = isFull;
            this.courseCode = courseCode;
            this.timeSlots = timeSlots;
        }

        public String getSectionId() { return sectionId; }
        public List<String> getInstructors() { return instructors; }
        public int getEnrolledStudents() { return enrolledStudents; }
        public int getCapacity() { return capacity; }
        public boolean isFull() { return isFull; }
        public List<TimeSlotViewModel> getTimeSlotViewModels() { return timeSlots; }
        public String getCourseCode() { return courseCode; }
    }

    // 5. Refactor Inner Class: TimeSlotViewModel
    public static class TimeSlotViewModel {
        private final String dayName;
        private final int startHour;
        private final int endHour;
        private final String location;

        public TimeSlotViewModel(String dayName, int startHour, int endHour, String location) {
            this.dayName = dayName;
            this.startHour = startHour;
            this.endHour = endHour;
            this.location = location;
        }

        public String getDayName() { return dayName; }
        public int getStartHour() { return startHour; }
        public int getEndHour() { return endHour; }
        public String getLocation() { return location; }
    }
}
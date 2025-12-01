package interface_adapter.viewcourse;

import entity.Course;
import entity.Section;
import entity.TimeSlot;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class ViewCourseViewModel {
    private Course currentCourse;
    private String error;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void setCourse(Course course) {
        this.currentCourse = course;
        this.error = null;
        support.firePropertyChange("course", null, course);
    }

    public void setError(String error) {
        this.error = error;
        support.firePropertyChange("error", null, error);
    }

    public Course getCourse() {
        return currentCourse;
    }

    public String getError() {
        return error;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public String getCourseCode() {
        return currentCourse != null ? currentCourse.getCourseCode() : "";
    }

    public String getCourseName() {
        return currentCourse != null ? currentCourse.getCourseName() : "";
    }

    public String getTerm() {
        return currentCourse != null ? currentCourse.getTerm() : "";
    }

    public boolean hasRating() {
        return currentCourse != null && currentCourse.getCourseRating() != null;
    }

    public Float getRecommendation() {
        if (currentCourse != null && currentCourse.getCourseRating() != null) {
            return currentCourse.getCourseRating().getRating("Recommendation");
        }
        return null;
    }

    public Float getWorkload() {
        if (currentCourse != null && currentCourse.getCourseRating() != null) {
            return currentCourse.getCourseRating().getRating("Workload");
        }
        return null;
    }

    public List<SectionViewModel> getSectionViewModels() {
        if (currentCourse == null) {
            return new ArrayList<>();
        }

        List<SectionViewModel> result = new ArrayList<>();
        for (Section section : currentCourse.getSections()) {
            result.add(new SectionViewModel(section));
        }
        return result;
    }

    public static class SectionViewModel {
        private final Section section;

        public SectionViewModel(Section section) {
            this.section = section;
        }

        public String getSectionId() {
            return section.getSectionId();
        }

        public List<String> getInstructors() {
            return new ArrayList<>(section.getInstructors());
        }

        public int getEnrolledStudents() {
            return section.getEnrolledStudents();
        }

        public int getCapacity() {
            return section.getCapacity();
        }

        public boolean isFull() {
            return section.isFull();
        }

        public List<TimeSlotViewModel> getTimeSlotViewModels() {
            List<TimeSlotViewModel> result = new ArrayList<>();
            for (TimeSlot ts : section.getTimes()) {
                result.add(new TimeSlotViewModel(ts));
            }
            return result;
        }

        // 用于 Controller
        public String getCourseCode() {
            return section.getCourse() != null ? section.getCourse().getCourseCode() : "";
        }
    }

    public static class TimeSlotViewModel {
        private final TimeSlot timeSlot;

        public TimeSlotViewModel(TimeSlot timeSlot) {
            this.timeSlot = timeSlot;
        }

        public String getDayName() {
            return switch(timeSlot.getDayOfWeek()) {
                case 1 -> "Monday";
                case 2 -> "Tuesday";
                case 3 -> "Wednesday";
                case 4 -> "Thursday";
                case 5 -> "Friday";
                case 6 -> "Saturday";
                case 7 -> "Sunday";
                default -> "Unknown";
            };
        }

        public int getStartHour() {
            return timeSlot.getStartTime().getHour();
        }

        public int getEndHour() {
            return timeSlot.getEndTime().getHour();
        }

        public String getLocation() {
            return timeSlot.getBuilding() != null ?
                    timeSlot.getBuilding().getBuildingCode() : "TBD";
        }
    }
}
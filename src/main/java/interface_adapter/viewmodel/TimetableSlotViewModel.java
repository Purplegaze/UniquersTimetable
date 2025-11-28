package interface_adapter.viewmodel;

import java.awt.Color;

/**
 * View Model for displaying a course on the timetable.
 */
public class TimetableSlotViewModel {
    private final String courseCode;
    private final String sectionCode;
    private final String location;
    private final String dayName;
    private final int startHour;
    private final int endHour;
    private final Color color;
    private final boolean hasConflict;

    private TimetableSlotViewModel(Builder builder) {
        this.courseCode = builder.courseCode;
        this.sectionCode = builder.sectionCode;
        this.location = builder.location;
        this.dayName = builder.dayName;
        this.startHour = builder.startHour;
        this.endHour = builder.endHour;
        this.color = builder.color;
        this.hasConflict = builder.hasConflict;
    }

    public String getCourseCode() { return courseCode; }
    public String getSectionCode() { return sectionCode; }
    public String getLocation() { return location; }
    public String getDayName() { return dayName; }
    public int getStartHour() { return startHour; }
    public int getEndHour() { return endHour; }
    public Color getColor() { return color; }
    public boolean hasConflict() { return hasConflict; }
    public String getDisplayText() {
        return courseCode + " " + sectionCode;
    }

    public static class Builder {
        private String courseCode;
        private String sectionCode;
        private String location;
        private String dayName;
        private int startHour;
        private int endHour;
        private Color color;
        private boolean hasConflict;

        public Builder courseCode(String courseCode) {
            this.courseCode = courseCode;
            return this;
        }

        public Builder sectionCode(String sectionCode) {
            this.sectionCode = sectionCode;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder dayName(String dayName) {
            this.dayName = dayName;
            return this;
        }

        public Builder startHour(int startHour) {
            this.startHour = startHour;
            return this;
        }

        public Builder endHour(int endHour) {
            this.endHour = endHour;
            return this;
        }

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder hasConflict(boolean hasConflict) {
            this.hasConflict = hasConflict;
            return this;
        }

        public TimetableSlotViewModel build() {
            return new TimetableSlotViewModel(this);
        }
    }
}

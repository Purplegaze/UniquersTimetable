package data_access;

import java.util.List;

/**
 * Data access interface for Timetable operations.
 * Stores the current working timetable.
 */
public interface TimetableDataAccessInterface {

    /**
     * Add a section to the timetable.
     */
    void addSection(String courseCode, String sectionCode, String day,
                    int startHour, int endHour, String location);

    /**
     * Remove a section from the timetable.
     */
    void removeSection(String courseCode, String sectionCode);

    /**
     * Find conflicts at a given time slot.
     * @return List of conflicting course-section codes (e.g., ["CSC207-LEC0101"])
     */
    List<String> findConflicts(String day, int startHour, int endHour);

    /**
     * Check if a section is already in the timetable.
     */
    boolean hasSection(String courseCode, String sectionCode);

    /**
     * Check if a specific time slot for a section is already in the timetable.
     */
    boolean hasSectionAtTime(String courseCode, String sectionCode, String day,
                             int startHour, int endHour);

    /**
     * Get all sections in the timetable.
     */
    List<TimetableEntry> getAllSections();

    /**
     * Clear the timetable.
     */
    void clear();

    /**
     * Represents an entry in the timetable.
     */
    class TimetableEntry {
        private final String courseCode;
        private final String sectionCode;
        private final String day;
        private final int startHour;
        private final int endHour;
        private final String location;

        public TimetableEntry(String courseCode, String sectionCode, String day,
                              int startHour, int endHour, String location) {
            this.courseCode = courseCode;
            this.sectionCode = sectionCode;
            this.day = day;
            this.startHour = startHour;
            this.endHour = endHour;
            this.location = location;
        }

        public String getCourseCode() { return courseCode; }
        public String getSectionCode() { return sectionCode; }
        public String getDay() { return day; }
        public int getStartHour() { return startHour; }
        public int getEndHour() { return endHour; }
        public String getLocation() { return location; }
    }
}
package data_access;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory implementation of TimetableDataAccessInterface.
 * Stores the current working timetable in memory.
 */
public class InMemoryTimetableDataAccess implements TimetableDataAccessInterface {

    private final List<TimetableEntry> entries;

    public InMemoryTimetableDataAccess() {
        this.entries = new ArrayList<>();
    }

    @Override
    public void addSection(String courseCode, String sectionCode, String day,
                           int startHour, int endHour, String location) {
        // Don't add duplicates - check at time-slot level, not section level
        // This allows a section with multiple time slots (e.g., Mon and Wed) to be added
        if (!hasSectionAtTime(courseCode, sectionCode, day, startHour, endHour)) {
            entries.add(new TimetableEntry(courseCode, sectionCode, day, startHour, endHour, location));
        }
    }

    @Override
    public void removeSection(String courseCode, String sectionCode) {
        entries.removeIf(entry ->
                entry.getCourseCode().equals(courseCode) &&
                        entry.getSectionCode().equals(sectionCode)
        );
    }

    @Override
    public List<String> findConflicts(String day, int startHour, int endHour) {
        List<String> conflicts = new ArrayList<>();

        for (TimetableEntry entry : entries) {
            // Check if same day and times overlap
            if (entry.getDay().equals(day)) {
                boolean overlaps = !(endHour <= entry.getStartHour() || startHour >= entry.getEndHour());
                if (overlaps) {
                    conflicts.add(entry.getCourseCode() + "-" + entry.getSectionCode());
                }
            }
        }

        return conflicts;
    }

    @Override
    public boolean hasSection(String courseCode, String sectionCode) {
        for (TimetableEntry entry : entries) {
            if (entry.getCourseCode().equals(courseCode) &&
                    entry.getSectionCode().equals(sectionCode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasSectionAtTime(String courseCode, String sectionCode, String day,
                                    int startHour, int endHour) {
        for (TimetableEntry entry : entries) {
            if (entry.getCourseCode().equals(courseCode) &&
                    entry.getSectionCode().equals(sectionCode) &&
                    entry.getDay().equals(day) &&
                    entry.getStartHour() == startHour &&
                    entry.getEndHour() == endHour) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<TimetableEntry> getAllSections() {
        return new ArrayList<>(entries);
    }

    @Override
    public void clear() {
        entries.clear();
    }
}
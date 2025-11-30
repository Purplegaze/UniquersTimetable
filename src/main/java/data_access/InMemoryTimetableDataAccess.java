package data_access;

import entity.*;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory implementation of TimetableDataAccessInterface.
 * Stores the current working timetable in memory.
 */
public class InMemoryTimetableDataAccess implements TimetableDataAccessInterface {

    private final Timetable timetable;

    public InMemoryTimetableDataAccess() {
        this.timetable = new Timetable();
    }

    @Override
    public boolean addSection(Section section) {
        if (section == null) {
            throw new IllegalArgumentException("Section cannot be null");
        }

        if (hasSection(section)) {
            return false;
        }

        timetable.addSectionOfNewCourse(section);

        return true;
    }

    @Override
    public boolean removeSection(Section section) {
        if (section == null) {
            throw new IllegalArgumentException("Section cannot be null");
        }

        if (!hasSection(section)) {
            return false;
        }

        timetable.removeSection(section);
        return true;
    }

    @Override
    public boolean hasConflicts(Section section) {
        if (section == null) {
            throw new IllegalArgumentException("Section cannot be null");
        }

        List<TimeSlot> sectionTimes = section.getTimes();

        // Check each existing section for conflicts
        for (Section existingSection : getAllSections()) {
            if (existingSection.equals(section)) {
                continue;
            }

            for (TimeSlot newTime : sectionTimes) {
                for (TimeSlot existingTime : existingSection.getTimes()) {
                    if (newTime.overlapsWith(existingTime)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean hasSection(Section section) {
        if (section == null) {
            return false;
        }

        for (Section existingSection : getAllSections()) {
            if (existingSection.equals(section)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Section> getAllSections() {
        List<Section> sections = new ArrayList<>();

        // Extract sections from timetable blocks
        for (entity.TimetableBlock block : timetable.getBlocks()) {
            Section section = block.getSection();
            if (!sections.contains(section)) {
                sections.add(section);
            }
        }

        return sections;
    }

    @Override
    public Timetable getTimetable() {
        return timetable;
    }

    @Override
    public void clear() {
        List<Course> coursesToRemove = new ArrayList<>(timetable.getCourses());
        for (Course course : coursesToRemove) {
            timetable.removeCourse(course);
        }
    }

    @Override
    public String getCurrentTerm() {
        List<Course> courses = timetable.getCourses();

        if (courses.isEmpty()) {
            return null;
        }

        // Get term from first non-Y course
        for (Course course : courses) {
            String term = course.getTerm();
            if (!"Y".equals(term)) {
                return term;
            }
        }

        // If only Y courses, return null (can add either F or S)
        return null;
    }
}
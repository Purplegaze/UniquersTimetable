package data_access;

import entity.Section;
import entity.Timetable;

import java.util.List;

/**
 * Data access interface for Timetable operations.
 * Stores the current working timetable.
 */
public interface TimetableDataAccessInterface {

    /**
     * Add a section to the timetable.
     */
    boolean addSection(Section section);

    /**
     * Remove a section from the timetable.
     */
    boolean removeSection(Section section);

    /**
     * Check if a section is already in the timetable.
     */
    boolean hasSection(Section section);

    /**
     * Get all sections in the timetable.
     */
    List<Section> getAllSections();

    /**
     * Get the timetable entity.
     */
    Timetable getTimetable();

    /**
     * Clear the timetable.
     */
    void clear();

    /**
     * Get the current term of the timetable (F, S, or null if empty/all Y courses).
     */
    String getCurrentTerm();

    /**
     * Check if a section has any time conflicts with existing sections.
     */
    boolean hasConflicts(Section section);
}
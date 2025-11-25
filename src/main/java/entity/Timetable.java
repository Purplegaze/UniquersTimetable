package entity;

import java.util.ArrayList;
import java.util.List;

/**
 * NOTE: Conflicts are currently unsupported due to heavy implementation complexity.
 * As a result, the current state of the program prevents conflicts from being added.
 *
 */

public class Timetable {
    private ArrayList<Course> courses;
    private ArrayList<TimetableBlock> blocks;
    private ArrayList<Conflict> conflicts;

    public Timetable() {
        courses = new ArrayList<>();
        blocks = new ArrayList<>();
        conflicts = new ArrayList<>(); // Conflicts are currently unsupported.
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public ArrayList<TimetableBlock> getBlocks() {
        return blocks;
    }

    public void addSectionOfNewCourse(Section section) {
        List<TimeSlot> sectionTimes = section.getTimes();

        // Prevent a section from being added if it conflicts with an existing section.
        if (wouldConflict(sectionTimes)) {
            return;
        } // TODO: Add custom exception for conflict.

        courses.add(section.getCourse());

        addSection(section);
    }

    private boolean wouldConflict(List<TimeSlot> sectionTimes) {
        for (TimeSlot newTimeSlot : sectionTimes) {
            for (TimetableBlock block : this.blocks) {
                TimeSlot existingTimeSlot = block.getTimeSlot();
                if (newTimeSlot.overlapsWith(existingTimeSlot)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addSection(Section section) {
        for (TimeSlot timeSlot : section.getTimes()) {
            TimetableBlock newBlock = new TimetableBlock(section, timeSlot);
            blocks.add(newBlock);
            for (TimetableBlock existingBlock : blocks) {
                if (!existingBlock.equals(newBlock)) { // iterate through all other blocks currently on timetable
                    TimeSlot existingTimeSlot = existingBlock.getTimeSlot();
                    if (existingTimeSlot.immediatelyPrecedes(timeSlot)) {
                        // Existing --> New

                        // Populate nextTime/nextCourse/prevTime/prevCourse fields of newBlock and existingBlock
                        existingBlock.setNextCourse(newBlock);
                        newBlock.setPrevCourse(existingBlock);
                        Building existingBuilding = existingTimeSlot.getBuilding();
                        Building newBuilding = timeSlot.getBuilding();
                        existingBlock.setNextTime(existingBuilding.getTimeTo(newBuilding));
                        newBlock.setPrevTime(existingBuilding.getTimeTo(newBuilding));
                    }
                    else if (existingTimeSlot.immediatelyFollows(timeSlot)) {
                        // New --> Existing

                        // Populate nextTime/nextCourse/prevTime/prevCourse fields of newBlock and existingBlock
                        existingBlock.setPrevCourse(newBlock);
                        newBlock.setNextCourse(existingBlock);
                        Building existingBuilding = existingTimeSlot.getBuilding();
                        Building newBuilding = timeSlot.getBuilding();
                        existingBlock.setPrevTime(newBuilding.getTimeTo(existingBuilding));
                        newBlock.setNextTime(newBuilding.getTimeTo(existingBuilding));
                    }
                }
            }
        }
    }

    private void removeBlock(TimetableBlock block) {
        if (!blocks.contains(block)) {
            return;
        }
        TimeSlot timeSlot = block.getTimeSlot();

        // Find other blocks the removed block is back-to-back with, and remove the applicable next/prev fields
        for (TimetableBlock existingBlock : blocks) {
            TimeSlot existingTimeSlot = existingBlock.getTimeSlot();
            if (existingTimeSlot.immediatelyPrecedes(timeSlot)) {
                // Existing --> [Removed]
                // NOTE: This assumes conflicts are not implemented. Needs extra implementation if they are.
                existingBlock.setNextCourse(null);
                existingBlock.setNextTime(-1);
            } else if (existingTimeSlot.immediatelyFollows(timeSlot)) {
                // [Removed] --> Existing
                // NOTE: This assumes conflicts are not implemented. Needs extra implementation if they are.
                existingBlock.setPrevCourse(null);
                existingBlock.setPrevTime(-1);
            }
        }

        blocks.remove(block);

    }

    public void changeSectionOfExistingCourse(Section section) {
        Course sectionCourse = section.getCourse();

        // create a new Timetable object to check conflicts ignoring the existing section
        Timetable timetableWithoutExistingSection = new Timetable();
        timetableWithoutExistingSection.courses = new ArrayList<Course>(courses);
        timetableWithoutExistingSection.blocks = new ArrayList<TimetableBlock>(blocks);
        timetableWithoutExistingSection.conflicts = new ArrayList<Conflict>(conflicts);
        timetableWithoutExistingSection.removeCourse(sectionCourse);

        List<TimeSlot> sectionTimes = section.getTimes();
        if (timetableWithoutExistingSection.wouldConflict(sectionTimes)) {
            return; // TODO: Add custom exception for conflict
        }

        removeCourse(sectionCourse);
        addSectionOfNewCourse(section);
    }

    public void removeCourse(Course course) {
        ArrayList<TimetableBlock> toRemove = new ArrayList<>();
        for (TimetableBlock tb : blocks) {
            if (tb.getCourse().equals(course)) {
                toRemove.add(tb);
            }
        }
        for (TimetableBlock tb : toRemove) {
            removeBlock(tb);
        }
    }

    public Conflict warnBackToBack(Section newSection, int minMinutes) {

        for (TimeSlot newSlot : newSection.getTimes()) {

            for (TimetableBlock existing : blocks) {
                TimeSlot existingSlot = existing.getTimeSlot();

                boolean backToBack =
                        existingSlot.immediatelyPrecedes(newSlot) ||
                                existingSlot.immediatelyFollows(newSlot);

                if (backToBack) {

                    Building from = existingSlot.getBuilding();
                    Building to = newSlot.getBuilding();

                    int travel = from.getTimeTo(to);

                    // TODO: Temp until building API works
                    if (travel == -1) {
                        travel = 15;   // TODO: assumes 15 min walk if building dont match
                    }

                    if (travel > minMinutes) {
                        return new Conflict(
                                List.of(existing.getCourse(), newSection.getCourse()),
                                "BACK_TO_BACK",
                                "Not enough time to walk between these classes"
                        );
                    }
                }
            }
        }

        return null; // No issues
    }

    public ArrayList getTravelTimes() {
        // TODO: Decide in what form a list of travel times should be returned.
        return new ArrayList();
    }
}
package entity;

import java.util.ArrayList;

public class Timetable {
    private ArrayList<Course> courses;
    private ArrayList<TimetableBlock> blocks;
    private ArrayList<Conflict> conflicts;

    public Timetable() {
        courses = new ArrayList<>();
        blocks = new ArrayList<>();
        conflicts = new ArrayList<>();
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public ArrayList<TimetableBlock> getBlocks() {
        return blocks;
    }

    public void addSectionOfNewCourse(Section section) {
        // TODO: Prevent conflicts from being added.

        courses.add(section.getCourse());

        addSection(section);
    }

    private void addSection(Section section) {
        for (TimeSlot time : section.getTimes()) {
            TimetableBlock tb = new TimetableBlock(section, time);
            blocks.add(tb);
        }
    }

    public void changeSectionOfExistingCourse(Section section) {
        for (TimetableBlock tb : blocks) {
            if (tb.getSection().equals(section)) {
                blocks.remove(tb);
            }
        }
        addSection(section);

    }

    public void removeCourse(Course course) {
        for (TimetableBlock tb : blocks) {
            if (tb.getCourse().equals(course)) {
                blocks.remove(tb);
            }
        }
    }

    public ArrayList getTravelTimes() {
        // TODO: Figure out where travel times are to be calculated.
        return new ArrayList();
    }
}
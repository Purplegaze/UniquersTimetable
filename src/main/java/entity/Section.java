package entity;

import java.util.ArrayList;
import java.util.List;

public class Section {
    private final String section_id;
    private final List<TimeSlot> times;
    private final int enrolled_students;
    private final List<String> instructors;
    private final int capacity;
    private final Course course;

    public Section(String section_id, List<TimeSlot> times, int enrolled_students,
                   List<String> instructors, int capacity, Course course) {
        this.section_id = section_id;
        this.times = new ArrayList<>(times);
        this.enrolled_students = enrolled_students;
        this.instructors = new ArrayList<>(instructors);
        this.capacity = capacity;
        this.course = course;
    }

    /**
     * Gets the unique section identifier.
     *
     * @return the section ID
     */
    public String getSectionId() {
        return section_id;
    }

    /**
     * Gets the time slots when this section meets.
     *
     * @return a copy of the list of time slots
     */
    public List<TimeSlot> getTimes() {
        return new ArrayList<>(times);
    }

    /**
     * Gets the number of currently enrolled students.
     *
     * @return the number of enrolled students
     */
    public int getEnrolledStudents() {
        return enrolled_students;
    }

    /**
     * Gets the list of instructors teaching this section.
     *
     * @return a copy of the list of instructor names
     */
    public List<String> getInstructors() {
        return new ArrayList<>(instructors);
    }

    /**
     * Gets the maximum capacity of this section.
     *
     * @return the maximum number of students
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Gets the course this section belongs to.
     *
     * @return the parent course
     */
    public Course getCourse() {
        return course;
    }

    /**
     * Checks if this section is full.
     *
     * @return true if enrolled students equals or exceeds capacity
     */
    public boolean isFull() {
        return enrolled_students >= capacity;
    }

    /**
     * Gets the number of available spots in this section.
     *
     * @return the number of spots remaining
     */
    public int getAvailableSpots() {
        return Math.max(0, capacity - enrolled_students);
    }

    /**
     * Checks if this section has any time conflicts with another section.
     *
     * @param other the other section to check against
     * @return true if there is any time overlap
     */
    public boolean conflictsWith(Section other) {
        for (TimeSlot thisSlot : this.times) {
            for (TimeSlot otherSlot : other.getTimes()) {
                if (thisSlot.overlapsWith(otherSlot)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Section{" +
                "section_id='" + section_id + '\'' +
                ", course=" + (course != null ? course.getCourseCode() : "null") +
                ", instructors=" + instructors +
                ", enrolled=" + enrolled_students + "/" + capacity +
                ", times=" + times +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return section_id.equals(section.section_id);
    }

    @Override
    public int hashCode() {
        return section_id.hashCode();
    }
}
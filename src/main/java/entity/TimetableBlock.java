package entity;

public class TimetableBlock {
    private Timetable timetable;
    private Section section;
    private Course course;
    private TimeSlot timeSlot;

    private TimetableBlock prevCourse;
    private int prevTime;
    private TimetableBlock nextCourse;
    private int nextTime;
    private int time;

    public TimetableBlock(Section section, TimeSlot timeSlot) {
        this.timetable = null;
        this.section = section;
        this.course = section.getCourse();
        this.timeSlot = timeSlot;
        this.prevCourse = null;
        this.prevTime = -1;
        this.nextCourse = null;
        this.nextTime = -1;
    }

    public Section getSection() {
        return section;
    }

    public Course getCourse() {
        return course;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public TimetableBlock getPrevCourse() {
        return prevCourse;
    }

    public int getPrevTime() {
        return prevTime;
    }

    public TimetableBlock getNextCourse() {
        return nextCourse;
    }

    public int getNextTime() {
        return nextTime;
    }

    public void setNextCourse(TimetableBlock nextCourse) {
        this.nextCourse = nextCourse;
    }

    public void setNextTime(int nextTime) {
        this.nextTime = nextTime;
    }

    public void setPrevCourse(TimetableBlock prevCourse) {
        this.prevCourse = prevCourse;
    }

    public void setPrevTime(int prevTime) {
        this.prevTime = prevTime;
    }
}

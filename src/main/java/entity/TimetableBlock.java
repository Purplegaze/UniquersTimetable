class TimetableBlock {
    private Timetable timetable;
    private Section section;
    private Course course;
    private TimeSlot timeSlot;

    private TimetableBlock prevCourse;
    private int prevTime;
    private TimetableBlock nextCourse;
    private int nextTime;

    public TimetableCourse(Section section, TimeSlot timeSlot) {
        this.timetable = null;
        this.section = section;
        this.course = section.course;
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
}

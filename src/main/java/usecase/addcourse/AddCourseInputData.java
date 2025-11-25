package usecase.addcourse;

/**
 * Input data for the Add Course use case.
 */
public class AddCourseInputData {
    private final String courseCode;
    private final String sectionCode;
    private final String day;
    private final int startHour;
    private final int endHour;
    private final String location;
    private final String term;

    public AddCourseInputData(String courseCode, String sectionCode, String day,
                              int startHour, int endHour, String location, String term) {
        this.courseCode = courseCode;
        this.sectionCode = sectionCode;
        this.day = day;
        this.startHour = startHour;
        this.endHour = endHour;
        this.location = location;
        this.term = term;
    }

    public String getCourseCode() { return courseCode; }
    public String getSectionCode() { return sectionCode; }
    public String getDay() { return day; }
    public int getStartHour() { return startHour; }
    public int getEndHour() { return endHour; }
    public String getLocation() { return location; }
    public String getTerm() { return term; }
}

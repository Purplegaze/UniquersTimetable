package entity;

import java.util.List;
import java.util.Map;

public class Course {
    private String courseCode;
    private String courseName;
    private String description;
    private float credits;
    private Rating courseRating;
    private String term;
    private List<Section> sections;
    private Building location;
    private int breadthCategory;

    public Course(String courseCode, String courseName, String description,
                  float credits, Rating courseRating, String term,
                  List<Section> sections, Building location, int breadthCategory) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.description = description;
        this.credits = credits;
        this.courseRating = courseRating;
        this.term = term;
        this.sections = sections;
        this.location = location;
        this.breadthCategory = breadthCategory;
    }


    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void removeSection(Section section) {
        sections.remove(section);
    }

    public Rating getCourseRating() {
        return courseRating;
    }

    public float getAvgRating() {
        return courseRating == null ? 0f : courseRating.getAvgRating();
    }

    public String getTerm() { return term; }
}
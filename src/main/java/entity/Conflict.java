package entity;

import java.util.List;

public class Conflict {

    // Fields
    private List<Course> courses;
    private String type;
    private String details;

    // Constructor
    public Conflict(List<Course> courses, String type, String details) {
        this.courses = courses;
        this.type = type;
        this.details = details;
    }
    public List<Course> getCourses() {
        return courses;
    }
    public String getType() {
        return type;
    }
    public String getDetails() {
        return details;
    }
    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setDetails(String details) {
        this.details = details;
    }}
package interface_adapter.viewmodel;

import entity.Course;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ViewCourseViewModel {
    private Course currentCourse;
    private String error;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void setCourse(Course course) {
        this.currentCourse = course;
        this.error = null;
        support.firePropertyChange("course", null, course);
    }

    public void setError(String error) {
        this.error = error;
        support.firePropertyChange("error", null, error);
    }

    public Course getCourse() {
        return currentCourse;
    }

    public String getError() {
        return error;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }
}
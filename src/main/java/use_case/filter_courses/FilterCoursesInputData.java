package use_case.filter_courses;

import entity.Course;
import java.util.List;

public class FilterCoursesInputData {
    private final String query;
    private final Integer breadth;

    public FilterCoursesInputData(String query, Integer breadth) {
        this.query = query;
        this.breadth = breadth;
    }

    public String getQuery() {
        return query;
    }

    public Integer getBreadth() {
        return breadth;
    }
}

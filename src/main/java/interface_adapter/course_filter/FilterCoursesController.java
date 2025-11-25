package interface_adapter.course_filter;

import entity.Course;
import use_case.filter_courses.FilterCoursesInputBoundary;
import use_case.filter_courses.FilterCoursesInputData;

import java.util.List;

public class FilterCoursesController {

    private final FilterCoursesInputBoundary interactor;

    public FilterCoursesController(FilterCoursesInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(List<Course> allCourses, Integer breadth) {
        FilterCoursesInputData data =
                new FilterCoursesInputData(allCourses, breadth);
        interactor.filter(data);
    }
}
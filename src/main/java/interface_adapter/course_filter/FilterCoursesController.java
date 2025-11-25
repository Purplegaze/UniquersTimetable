package interface_adapter.course_filter;

import use_case.filter_courses.FilterCoursesInputBoundary;
import use_case.filter_courses.FilterCoursesInputData;

public class FilterCoursesController {
    private final FilterCoursesInputBoundary interactor;

    public FilterCoursesController(FilterCoursesInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void filter(String query, Integer breadth) {
        FilterCoursesInputData data = new FilterCoursesInputData(query, breadth);
        interactor.execute(data);
    }
}

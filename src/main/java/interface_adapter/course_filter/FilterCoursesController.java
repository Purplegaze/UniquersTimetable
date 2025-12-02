package interface_adapter.filter_courses;

import usecase.filter_courses.FilterCoursesInputBoundary;
import usecase.filter_courses.FilterCoursesInputData;

public class FilterCoursesController {

    private final FilterCoursesInputBoundary interactor;

    public FilterCoursesController(FilterCoursesInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String query, Integer breadth) {
        FilterCoursesInputData data = new FilterCoursesInputData(query, breadth);
        interactor.execute(data);
    }
}
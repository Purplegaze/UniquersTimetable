package interface_adapter.controller;

import usecase.viewcourse.ViewCourseInputBoundary;
import usecase.viewcourse.ViewCourseInputData;

public class ViewCourseController {
    private final ViewCourseInputBoundary interactor;

    public ViewCourseController(ViewCourseInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String courseCode) {
        ViewCourseInputData data = new ViewCourseInputData(courseCode);
        interactor.execute(data);
    }
}
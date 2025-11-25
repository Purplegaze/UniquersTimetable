package interface_adapter.filter_courses;

import use_case.filter_courses.FilterCoursesOutputBoundary;
import use_case.filter_courses.FilterCoursesOutputData;

public class FilterCoursesPresenter implements FilterCoursesOutputBoundary {

    private final FilterCoursesViewModel viewModel;

    public FilterCoursesPresenter(FilterCoursesViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(FilterCoursesOutputData outputData) {
        viewModel.setCourses(outputData.getCourses());
    }
}

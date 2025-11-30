package interface_adapter.presenter;

import interface_adapter.viewmodel.ViewCourseViewModel;
import usecase.viewcourse.ViewCourseOutputBoundary;
import usecase.viewcourse.ViewCourseOutputData;

public class ViewCoursePresenter implements ViewCourseOutputBoundary {
    private final ViewCourseViewModel viewModel;

    public ViewCoursePresenter(ViewCourseViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(ViewCourseOutputData outputData) {
        viewModel.setCourse(outputData.getCourse());
    }

    @Override
    public void prepareFailView(String error) {
        viewModel.setError(error);
    }
}
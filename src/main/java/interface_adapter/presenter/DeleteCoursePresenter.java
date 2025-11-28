package interface_adapter.presenter;

import usecase.deletecourse.DeleteCourseOutputBoundary;
import usecase.deletecourse.DeleteCourseOutputData;

/**
 * Presenter for Delete Course use case.
 */
public class DeleteCoursePresenter implements DeleteCourseOutputBoundary {
    
    private final interface_adapter.presenter.TimetableViewInterface view;
    
    public DeleteCoursePresenter(interface_adapter.presenter.TimetableViewInterface view) {
        if (view == null) {
            throw new IllegalArgumentException("View cannot be null");
        }
        this.view = view;
    }

    @Override
    public void presentSuccess(DeleteCourseOutputData outputData) {
        // Just remove the deleted course
        view.removeCourse(
                outputData.getDeletedCourseCode(),
                outputData.getDeletedSectionCode()
        );
    }
    
    @Override
    public void presentError(String errorMessage) {
        view.showError(errorMessage);
    }
}

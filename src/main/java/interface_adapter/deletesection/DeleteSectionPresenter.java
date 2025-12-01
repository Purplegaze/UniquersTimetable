package interface_adapter.deletesection;

import usecase.deletesection.DeleteSectionOutputBoundary;
import usecase.deletesection.DeleteSectionOutputData;

/**
 * Presenter for Delete Course use case.
 */
public class DeleteSectionPresenter implements DeleteSectionOutputBoundary {
    
    private final interface_adapter.presenter.TimetableViewInterface view;
    
    public DeleteSectionPresenter(interface_adapter.presenter.TimetableViewInterface view) {
        if (view == null) {
            throw new IllegalArgumentException("View cannot be null");
        }
        this.view = view;
    }

    @Override
    public void presentSuccess(DeleteSectionOutputData outputData) {
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

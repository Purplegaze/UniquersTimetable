package interface_adapter.deletesection;

import usecase.deletesection.DeleteSectionOutputBoundary;
import usecase.deletesection.DeleteSectionOutputData;

/**
 * Presenter for Delete Section use case.
 */
public class DeleteSectionPresenter implements DeleteSectionOutputBoundary {

    private final DeleteSectionViewModel viewModel;

    public DeleteSectionPresenter(DeleteSectionViewModel viewModel) {
        if (viewModel == null) {
            throw new IllegalArgumentException("ViewModel cannot be null");
        }
        this.viewModel = viewModel;
    }

    @Override
    public void presentSuccess(DeleteSectionOutputData outputData) {
        // Update ViewModel with deletion info
        viewModel.setSectionDeleted(
                outputData.getDeletedCourseCode(),
                outputData.getDeletedSectionCode()
        );
    }

    @Override
    public void presentError(String errorMessage) {
        viewModel.setError(errorMessage);
    }
}
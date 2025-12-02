package interface_adapter.export;

import usecase.export.ExportTimetableOutputBoundary;
import usecase.export.ExportTimetableOutputData;

public class ExportTimetablePresenter implements ExportTimetableOutputBoundary {
    private final ExportTimetableViewModel viewModel;

    public ExportTimetablePresenter(ExportTimetableViewModel viewModel) {
        if (viewModel == null) {
            throw new IllegalArgumentException("ViewModel cannot be null");
        }
        this.viewModel = viewModel;
    }


    @Override
    public void presentSuccess(ExportTimetableOutputData outputData) {
        viewModel.setExported(outputData.getFilepath());
    }

    @Override
    public void presentCancel() {
        viewModel.setCancelled();
    }

    @Override
    public void presentError(String error) {
        viewModel.setError(error);
    }
}

package interface_adapter.importsections;


import usecase.importsections.ImportTimetableOutputBoundary;
import usecase.importsections.ImportTimetableOutputData;

public class ImportTimetablePresenter implements ImportTimetableOutputBoundary {
    private final ImportTimetableViewModel viewModel;

    public ImportTimetablePresenter(ImportTimetableViewModel viewModel) {
        if (viewModel == null) {
            throw new IllegalArgumentException("ViewModel cannot be null");
        }
        this.viewModel = viewModel;
    }


    @Override
    public void presentSuccess(ImportTimetableOutputData outputData) {
        viewModel.setImportDataString(outputData.importDataString());
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

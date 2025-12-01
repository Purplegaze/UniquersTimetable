package interface_adapter.presenter;

import usecase.export.ExportTimetableOutputBoundary;
import usecase.export.ExportTimetableOutputData;

public class ExportTimetablePresenter implements ExportTimetableOutputBoundary {
    @Override
    public void presentSuccess(ExportTimetableOutputData outputData) {
        //TODO: ???
        return;
    }
    public void presentError(String errorMessage) {
        //TODO: ???
        return;
    }
}

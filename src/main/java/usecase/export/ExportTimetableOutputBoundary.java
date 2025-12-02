package usecase.export;


public interface ExportTimetableOutputBoundary {
    void presentSuccess(ExportTimetableOutputData outputData);

    void presentCancel();

    void presentError(String errorMessage);

}

package usecase.export;


public interface ExportTimetableOutputBoundary {
    void presentSuccess(ExportTimetableOutputData outputData);

    void presentError(String errorMessage);

}

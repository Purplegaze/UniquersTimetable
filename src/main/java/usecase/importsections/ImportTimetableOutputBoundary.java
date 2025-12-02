package usecase.importsections;


public interface ImportTimetableOutputBoundary {
    void presentSuccess(ImportTimetableOutputData outputData);

    void presentCancel();

    void presentError(String errorMessage);

}

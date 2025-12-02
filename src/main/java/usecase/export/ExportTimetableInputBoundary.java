package usecase.export;

public interface ExportTimetableInputBoundary {
    /**
     * Executes the export use case.
     * @param inputData the input data
     */
    void execute(ExportTimetableInputData inputData);

}

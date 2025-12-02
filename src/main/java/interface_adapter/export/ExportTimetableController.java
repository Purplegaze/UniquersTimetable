package interface_adapter.export;

import usecase.export.ExportTimetableInputBoundary;
import usecase.export.ExportTimetableInputData;

public class ExportTimetableController {
    private final ExportTimetableInputBoundary exportInteractor;

    public ExportTimetableController(ExportTimetableInputBoundary inputBoundary) {
        if (inputBoundary == null) {
            throw new IllegalArgumentException("Export input boundary cannot be null");
        }
        this.exportInteractor = inputBoundary;

    }

    public void exportTimetable(String filePath) {
        ExportTimetableInputData inputData = new ExportTimetableInputData(filePath);
        try {
            exportInteractor.execute(inputData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

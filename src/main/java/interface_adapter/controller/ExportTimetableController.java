package interface_adapter.controller;

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

    public void ExportTimetable() {
        ExportTimetableInputData inputData = new ExportTimetableInputData("./testExport.json");
        try {
            exportInteractor.execute(inputData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

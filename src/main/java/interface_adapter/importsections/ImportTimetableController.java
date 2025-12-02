package interface_adapter.importsections;

import usecase.export.ExportTimetableInputBoundary;
import usecase.export.ExportTimetableInputData;
import usecase.importsections.ImportTimetableInputBoundary;
import usecase.importsections.ImportTimetableInputData;

public class ImportTimetableController {
    private final ImportTimetableInputBoundary importInteractor;

    public ImportTimetableController(ImportTimetableInputBoundary inputBoundary) {
        if (inputBoundary == null) {
            throw new IllegalArgumentException("Import input boundary cannot be null");
        }
        this.importInteractor = inputBoundary;

    }

    public void importTimetable(String filePath) {
        ImportTimetableInputData inputData = new ImportTimetableInputData(filePath);
        try {
            importInteractor.execute(inputData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

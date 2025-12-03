package usecase.export;

import data_access.TimetableDataAccessInterface;
import entity.*;

import java.util.List;

public class ExportTimetableInteractor implements ExportTimetableInputBoundary {
    private final ExportTimetableOutputBoundary presenter;
    private final TimetableDataAccessInterface timetableDataAccess;
    private final ExportTimetableDataAccessInterface exportDataAccess;

    public ExportTimetableInteractor(ExportTimetableOutputBoundary presenter,
                                     TimetableDataAccessInterface timetableDataAccess,
                                     ExportTimetableDataAccessInterface exportDataAccess
    ) {
        if (timetableDataAccess == null || presenter == null || exportDataAccess == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }

        this.timetableDataAccess = timetableDataAccess;
        this.presenter = presenter;
        this.exportDataAccess = exportDataAccess;
    }

    @Override
    public void execute(ExportTimetableInputData inputData) {
        try {
            String filepath = inputData.getFilepath();

            if (filepath == null) {
                presenter.presentCancel();
                return;
            }

            List<Section> sectionsInTimetable = timetableDataAccess.getAllSections();

            exportDataAccess.save(filepath, sectionsInTimetable);

            presenter.presentSuccess(new ExportTimetableOutputData(filepath));
        } catch (Exception e) {
            presenter.presentError("Error while exporting: " + e.getMessage());
        }
    }


}

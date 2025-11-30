package usecase.export;

import data_access.CourseDataAccessInterface;
import data_access.JSONConverter;
import data_access.TimetableDataAccessInterface;
import entity.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class ExportTimetableInteractor implements ExportTimetableInputBoundary {
    private final ExportTimetableOutputBoundary presenter;
    private final TimetableDataAccessInterface timetableDataAccess;
    private final CourseDataAccessInterface courseDataAccess;
    private final ExportTimetableDataAccessInterface exportDataAccess;

    public ExportTimetableInteractor(ExportTimetableOutputBoundary presenter,
                                     TimetableDataAccessInterface timetableDataAccess,
                                     CourseDataAccessInterface courseDataAccess,
                                     ExportTimetableDataAccessInterface exportDataAccess
    ) {
        this.courseDataAccess =  courseDataAccess;
        this.timetableDataAccess = timetableDataAccess;
        this.presenter = presenter;
        this.exportDataAccess = exportDataAccess;
        if (timetableDataAccess == null || courseDataAccess == null || presenter == null || exportDataAccess == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
    }

    @Override
    public void execute(ExportTimetableInputData inputData) {
        try {
            String filepath = inputData.getFilepath();

            List<Section> sectionsInTimetable = timetableDataAccess.getAllSections();

            JSONObject courses = JSONConverter.logSections(sectionsInTimetable);

            exportDataAccess.save(filepath, courses);

            presenter.presentSuccess(new ExportTimetableOutputData(filepath));
        } catch (Exception e) {
            presenter.presentError("Error while exporting: " + e.getMessage());
        }
    }


}

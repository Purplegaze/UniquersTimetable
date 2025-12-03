package usecase.importsections;

import data_access.CourseDataAccessInterface;
import data_access.TimetableDataAccessInterface;
import entity.*;
import usecase.addcourse.AddCourseOutputData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Interactor for importing courses from JSON files.
 */
public class ImportTimetableInteractor implements ImportTimetableInputBoundary {
    private final ImportTimetableOutputBoundary presenter;
    private final TimetableDataAccessInterface timetableDataAccess;
    private final CourseDataAccessInterface courseDataAccess;
    private final ImportTimetableDataAccessInterface importDataAccess;

    public ImportTimetableInteractor(ImportTimetableOutputBoundary presenter,
                                     TimetableDataAccessInterface timetableDataAccess,
                                     CourseDataAccessInterface courseDataAccess,
                                     ImportTimetableDataAccessInterface importDataAccess
    ) {
        // Don't allow dependencies to be null.
        if (timetableDataAccess == null || courseDataAccess == null || presenter == null || importDataAccess == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }

        this.courseDataAccess =  courseDataAccess;
        this.timetableDataAccess = timetableDataAccess;
        this.presenter = presenter;
        this.importDataAccess = importDataAccess;
    }

    @Override
    public void execute(ImportTimetableInputData inputData) {
        try {
            String filepath = inputData.getFilepath();
            ImportTimetableOutputData outputData = new ImportTimetableOutputData(filepath);

            // If filepath is null, the user cancelled the import.
            if (filepath == null) {
                presenter.presentCancel();
                return;
            }

            // Parse import JSON and import it as an ImportDataObject.
            ImportDataObject importData = importDataAccess.open(filepath);

            HashMap<String, List<String>> sectionMap = importData.getSectionMap();

            // Iterate through all course codes in the JSON file.
            for (String courseCode : sectionMap.keySet()) {
                Course course = courseDataAccess.findByCourseCode(courseCode);

                boolean added = false;
                for (String sectionCode : sectionMap.get(courseCode)) {
                    try {
                        if (course == null) {
                            // Course is null if findByCourseCode returned null <=> course wasn't found.)
                            throw new Course.SectionNotFoundException("Course not found: " + courseCode);
                        }
                        Section section = course.getSectionByCode(sectionCode);
                        timetableDataAccess.addSection(section);
                        outputData.incrementSectionsAdded();

                        // Convert TimeSlots to TimeSlotData
                        List<AddCourseOutputData.TimeSlotData> timeSlotDatas = new ArrayList<>();
                        for (TimeSlot timeSlot : section.getTimes()) {
                            timeSlotDatas.add(new AddCourseOutputData.TimeSlotData(
                                    timeSlot.getDayName(),
                                    timeSlot.getStartTime().getHour(),
                                    timeSlot.getEndTime().getHour(),
                                    timeSlot.getBuilding() != null ? timeSlot.getBuilding().getBuildingCode() : "TBD"
                            ));
                        }

                        // Create output data for visually adding the course
                        AddCourseOutputData addCourseOutputData = new AddCourseOutputData(
                                courseCode,
                                sectionCode,
                                "N/A",
                                timeSlotDatas,
                                false
                        );
                        outputData.addToAddCourseOutputData(addCourseOutputData);
                        added = true;
                    }
                    catch (Course.SectionNotFoundException e) {
                        // Log that the section wasn't found to pass onto the import message.
                        outputData.addToSectionsNotFound(courseCode + " " + sectionCode);
                    }
                }
                if (added) {
                    outputData.incrementCoursesAdded();
                }
            }
            presenter.presentSuccess(outputData);
        } catch (Exception e) {
            presenter.presentError("Error while importing: " + e.getMessage());
        }
    }


}

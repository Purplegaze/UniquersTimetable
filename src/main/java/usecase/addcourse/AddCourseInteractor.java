package usecase.addcourse;

import data_access.TimetableDataAccessInterface;

import java.util.List;

/**
 * Interactor for the Add Course use case.
 * Contains the business logic for adding courses to timetable.
 */
public class AddCourseInteractor implements AddCourseInputBoundary {

    private final TimetableDataAccessInterface timetableDataAccess;
    private final AddCourseOutputBoundary presenter;

    public AddCourseInteractor(TimetableDataAccessInterface timetableDataAccess,
                               AddCourseOutputBoundary presenter) {
        this.timetableDataAccess = timetableDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(AddCourseInputData inputData) {
        if (!isValidInput(inputData)) {
            presenter.presentError("Invalid input: Please check course details");
            return;
        }

        List<String> conflicts = findConflicts(inputData);
        addSectionToTimetable(inputData);
        presentResult(inputData, conflicts);
    }

    private List<String> findConflicts(AddCourseInputData inputData) {
        return timetableDataAccess.findConflicts(
                inputData.getDay(),
                inputData.getStartHour(),
                inputData.getEndHour()
        );
    }

    private void addSectionToTimetable(AddCourseInputData inputData) {
        timetableDataAccess.addSection(
                inputData.getCourseCode(),
                inputData.getSectionCode(),
                inputData.getDay(),
                inputData.getStartHour(),
                inputData.getEndHour(),
                inputData.getLocation()
        );
    }

    private void presentResult(AddCourseInputData inputData, List<String> conflicts) {
        AddCourseOutputData outputData = new AddCourseOutputData(
                inputData,
                !conflicts.isEmpty(),
                conflicts
        );

        if (!conflicts.isEmpty()) {
            presenter.presentConflict(outputData);
        } else {
            presenter.presentSuccess(outputData);
        }
    }

    private boolean isValidInput(AddCourseInputData inputData) {
        return isValidString(inputData.getCourseCode())
                && isValidString(inputData.getSectionCode())
                && isValidString(inputData.getDay())
                && isValidTimeRange(inputData.getStartHour(), inputData.getEndHour());
    }

    private boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean isValidTimeRange(int startHour, int endHour) {
        return startHour >= 0 && endHour >= 0 && endHour <= 24
                && startHour < endHour;
    }
}

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

        // Check term compatibility
        if (!isTermCompatible(inputData.getTerm())) {
            String currentTerm = timetableDataAccess.getCurrentTerm();
            String termName = "F".equals(currentTerm) ? "Fall" : "Winter";
            String courseTermName = "F".equals(inputData.getTerm()) ? "Fall" : "Winter";
            presenter.presentError("Cannot mix terms: Your timetable has " + termName
                    + " courses, but this is a " + courseTermName + " course");
            return;
        }

        // Check for duplicate
        if (isDuplicateSection(inputData)) {
            presenter.presentError("Section " + inputData.getCourseCode() + " "
                    + inputData.getSectionCode() + " is already in your timetable");
            return;
        }

        List<String> conflicts = findConflicts(inputData);
        addSectionToTimetable(inputData);
        presentResult(inputData, conflicts);
    }

    /**
     * Check if course term is compatible with current timetable term.
     */
    private boolean isTermCompatible(String courseCode) {
        String currentTerm = timetableDataAccess.getCurrentTerm();
        String courseTerm = extractTerm(courseCode);

        // Empty timetable - any course is fine
        if (currentTerm == null) {
            return true;
        }

        // Year course - always compatible
        if ("Y".equals(courseTerm)) {
            return true;
        }

        // Must match current term
        return currentTerm.equals(courseTerm);
    }

    /**
     * Extract term indicator (F/S/Y) from course code.
     */
    private String extractTerm(String courseCode) {
        if (courseCode == null || courseCode.isEmpty()) {
            return null;
        }
        String lastChar = courseCode.substring(courseCode.length() - 1);
        if (lastChar.matches("[FSY]")) {
            return lastChar;
        }
        return null;
    }

    private boolean isDuplicateSection(AddCourseInputData inputData) {
        return timetableDataAccess.hasSectionAtTime(
                inputData.getCourseCode(),
                inputData.getSectionCode(),
                inputData.getDay(),
                inputData.getStartHour(),
                inputData.getEndHour()
        );
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
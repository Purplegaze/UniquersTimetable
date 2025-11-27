package usecase.addcourse;

import data_access.TimetableDataAccessInterface;
import entity.Course;
import entity.Section;

/**
 * Interactor for the Add Course use case.
 */
public class AddCourseInteractor implements AddCourseInputBoundary {

    private final TimetableDataAccessInterface timetableDataAccess;
    private final AddCourseOutputBoundary presenter;

    public AddCourseInteractor(TimetableDataAccessInterface timetableDataAccess,
                               AddCourseOutputBoundary presenter) {
        if (timetableDataAccess == null || presenter == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
        this.timetableDataAccess = timetableDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(AddCourseInputData inputData) {
        if (inputData == null) {
            presenter.presentError("Invalid input: No section provided");
            return;
        }

        Section section = inputData.getSection();
        Course course = section.getCourse();

        // Validate section has time slots
        if (section.getTimes().isEmpty()) {
            presenter.presentError("Invalid input: Section has no time slots");
            return;
        }

        // Check term compatibility
        if (!isTermCompatible(course)) {
            String currentTerm = timetableDataAccess.getCurrentTerm();
            String termName = "F".equals(currentTerm) ? "Fall" : "Winter";
            String courseTermName = "F".equals(course.getTerm()) ? "Fall" : "Winter";
            presenter.presentError("Cannot mix terms: Your timetable has " + termName
                    + " courses, but this is a " + courseTermName + " course");
            return;
        }

        // Check for duplicate
        if (timetableDataAccess.hasSection(section)) {
            presenter.presentError("Section " + section.getSectionId()
                    + " is already in your timetable");
            return;
        }

        // Check if there are any conflicts
        boolean hasConflict = timetableDataAccess.hasConflicts(section);

        if (hasConflict) {
            presenter.presentError("Cannot add section: Time conflict with existing courses");
            return;
        }

        // Only add if NO conflicts
        boolean added = timetableDataAccess.addSection(section);

        if (!added) {
            presenter.presentError("Failed to add section to timetable");
            return;
        }

        AddCourseOutputData outputData = new AddCourseOutputData(section, false);
        presenter.presentSuccess(outputData);
    }

    /**
     * Check if course term is compatible with current timetable term.
     */
    private boolean isTermCompatible(Course course) {
        String currentTerm = timetableDataAccess.getCurrentTerm();
        String courseTerm = course.getTerm();

        // Empty timetable
        if (currentTerm == null) {
            return true;
        }

        // Year course
        if (course.isYearLong()) {
            return true;
        }

        // Must match current term
        return currentTerm.equals(courseTerm);
    }
}

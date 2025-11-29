package interface_adapter.presenter;

import interface_adapter.viewmodel.TimetableSlotViewModel;

/**
 * View interface for the timetable display.
 */
public interface TimetableViewInterface {

    void displayCourse(TimetableSlotViewModel viewModel);

    void showError(String message);

    void showConflictWarning(String message);

    void clearTimetable();

    void removeCourse(String courseCode, String sectionCode);
}

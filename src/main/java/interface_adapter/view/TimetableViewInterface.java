package interface_adapter.view;

import interface_adapter.viewmodel.TimetableSlotViewModel;

/**
 * View interface for the timetable display.
 */
public interface TimetableViewInterface {

    void displayCourse(TimetableSlotViewModel viewModel);

    void showError(String message);

    void showConflictWarning(String message);

    void clearTimetable();
}

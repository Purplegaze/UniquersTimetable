package view;

import interface_adapter.presenter.TimetableViewInterface;
import interface_adapter.viewmodel.TimetableSlotViewModel;

/**
 * Adapter to bridge the existing TimetableView with TimetableViewInterface.
 */
public class TimetableViewAdapter implements TimetableViewInterface {

    private final TimetableView timetableView;

    public TimetableViewAdapter(TimetableView timetableView) {
        if (timetableView == null) {
            throw new IllegalArgumentException("TimetableView cannot be null");
        }
        this.timetableView = timetableView;
    }

    @Override
    public void displayCourse(TimetableSlotViewModel viewModel) {
        // Convert ViewModel to the format expected by the existing view
        TimetableView.TimetableSlotItem item = new TimetableView.TimetableSlotItem(
                viewModel.getCourseCode(),
                viewModel.getSectionCode(),
                viewModel.getLocation(),
                viewModel.getColor(),
                viewModel.hasConflict()
        );

        timetableView.displayCourse(
                viewModel.getDayName(),
                viewModel.getStartHour(),
                viewModel.getEndHour(),
                item
        );
    }

    @Override
    public void showError(String message) {
        timetableView.showErrorMessage(message);
    }

    @Override
    public void showConflictWarning(String message) {
        timetableView.showConflictWarning(message);
    }

    @Override
    public void clearTimetable() { timetableView.clearAll(); }

    public TimetableView getTimetableView() {
        return timetableView;
    }
}

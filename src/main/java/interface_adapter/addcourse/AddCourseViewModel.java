package interface_adapter.addcourse;

import interface_adapter.ViewModel;
import interface_adapter.viewmodel.TimetableSlotViewModel;
import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for the Add Course use case.
 * Manages the state of courses added to the timetable.
 */
public class AddCourseViewModel extends ViewModel {

    private List<TimetableSlotViewModel> slots = new ArrayList<>();
    private String conflictMessage = "";
    private String errorMessage = "";

    public AddCourseViewModel() {
        super("addCourse");
    }

    public List<TimetableSlotViewModel> getSlots() {
        return new ArrayList<>(slots);
    }

    public String getConflictMessage() {
        return conflictMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Add multiple slots at once, for one section with multiple time slots.
     */
    public void addSlots(List<TimetableSlotViewModel> newSlots) {
        this.slots = new ArrayList<>(newSlots);
        firePropertyChanged("slotsAdded");
    }

    /**
     * Set conflict warning.
     */
    public void setConflict(String message) {
        this.conflictMessage = message;
        firePropertyChanged("conflict");
    }

    /**
     * Set error message.
     */
    public void setError(String message) {
        this.errorMessage = message;
        firePropertyChanged("error");
    }

    /**
     * Clear all slots from timetable.
     */
    public void clearSlots() {
        this.slots.clear();
        firePropertyChanged("cleared");
    }
}
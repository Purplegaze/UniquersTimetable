package interface_adapter.presenter;

import interface_adapter.viewmodel.TimetableSlotViewModel;
import entity.Course;
import entity.Section;
import entity.TimeSlot;
import usecase.addcourse.AddCourseOutputBoundary;
import usecase.addcourse.AddCourseOutputData;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Presenter for the Add Course use case.
 */
public class AddCoursePresenter implements AddCourseOutputBoundary {

    private final TimetableViewInterface view;
    private final Map<String, Color> courseColors;
    private int colorIndex;

    private static final Color[] COLOR_PALETTE = {
            new Color(189, 195, 255),
            new Color(175, 255, 175),
            new Color(255, 201, 208),
            new Color(255, 218, 185),
            new Color(243, 198, 243),
            new Color(194, 242, 255),
            new Color(255, 255, 153),
            new Color(169, 255, 207),
    };

    public AddCoursePresenter(TimetableViewInterface view) {
        if (view == null) {
            throw new IllegalArgumentException("View cannot be null");
        }
        this.view = view;
        this.courseColors = new HashMap<>();
        this.colorIndex = 0;
    }

    @Override
    public void presentSuccess(AddCourseOutputData outputData) {
        Section section = outputData.getSection();
        Course course = section.getCourse();

        Color color = getColorForCourse(course.getCourseCode());
        
        // Convert each time slot to a view model
        for (TimeSlot timeSlot : section.getTimes()) {
            TimetableSlotViewModel viewModel = createViewModel(
                    section, timeSlot, color, false
            );
            view.displayCourse(viewModel);
        }
    }

    @Override
    public void presentConflict(AddCourseOutputData outputData) {
        view.showConflictWarning("Cannot add section: Time conflict with existing courses");
    }

    @Override
    public void presentError(String errorMessage) {
        view.showError(errorMessage);
    }

    /**
     * Create a view model from entity data.
     */
    private TimetableSlotViewModel createViewModel(Section section, TimeSlot timeSlot,
                                                   Color color, boolean hasConflict) {
        return new TimetableSlotViewModel.Builder()
                .courseCode(section.getCourse().getCourseCode())
                .sectionCode(section.getSectionId())
                .location(timeSlot.getBuilding().getBuildingCode())
                .dayName(timeSlot.getDayName())
                .startHour(timeSlot.getStartTime().getHour())
                .endHour(timeSlot.getEndTime().getHour())
                .color(color)
                .hasConflict(hasConflict)
                .build();
    }

    /**
     * Get or assign a color for a course.
     */
    private Color getColorForCourse(String courseCode) {
        return courseColors.computeIfAbsent(courseCode, k -> {
            Color color = COLOR_PALETTE[colorIndex % COLOR_PALETTE.length];
            colorIndex++;
            return color;
        });
    }

    /**
     * Reset color assignments.
     */
    public void resetColors() {
        courseColors.clear();
        colorIndex = 0;
    }
}

package interface_adapter.addcourse;

import interface_adapter.viewmodel.TimetableSlotViewModel;
import usecase.addcourse.AddCourseOutputBoundary;
import usecase.addcourse.AddCourseOutputData;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Presenter for the Add Course use case.
 */
public class AddCoursePresenter implements AddCourseOutputBoundary {

    private final AddCourseViewModel viewModel;
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

    public AddCoursePresenter(AddCourseViewModel viewModel) {
        if (viewModel == null) {
            throw new IllegalArgumentException("ViewModel cannot be null");
        }
        this.viewModel = viewModel;
        this.courseColors = new HashMap<>();
        this.colorIndex = 0;
    }

    @Override
    public void presentSuccess(AddCourseOutputData outputData) {
        String courseCode = outputData.getCourseCode();
        String sectionCode = outputData.getSectionCode();

        Color color = getColorForCourse(courseCode);

        // Convert each time slot data to a view model
        List<TimetableSlotViewModel> slotViewModels = new ArrayList<>();
        for (AddCourseOutputData.TimeSlotData timeSlotData : outputData.getTimeSlots()) {
            TimetableSlotViewModel slotVM = new TimetableSlotViewModel.Builder()
                    .courseCode(courseCode)
                    .sectionCode(sectionCode)
                    .location(timeSlotData.getLocation())
                    .dayName(timeSlotData.getDayName())
                    .startHour(timeSlotData.getStartHour())
                    .endHour(timeSlotData.getEndHour())
                    .color(color)
                    .hasConflict(false)  // No conflict since it was successfully added
                    .build();
            slotViewModels.add(slotVM);
        }

        viewModel.addSlots(slotViewModels);
    }

    @Override
    public void presentConflict(AddCourseOutputData outputData) {
        viewModel.setConflict("Cannot add section: Time conflict with existing courses");
    }

    @Override
    public void presentError(String errorMessage) {
        viewModel.setError(errorMessage);
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
}
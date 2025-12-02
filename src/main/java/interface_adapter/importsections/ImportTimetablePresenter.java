package interface_adapter.importsections;


import interface_adapter.addcourse.AddCourseViewModel;
import interface_adapter.viewmodel.TimetableSlotViewModel;
import usecase.addcourse.AddCourseOutputData;
import usecase.importsections.ImportTimetableOutputBoundary;
import usecase.importsections.ImportTimetableOutputData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ImportTimetablePresenter implements ImportTimetableOutputBoundary {
    private final ImportTimetableViewModel viewModel;
    private final AddCourseViewModel addCourseViewModel;

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

    public ImportTimetablePresenter(ImportTimetableViewModel viewModel, AddCourseViewModel addCourseViewModel) {
        if (viewModel == null) {
            throw new IllegalArgumentException("ViewModel cannot be null");
        }
        this.viewModel = viewModel;
        this.addCourseViewModel = addCourseViewModel;
    }


    @Override
    public void presentSuccess(ImportTimetableOutputData outputData) {
        // Convert each time slot data to a view model
        List<TimetableSlotViewModel> slotViewModels = new ArrayList<>();

        int i = 7;
        for (AddCourseOutputData addCourseOutputData : outputData.getCoursesToAdd()) {
            for (AddCourseOutputData.TimeSlotData timeSlotData : addCourseOutputData.getTimeSlots()) {
                TimetableSlotViewModel slotVM = new TimetableSlotViewModel.Builder()
                        .courseCode(addCourseOutputData.getCourseCode())
                        .sectionCode(addCourseOutputData.getSectionCode())
                        .location(timeSlotData.getLocation())
                        .dayName(timeSlotData.getDayName())
                        .startHour(timeSlotData.getStartHour())
                        .endHour(timeSlotData.getEndHour())
                        .color(COLOR_PALETTE[i])
                        .hasConflict(false)  // No conflict since it was successfully added
                        .build();
                slotViewModels.add(slotVM);
            }
            i--;
            if (i < 0) {i += 8;}
        }



        addCourseViewModel.addSlots(slotViewModels);

        viewModel.setImportDataString(outputData.importDataString());
    }

    @Override
    public void presentCancel() {
        viewModel.setCancelled();
    }

    @Override
    public void presentError(String error) {
        viewModel.setError(error);
    }
}

package interface_adapter.viewcourse;

import usecase.viewcourse.ViewCourseOutputBoundary;
import usecase.viewcourse.ViewCourseOutputData;
import java.util.ArrayList;
import java.util.List;

public class ViewCoursePresenter implements ViewCourseOutputBoundary {
    private final ViewCourseViewModel viewModel;

    public ViewCoursePresenter(ViewCourseViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(ViewCourseOutputData outputData) {
        // Map OutputData sections to ViewModel sections
        List<ViewCourseViewModel.SectionViewModel> sectionVMs = new ArrayList<>();

        for (ViewCourseOutputData.SectionData secData : outputData.getSections()) {

            // Map TimeSlots
            List<ViewCourseViewModel.TimeSlotViewModel> timeSlotVMs = new ArrayList<>();
            for (ViewCourseOutputData.TimeSlotData tsData : secData.getTimeSlots()) {
                timeSlotVMs.add(new ViewCourseViewModel.TimeSlotViewModel(
                        tsData.getDay(),
                        tsData.getStart(),
                        tsData.getEnd(),
                        tsData.getLocation()
                ));
            }

            // Create Section ViewModel
            sectionVMs.add(new ViewCourseViewModel.SectionViewModel(
                    secData.getSectionId(),
                    secData.getInstructors(),
                    secData.getEnrolled(),
                    secData.getCapacity(),
                    secData.isFull(),
                    outputData.getCourseCode(),
                    timeSlotVMs
            ));
        }

        // Pass simple data to ViewModel
        viewModel.setCourseData(
                outputData.getCourseCode(),
                outputData.getCourseName(),
                outputData.getTerm(),
                outputData.getRecommendation(),
                outputData.getWorkload(),
                sectionVMs
        );
    }

    @Override
    public void prepareFailView(String error) {
        viewModel.setError(error);
    }
}
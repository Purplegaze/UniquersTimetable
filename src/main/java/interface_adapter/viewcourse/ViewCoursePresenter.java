package interface_adapter.viewcourse;

import entity.Course;
import entity.Section;
import entity.TimeSlot;
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
        Course course = outputData.getCourse();

        // Extract basic course info
        String courseCode = course.getCourseCode();
        String courseName = course.getCourseName();
        String term = course.getTerm();

        Float recommendation = null;
        Float workload = null;
        if (course.getCourseRating() != null) {
            recommendation = course.getCourseRating().getRating("Recommendation");
            workload = course.getCourseRating().getRating("Workload");
        }

        // Convert Section entities to SectionViewModels
        List<ViewCourseViewModel.SectionViewModel> sectionViewModels = new ArrayList<>();

        for (Section section : course.getSections()) {

            // 2a. Convert TimeSlots within each section
            List<ViewCourseViewModel.TimeSlotViewModel> timeSlotViewModels = new ArrayList<>();
            for (TimeSlot ts : section.getTimes()) {
                String dayName = getDayName(ts.getDayOfWeek());
                int startHour = ts.getStartTime().getHour();
                int endHour = ts.getEndTime().getHour();
                String location = (ts.getBuilding() != null) ? ts.getBuilding().getBuildingCode() : "TBD";

                timeSlotViewModels.add(new ViewCourseViewModel.TimeSlotViewModel(
                        dayName, startHour, endHour, location
                ));
            }

            // Create the SectionViewModel
            sectionViewModels.add(new ViewCourseViewModel.SectionViewModel(
                    section.getSectionId(),
                    new ArrayList<>(section.getInstructors()),
                    section.getEnrolledStudents(),
                    section.getCapacity(),
                    section.isFull(),
                    courseCode,
                    timeSlotViewModels
            ));
        }

        // Pass the formatted data to the ViewModel
        viewModel.setCourseData(courseCode, courseName, term, recommendation, workload, sectionViewModels);
    }

    @Override
    public void prepareFailView(String error) {
        viewModel.setError(error);
    }

    // Convert integer day to string
    private String getDayName(int dayOfWeek) {
        return switch(dayOfWeek) {
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            case 3 -> "Wednesday";
            case 4 -> "Thursday";
            case 5 -> "Friday";
            case 6 -> "Saturday";
            case 7 -> "Sunday";
            default -> "Unknown";
        };
    }
}
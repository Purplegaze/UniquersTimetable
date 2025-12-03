package usecase.viewcourse;

import data_access.CourseDataAccessInterface;
import data_access.CourseEvalDataReader;
import entity.Course;
import entity.Rating;
import entity.Section;
import entity.TimeSlot;

import java.util.ArrayList;
import java.util.List;

public class ViewCourseInteractor implements ViewCourseInputBoundary {
    private final CourseDataAccessInterface courseDataAccess;
    private final CourseEvalDataReader ratingDataAccess;
    private final ViewCourseOutputBoundary presenter;

    public ViewCourseInteractor(CourseDataAccessInterface courseDataAccess,
                                CourseEvalDataReader ratingDataAccess,
                                ViewCourseOutputBoundary presenter) {
        this.courseDataAccess = courseDataAccess;
        this.ratingDataAccess = ratingDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(ViewCourseInputData inputData) {
        String code = inputData.getCourseCode();
        Course course = courseDataAccess.findByCourseCode(code);

        if (course == null) {
            presenter.prepareFailView("Course not found: " + code);
        } else {
            // 1. Attach Rating
            Rating rating = ratingDataAccess.getRating(code);
            if (rating != null) {
                course.setCourseRating(rating);
            }

            // 2. Extract Data for OutputData
            Float rec = (course.getCourseRating() != null) ? course.getCourseRating().getRating("Recommendation") : null;
            Float work = (course.getCourseRating() != null) ? course.getCourseRating().getRating("Workload") : null;

            List<ViewCourseOutputData.SectionData> sectionDataList = new ArrayList<>();

            for (Section section : course.getSections()) {
                List<ViewCourseOutputData.TimeSlotData> timeSlotDataList = new ArrayList<>();

                for (TimeSlot ts : section.getTimes()) {
                    String dayName = getDayName(ts.getDayOfWeek());
                    String loc = (ts.getBuilding() != null) ? ts.getBuilding().getBuildingCode() : "TBD";

                    timeSlotDataList.add(new ViewCourseOutputData.TimeSlotData(
                            dayName,
                            ts.getStartTime().getHour(),
                            ts.getEndTime().getHour(),
                            loc
                    ));
                }

                sectionDataList.add(new ViewCourseOutputData.SectionData(
                        section.getSectionId(),
                        new ArrayList<>(section.getInstructors()),
                        section.getEnrolledStudents(),
                        section.getCapacity(),
                        section.isFull(),
                        timeSlotDataList
                ));
            }

            ViewCourseOutputData outputData = new ViewCourseOutputData(
                    course.getCourseCode(),
                    course.getCourseName(),
                    course.getTerm(),
                    rec,
                    work,
                    sectionDataList
            );

            presenter.prepareSuccessView(outputData);
        }
    }

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
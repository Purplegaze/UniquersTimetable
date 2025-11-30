package usecase.viewcourse;

import data_access.CourseDataAccessInterface;
import data_access.CourseEvalDataReader; // Your Step 2 Class
import entity.Course;
import entity.Rating;

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
            // Fetch and attach the rating here!
            Rating rating = ratingDataAccess.getRating(code);
            if (rating != null) {
                course.setCourseRating(rating);
            }

            ViewCourseOutputData outputData = new ViewCourseOutputData(course);
            presenter.prepareSuccessView(outputData);
        }
    }
}
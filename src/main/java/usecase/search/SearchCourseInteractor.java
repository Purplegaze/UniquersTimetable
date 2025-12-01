package usecase.search;

import data_access.CourseDataAccessInterface;
import entity.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * Interactor for the Search Course use case.
 */
public class SearchCourseInteractor implements SearchCourseInputBoundary {

    private final CourseDataAccessInterface courseDataAccess;
    private final SearchCourseOutputBoundary presenter;

    public SearchCourseInteractor(CourseDataAccessInterface courseDataAccess,
                                  SearchCourseOutputBoundary presenter) {
        if (courseDataAccess == null || presenter == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
        this.courseDataAccess = courseDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(SearchCourseInputData inputData) {
        if (inputData == null) {
            presenter.presentError("Invalid input: No search query provided");
            return;
        }

        String query = inputData.getQuery();
        if (query == null) {
            query = "";
        }
        query = query.toLowerCase().trim();

        try {
            List<Course> allCourses = courseDataAccess.getAllCourses();

            List<Course> matchingCourses = filterCourses(allCourses, query);

            // Present results
            if (matchingCourses.isEmpty()) {
                presenter.presentNoResults();
            } else {
                SearchCourseOutputData outputData =
                    new SearchCourseOutputData(convertToCourseData(matchingCourses));
                presenter.presentSearchResults(outputData);
            }
        } catch (Exception e) {
            presenter.presentError("Error searching courses: " + e.getMessage());
        }
    }

    private List<Course> filterCourses(List<Course> courses, String query) {
        if (query.isEmpty()) {
            return new ArrayList<>(courses);
        }

        List<Course> matches = new ArrayList<>();
        for (Course course : courses) {
            if (matchesCourse(course, query)) {
                matches.add(course);
            }
        }
        return matches;
    }

    private boolean matchesCourse(Course course, String query) {
        String code = course.getCourseCode() != null
                ? course.getCourseCode().toLowerCase() : "";
        String name = course.getCourseName() != null
                ? course.getCourseName().toLowerCase() : "";

        return code.contains(query) || name.contains(query);
    }

    private List<SearchCourseOutputData.CourseData> convertToCourseData(List<Course> courses) {
        List<SearchCourseOutputData.CourseData> courseData = new ArrayList<>();
        for (Course course : courses) {
            courseData.add(new SearchCourseOutputData.CourseData(
                    course.getCourseCode(),
                    course.getCourseName()
            ));
        }
        return courseData;
    }
}

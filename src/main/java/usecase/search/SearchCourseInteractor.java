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
        this.courseDataAccess = courseDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(SearchCourseInputData inputData) {
        String query = inputData.getQuery().toLowerCase().trim();

        List<Course> allCourses = courseDataAccess.getAllCourses();
        List<Course> matchingCourses = filterCourses(allCourses, query);

        extracted(matchingCourses);
    }

    private void extracted(List<Course> matchingCourses) {
        if (matchingCourses.isEmpty()) {
            presenter.presentNoResults();
        } else {
            List<SearchCourseOutputData.ResultItem> resultItems = new ArrayList<>();
            for (Course course : matchingCourses) {
                resultItems.add(new SearchCourseOutputData.ResultItem(
                        course.getCourseCode(),
                        course.getCourseName()
                ));
            }
            presenter.presentSearchResults(new SearchCourseOutputData(resultItems));
        }
    }

    /**
     * Filter courses matching the query against code, subject, or name.
     */
    private List<Course> filterCourses(List<Course> courses, String query) {
        if (query.isEmpty()) {
            return new ArrayList<>(courses);
        }

        List<Course> matches = new ArrayList<>();
        for (Course course : courses) {
            String code = course.getCourseCode() != null
                    ? course.getCourseCode().toLowerCase() : "";
            String name = course.getCourseName() != null
                    ? course.getCourseName().toLowerCase() : "";

            if (code.contains(query) || name.contains(query)) {
                matches.add(course);
            }
        }
        return matches;
    }
}


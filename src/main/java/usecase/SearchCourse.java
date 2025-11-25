package usecase;

import data_access.CourseDataAccessInterface;
import entity.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * Use Case 1: Search Courses
 */
public class SearchCourse {

    public interface InputBoundary {
        void execute(InputData inputData);
    }

    public static class InputData {
        private final String query;

        public InputData(String query) {
            this.query = query;
        }

        public String getQuery() {
            return query;
        }
    }

    public interface OutputBoundary {
        void presentSearchResults(OutputData outputData);
        void presentNoResults();
        void presentError(String errorMessage);
    }

    public static class OutputData {

        public static class ResultItem {
            private final String courseCode;
            private final String courseName;

            public ResultItem(String courseCode, String courseName) {
                this.courseCode = courseCode;
                this.courseName = courseName;
            }

            public String getCourseCode() { return courseCode; }
            public String getCourseName() { return courseName; }
        }

        private final List<ResultItem> results;

        public OutputData(List<ResultItem> results) {
            this.results = results;
        }

        public List<ResultItem> getResults() {
            return results;
        }
    }

    public static class Interactor implements InputBoundary {

        private final CourseDataAccessInterface courseDataAccess;
        private final OutputBoundary presenter;

        public Interactor(CourseDataAccessInterface courseDataAccess, OutputBoundary presenter) {
            this.courseDataAccess = courseDataAccess;
            this.presenter = presenter;
        }

        @Override
        public void execute(InputData inputData) {
            try {
                String query = inputData.getQuery().toLowerCase().trim();

                List<Course> allCourses = courseDataAccess.getAllCourses();
                List<Course> matchingCourses = filterCourses(allCourses, query);

                if (matchingCourses.isEmpty()) {
                    presenter.presentNoResults();
                } else {
                    List<OutputData.ResultItem> resultItems = new ArrayList<>();
                    for (Course course : matchingCourses) {
                        resultItems.add(new OutputData.ResultItem(
                                course.getCourseCode(),
                                course.getCourseName()
                        ));
                    }
                    presenter.presentSearchResults(new OutputData(resultItems));
                }

            } catch (Exception e) {
                presenter.presentError("Failed to search courses: " + e.getMessage());
            }
        }

        // Filter courses matching the query against code, subject, or name.
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
}

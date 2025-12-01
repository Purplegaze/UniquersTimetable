package usecase.search;

import data_access.CourseDataAccessInterface;
import entity.Building;
import entity.Course;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SearchCourseInteractorTest {

    @ParameterizedTest
    @MethodSource("successTestCases")
    void successTest_Parameterized(String query, int expectedCount, CourseDataAccessInterface dataAccess) {
        SearchCourseInputData inputData = new SearchCourseInputData(query);
        SearchCourseOutputBoundary presenter = createSuccessPresenter(expectedCount);
        new SearchCourseInteractor(dataAccess, presenter).execute(inputData);
    }

    private static Stream<Arguments> successTestCases() {
        return Stream.of(
                Arguments.of("CSC207", 1, new InMemoryCourseDataAccess()),
                Arguments.of("Software Design", 1, new InMemoryCourseDataAccess()),
                Arguments.of("CSC", 3, new InMemoryCourseDataAccess()),
                Arguments.of("", 3, new InMemoryCourseDataAccess()),
                Arguments.of("   ", 3, new InMemoryCourseDataAccess()),
                Arguments.of(null, 3, new InMemoryCourseDataAccess()),
                Arguments.of("csc207", 1, new InMemoryCourseDataAccess()),
                Arguments.of("CSC207", 1, new CourseDataAccessWithNullCode()),
                Arguments.of("CSC999", 1, new CourseDataAccessWithNullName())
        );
    }

    @Test
    void failureTest_NoMatch() {
        SearchCourseInputData inputData = new SearchCourseInputData("XYZ999");

        SearchCourseOutputBoundary presenter = new SearchCourseOutputBoundary() {
            @Override
            public void presentSearchResults(SearchCourseOutputData outputData) {
                fail("Unexpected success.");
            }

            @Override
            public void presentNoResults() {
                assertTrue(true);
            }

            @Override
            public void presentError(String errorMessage) {
                fail("Unexpected error.");
            }
        };

        new SearchCourseInteractor(new InMemoryCourseDataAccess(), presenter).execute(inputData);
    }

    @Test
    void failureTest_NullInputData() {
        SearchCourseOutputBoundary presenter = new SearchCourseOutputBoundary() {
            @Override
            public void presentSearchResults(SearchCourseOutputData outputData) {
                fail("Unexpected success.");
            }

            @Override
            public void presentNoResults() {
                fail("Unexpected no results.");
            }

            @Override
            public void presentError(String errorMessage) {
                assertEquals("Invalid input: No search query provided", errorMessage);
            }
        };

        new SearchCourseInteractor(new InMemoryCourseDataAccess(), presenter).execute(null);
    }

    @Test
    void failureTest_EmptyDatabase() {
        SearchCourseInputData inputData = new SearchCourseInputData("CSC207");

        SearchCourseOutputBoundary presenter = new SearchCourseOutputBoundary() {
            @Override
            public void presentSearchResults(SearchCourseOutputData outputData) {
                fail("Unexpected success.");
            }

            @Override
            public void presentNoResults() {
                assertTrue(true);
            }

            @Override
            public void presentError(String errorMessage) {
                fail("Unexpected error.");
            }
        };

        new SearchCourseInteractor(new EmptyInMemoryCourseDataAccess(), presenter).execute(inputData);
    }

    @Test
    void failureTest_DataAccessException() {
        SearchCourseInputData inputData = new SearchCourseInputData("CSC207");

        SearchCourseOutputBoundary presenter = new SearchCourseOutputBoundary() {
            @Override
            public void presentSearchResults(SearchCourseOutputData outputData) {
                fail("Unexpected success.");
            }

            @Override
            public void presentNoResults() {
                fail("Unexpected no results.");
            }

            @Override
            public void presentError(String errorMessage) {
                assertTrue(errorMessage.contains("Error searching courses"));
            }
        };

        new SearchCourseInteractor(new FaultyCourseDataAccess(), presenter).execute(inputData);
    }

    @Test
    void constructorTest_NullDependencies() {
        SearchCourseOutputBoundary presenter = new SearchCourseOutputBoundary() {
            @Override
            public void presentSearchResults(SearchCourseOutputData outputData) {}
            @Override
            public void presentNoResults() {}
            @Override
            public void presentError(String errorMessage) {}
        };

        assertThrows(IllegalArgumentException.class, () -> new SearchCourseInteractor(null, presenter));
        assertThrows(IllegalArgumentException.class, () -> new SearchCourseInteractor(new InMemoryCourseDataAccess(), null));
    }

    // Helper method
    private SearchCourseOutputBoundary createSuccessPresenter(int expectedCount) {
        return new SearchCourseOutputBoundary() {
            @Override
            public void presentSearchResults(SearchCourseOutputData outputData) {
                assertEquals(expectedCount, outputData.getResultCount());
                assertTrue(outputData.hasResults());
            }

            @Override
            public void presentNoResults() {
                fail("Unexpected no results.");
            }

            @Override
            public void presentError(String errorMessage) {
                fail("Unexpected error: " + errorMessage);
            }
        };
    }

    // helper classes

    private static class InMemoryCourseDataAccess implements CourseDataAccessInterface {
        private final List<Course> courses = new ArrayList<>();

        public InMemoryCourseDataAccess() {
            Building ba = new Building("BA", "Bahen Centre", 43, 79);
            courses.add(new Course("CSC207", "Software Design", "Intro to software design", 0.5f, "F", new ArrayList<>(), ba, 5));
            courses.add(new Course("CSC236", "Introduction to Theory of Computation", "Intro to computability", 0.5f, "F", new ArrayList<>(), ba, 5));
            courses.add(new Course("CSC258", "Computer Organization", "Computer structures", 0.5f, "S", new ArrayList<>(), ba, 5));
        }

        @Override
        public List<Course> getAllCourses() { return new ArrayList<>(courses); }

        @Override
        public Course findByCourseCode(String courseCode) {
            for (Course course : courses) {
                if (course.getCourseCode().equals(courseCode)) return course;
            }
            return null;
        }
    }

    private static class EmptyInMemoryCourseDataAccess implements CourseDataAccessInterface {
        @Override
        public List<Course> getAllCourses() { return new ArrayList<>(); }

        @Override
        public Course findByCourseCode(String courseCode) { return null; }
    }

    private static class CourseDataAccessWithNullCode implements CourseDataAccessInterface {
        private final List<Course> courses = new ArrayList<>();

        public CourseDataAccessWithNullCode() {
            Building ba = new Building("BA", "Bahen", 43, 79);
            courses.add(new Course(null, "Mystery Course", "No code", 0.5f, "F", new ArrayList<>(), ba, 1));
            courses.add(new Course("CSC207", "Software Design", "Intro", 0.5f, "F", new ArrayList<>(), ba, 5));
        }

        @Override
        public List<Course> getAllCourses() { return new ArrayList<>(courses); }

        @Override
        public Course findByCourseCode(String courseCode) { return null; }
    }

    private static class CourseDataAccessWithNullName implements CourseDataAccessInterface {
        private final List<Course> courses = new ArrayList<>();

        public CourseDataAccessWithNullName() {
            Building ba = new Building("BA", "Bahen", 43, 79);
            courses.add(new Course("CSC999", null, "No name", 0.5f, "F", new ArrayList<>(), ba, 1));
        }

        @Override
        public List<Course> getAllCourses() { return new ArrayList<>(courses); }

        @Override
        public Course findByCourseCode(String courseCode) { return null; }
    }

    private static class FaultyCourseDataAccess implements CourseDataAccessInterface {
        @Override
        public List<Course> getAllCourses() { throw new RuntimeException("Database connection failed"); }

        @Override
        public Course findByCourseCode(String courseCode) { throw new RuntimeException("Database connection failed"); }
    }
}
package usecase.customtimefilter;

import data_access.CourseDataAccessInterface;
import entity.Course;
import entity.Section;
import entity.TimeSlot;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CustomTimeFilterInteractor.
 *
 * Format is based on the instructor's LoginInteractorTest example:
 * - Arrange: input data, repository, presenter
 * - Act: call interactor.execute(...)
 * - Assert: use assertions inside presenter methods
 */
class CustomTimeFilterInteractorTest {

    /**
     * Simple in-memory repository implementation used only for tests.
     */
    private static class InMemoryCourseRepo implements CourseDataAccessInterface {
        private final List<Course> courses = new ArrayList<>();

        void addCourse(Course course) {
            courses.add(course);
        }

        @Override
        public List<Course> getAllCourses() {
            return new ArrayList<>(courses);
        }

        @Override
        public Course findByCourseCode(String courseCode) {
            for (Course c : courses) {
                if (c.getCourseCode() != null &&
                        c.getCourseCode().equalsIgnoreCase(courseCode)) {
                    return c;
                }
            }
            return null;
        }
    }

    /**
     * Helper to build a Course with a single Section and a single TimeSlot.
     */
    private Course createCourseWithOneSlot(String courseCode,
                                           String courseName,
                                           int dayOfWeek,
                                           int startHour,
                                           int endHour) {
        List<Section> sections = new ArrayList<>();
        Course course = new Course(
                courseCode,
                courseName,
                "",
                0f,
                "",
                sections,
                null,
                0
        );

        TimeSlot slot = new TimeSlot(
                dayOfWeek,
                LocalTime.of(startHour, 0),
                LocalTime.of(endHour, 0),
                null
        );

        Section section = new Section(
                "LEC0101",
                List.of(slot),
                0,
                List.of("Instructor"),
                100,
                course
        );

        sections.add(section);
        return course;
    }

    @Test
    void successReturnsOnlyCoursesWithinTimeWindowAndMatchingQuery() {
        // Arrange
        // Query: "csc" should match both CSC207 and CSC236.
        // Time window: Monday 09:00–12:00
        CustomTimeFilterInputData inputData =
                new CustomTimeFilterInputData("csc", "Mon", "09:00", "12:00");

        InMemoryCourseRepo courseRepository = new InMemoryCourseRepo();

        // Course that fits the time window
        Course csc207 = createCourseWithOneSlot(
                "CSC207", "Software Design", 1, 10, 11);  // Mon 10:00–11:00
        // Course that does NOT fit the time window (ends after 12:00)
        Course csc236 = createCourseWithOneSlot(
                "CSC236", "Theory of Computation", 1, 11, 13); // Mon 11:00–13:00

        courseRepository.addCourse(csc207);
        courseRepository.addCourse(csc236);

        // Presenter that asserts success is as expected
        CustomTimeFilterOutputBoundary successPresenter = new CustomTimeFilterOutputBoundary() {
            @Override
            public void presentResults(CustomTimeFilterOutputData outputData) {
                // We expect only CSC207 to be returned
                assertNotNull(outputData);

                // Adjust this if your getter has a different name (e.g., getResultItems())
                List<CustomTimeFilterOutputData.ResultItem> results = outputData.getResults();

                assertEquals(1, results.size());
                CustomTimeFilterOutputData.ResultItem item = results.get(0);
                assertEquals("CSC207", item.getCourseCode());
                assertEquals("Software Design", item.getCourseName());
            }

            @Override
            public void presentNoResults() {
                fail("Use case failure (no results) is unexpected.");
            }

            @Override
            public void presentError(String errorMessage) {
                fail("Use case error is unexpected: " + errorMessage);
            }
        };

        CustomTimeFilterInputBoundary interactor =
                new CustomTimeFilterInteractor(courseRepository, successPresenter);

        // Act
        interactor.execute(inputData);
    }

    @Test
    void failureNoCourseFitsTimeWindow() {
        // Arrange
        CustomTimeFilterInputData inputData =
                new CustomTimeFilterInputData("", "Friday", "08:00", "10:00");

        InMemoryCourseRepo courseRepository = new InMemoryCourseRepo();

        // This course is on Friday 10:00–12:00, so it does NOT fit within 08:00–10:00.
        Course course = createCourseWithOneSlot(
                "CSC207", "Software Design", 5, 10, 12); // Friday
        courseRepository.addCourse(course);

        CustomTimeFilterOutputBoundary failurePresenter = new CustomTimeFilterOutputBoundary() {
            @Override
            public void presentResults(CustomTimeFilterOutputData outputData) {
                fail("Use case success is unexpected when no course fits.");
            }

            @Override
            public void presentNoResults() {
                // This is the expected path: no courses in the time window
                // No assertion needed here other than not failing.
            }

            @Override
            public void presentError(String errorMessage) {
                fail("Error is unexpected for a valid but non-matching time range: " + errorMessage);
            }
        };

        CustomTimeFilterInputBoundary interactor =
                new CustomTimeFilterInteractor(courseRepository, failurePresenter);

        // Act
        interactor.execute(inputData);
    }

    @Test
    void failureInvalidDayOfWeekCallsError() {
        // Arrange
        CustomTimeFilterInputData inputData =
                new CustomTimeFilterInputData("", "NotADay", "09:00", "11:00");

        InMemoryCourseRepo courseRepository = new InMemoryCourseRepo();

        CustomTimeFilterOutputBoundary failurePresenter = new CustomTimeFilterOutputBoundary() {
            @Override
            public void presentResults(CustomTimeFilterOutputData outputData) {
                fail("Use case success is unexpected for invalid day input.");
            }

            @Override
            public void presentNoResults() {
                // Note: filterByTimeWindow returns empty list after calling presentError,
                // so execute(...) will also call presentNoResults().
                // We do NOT fail here.
            }

            @Override
            public void presentError(String errorMessage) {
                assertEquals("Invalid day of week: NotADay", errorMessage);
            }
        };

        CustomTimeFilterInputBoundary interactor =
                new CustomTimeFilterInteractor(courseRepository, failurePresenter);

        // Act
        interactor.execute(inputData);
    }

    @Test
    void failureInvalidTimeFormatCallsError() {
        // Arrange: invalid start time format ("9" instead of "09:00")
        CustomTimeFilterInputData inputData =
                new CustomTimeFilterInputData("", "Mon", "9", "11:00");

        InMemoryCourseRepo courseRepository = new InMemoryCourseRepo();

        CustomTimeFilterOutputBoundary failurePresenter = new CustomTimeFilterOutputBoundary() {
            @Override
            public void presentResults(CustomTimeFilterOutputData outputData) {
                fail("Use case success is unexpected for invalid time format.");
            }

            @Override
            public void presentNoResults() {
                // As above, execute(...) will also call presentNoResults()
                // after presentError. We accept that.
            }

            @Override
            public void presentError(String errorMessage) {
                assertEquals("Invalid time format. Please use HH:mm, e.g., 10:00.", errorMessage);
            }
        };

        CustomTimeFilterInputBoundary interactor =
                new CustomTimeFilterInteractor(courseRepository, failurePresenter);

        // Act
        interactor.execute(inputData);
    }

    @Test
    void failureStartTimeNotBeforeEndTimeCallsError() {
        // Arrange: start >= end
        CustomTimeFilterInputData inputData =
                new CustomTimeFilterInputData("", "Mon", "13:00", "11:00");

        InMemoryCourseRepo courseRepository = new InMemoryCourseRepo();

        CustomTimeFilterOutputBoundary failurePresenter = new CustomTimeFilterOutputBoundary() {
            @Override
            public void presentResults(CustomTimeFilterOutputData outputData) {
                fail("Use case success is unexpected when start time is not before end time.");
            }

            @Override
            public void presentNoResults() {
                // Again, execute(...) will call this after presentError.
            }

            @Override
            public void presentError(String errorMessage) {
                assertEquals("Start time must be before end time.", errorMessage);
            }
        };

        CustomTimeFilterInputBoundary interactor =
                new CustomTimeFilterInteractor(courseRepository, failurePresenter);

        // Act
        interactor.execute(inputData);
    }
}


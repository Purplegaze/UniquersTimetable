package usecase.viewcourse;

import data_access.CourseDataAccessInterface;
import data_access.CourseEvalDataReader;
import entity.Building;
import entity.Course;
import entity.Rating;
import entity.Section;
import entity.TimeSlot;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ViewCourseInteractorTest {

    // --- Helper to create a dummy Course entity for testing ---
    private Course createTestCourse(String code) {
        Building building = new Building("BA", "Bahen", 0.0, 0.0);
        List<Section> sections = new ArrayList<>();

        // Create Course with correct 8 arguments (no 'null' for rating)
        Course course = new Course(code, "Software Design", "Software", 0.5f, "F", sections, building, 1);

        // Create Section linked to the course
        TimeSlot timeSlot = new TimeSlot(1, LocalTime.of(10, 0), LocalTime.of(11, 0), building);
        List<TimeSlot> times = new ArrayList<>();
        times.add(timeSlot);

        List<String> instructors = new ArrayList<>();
        instructors.add("Prof. Smith");
        Section section = new Section("LEC0101", times, 50, instructors, 100, course);

        sections.add(section);

        return course;
    }

    // --- Helper to create a temp file so CourseEvalDataReader doesn't crash ---
    private String createDummyFile() {
        try {
            File tempFile = File.createTempFile("dummy_ratings", ".csv");
            tempFile.deleteOnExit();
            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            return "";
        }
    }

    @Test
    public void successTestWithRatings() {
        // Arrange
        ViewCourseInputData inputData = new ViewCourseInputData("CSC207");

        CourseDataAccessInterface courseDao = new CourseDataAccessInterface() {
            @Override
            public Course findByCourseCode(String code) { return createTestCourse(code); }
            @Override
            public List<Course> getAllCourses() { return null; }
        };

        CourseEvalDataReader ratingDao = new CourseEvalDataReader(createDummyFile()) {
            @Override
            public Rating getRating(String courseCode) {
                Map<String, Float> scores = new HashMap<>();
                scores.put("Recommendation", 4.5f);
                scores.put("Workload", 3.0f);
                return new Rating("Prof. Smith", courseCode, scores);
            }
        };

        ViewCourseOutputBoundary successPresenter = new ViewCourseOutputBoundary() {
            @Override
            public void prepareSuccessView(ViewCourseOutputData outputData) {
                // Verify DTO primitives (NOT Entity objects)
                assertEquals("CSC207", outputData.getCourseCode());
                assertEquals("Software Design", outputData.getCourseName());
                assertEquals("F", outputData.getTerm());

                // Verify Ratings (Directly from DTO)
                assertEquals(4.5f, outputData.getRecommendation());
                assertEquals(3.0f, outputData.getWorkload());

                // Verify Section DTOs
                assertFalse(outputData.getSections().isEmpty());
                ViewCourseOutputData.SectionData section = outputData.getSections().get(0);
                assertEquals("LEC0101", section.getSectionId());
                assertEquals("Prof. Smith", section.getInstructors().get(0));

                // Verify TimeSlot DTOs
                assertFalse(section.getTimeSlots().isEmpty());
                assertEquals("Monday", section.getTimeSlots().get(0).getDay());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Should not reach fail view");
            }
        };

        // Act
        ViewCourseInteractor interactor = new ViewCourseInteractor(courseDao, ratingDao, successPresenter);
        interactor.execute(inputData);
    }

    @Test
    public void successTestNoRatings() {
        // Arrange
        ViewCourseInputData inputData = new ViewCourseInputData("CSC207");

        CourseDataAccessInterface courseDao = new CourseDataAccessInterface() {
            @Override
            public Course findByCourseCode(String code) { return createTestCourse(code); }
            @Override
            public List<Course> getAllCourses() { return null; }
        };

        CourseEvalDataReader ratingDao = new CourseEvalDataReader(createDummyFile()) {
            @Override
            public Rating getRating(String courseCode) { return null; }
        };

        ViewCourseOutputBoundary successPresenter = new ViewCourseOutputBoundary() {
            @Override
            public void prepareSuccessView(ViewCourseOutputData outputData) {
                assertEquals("CSC207", outputData.getCourseCode());
                // Ratings should be null
                assertNull(outputData.getRecommendation());
                assertNull(outputData.getWorkload());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Should not reach fail view");
            }
        };

        // Act
        ViewCourseInteractor interactor = new ViewCourseInteractor(courseDao, ratingDao, successPresenter);
        interactor.execute(inputData);
    }

    @Test
    public void failureTestCourseNotFound() {
        // Arrange
        ViewCourseInputData inputData = new ViewCourseInputData("INVALID_CODE");

        CourseDataAccessInterface courseDao = new CourseDataAccessInterface() {
            @Override
            public Course findByCourseCode(String code) { return null; }
            @Override
            public List<Course> getAllCourses() { return null; }
        };

        CourseEvalDataReader ratingDao = new CourseEvalDataReader(createDummyFile());

        ViewCourseOutputBoundary failPresenter = new ViewCourseOutputBoundary() {
            @Override
            public void prepareSuccessView(ViewCourseOutputData outputData) {
                fail("Should not reach success view");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("Course not found: INVALID_CODE", error);
            }
        };

        // Act
        ViewCourseInteractor interactor = new ViewCourseInteractor(courseDao, ratingDao, failPresenter);
        interactor.execute(inputData);
    }
}
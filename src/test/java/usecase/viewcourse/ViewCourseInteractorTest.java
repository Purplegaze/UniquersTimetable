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

    // --- Helper to create a dummy Course entity with classes on ALL days ---
    private Course createTestCourse(String code) {
        Building building = new Building("BA", "Bahen", 0.0, 0.0);
        List<Section> sections = new ArrayList<>();

        // 1. Create Course
        Course course = new Course(code, "Software Design", "Software", 0.5f, "F", sections, building, 1);

        // 2. Create TimeSlots for EVERY day of the week (1-7)
        List<TimeSlot> times = new ArrayList<>();
        for (int day = 1; day <= 7; day++) {
            times.add(new TimeSlot(day, LocalTime.of(10, 0), LocalTime.of(11, 0), building));
        }

        // 3. Create Section
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
                // Verify DTO primitives
                assertEquals("CSC207", outputData.getCourseCode());
                assertEquals("Software Design", outputData.getCourseName());
                assertEquals("F", outputData.getTerm());

                // Verify Ratings
                assertEquals(4.5f, outputData.getRecommendation());
                assertEquals(3.0f, outputData.getWorkload());

                // Verify Section DTOs
                assertFalse(outputData.getSections().isEmpty());
                ViewCourseOutputData.SectionData section = outputData.getSections().get(0);
                assertEquals("LEC0101", section.getSectionId());
                assertEquals("Prof. Smith", section.getInstructors().get(0));
                assertEquals(50, section.getEnrolled());
                assertEquals(100, section.getCapacity());
                assertFalse(section.isFull());

                // Verify TimeSlot DTOs
                // We expect 7 time slots (Monday to Sunday)
                List<ViewCourseOutputData.TimeSlotData> slots = section.getTimeSlots();
                assertEquals(7, slots.size());

                // Check a few to ensure conversion worked and hit getters
                assertEquals("Monday", slots.get(0).getDay());
                assertEquals("Wednesday", slots.get(2).getDay());
                assertEquals("Sunday", slots.get(6).getDay());

                assertEquals(10, slots.get(0).getStart());
                assertEquals(11, slots.get(0).getEnd());
                assertEquals("BA", slots.get(0).getLocation());
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

    @Test
    public void testUnknownDayCoverage() {
        // Create basic entities
        Building building = new Building("BA", "Bahen", 0.0, 0.0);
        List<Section> sections = new ArrayList<>();
        Course course = new Course("CSC999", "Edge Case Course", "Desc", 0.5f, "F", sections, building, 1);

        // Create a Subclassed TimeSlot
        // We pass valid arguments to the constructor (day 1) to pass the check,
        // BUT we override getDayOfWeek() to return 8.
        TimeSlot invalidSlot = new TimeSlot(1, LocalTime.of(10, 0), LocalTime.of(11, 0), building) {
            @Override
            public int getDayOfWeek() {
                return 8; // This forces the switch to go to 'default'
            }
        };

        List<TimeSlot> times = new ArrayList<>();
        times.add(invalidSlot);

        List<String> instructors = new ArrayList<>();
        Section section = new Section("LEC9999", times, 50, instructors, 100, course);
        sections.add(section);

        // Mock DAO to return our special course
        CourseDataAccessInterface courseDao = new CourseDataAccessInterface() {
            @Override
            public Course findByCourseCode(String code) { return course; }
            @Override
            public List<Course> getAllCourses() { return null; }
        };

        CourseEvalDataReader ratingDao = new CourseEvalDataReader(createDummyFile());

        // Presenter to verify Unknown
        ViewCourseOutputBoundary presenter = new ViewCourseOutputBoundary() {
            @Override
            public void prepareSuccessView(ViewCourseOutputData outputData) {
                // Verify that the 'default' case returned "Unknown"
                String dayName = outputData.getSections().get(0).getTimeSlots().get(0).getDay();
                assertEquals("Unknown", dayName);
            }

            @Override
            public void prepareFailView(String error) {
                fail("Should not fail");
            }
        };

        // 5. Act
        ViewCourseInteractor interactor = new ViewCourseInteractor(courseDao, ratingDao, presenter);
        interactor.execute(new ViewCourseInputData("CSC999"));
    }
}
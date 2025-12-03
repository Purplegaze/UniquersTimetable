package usecase.export;

import data_access.CourseDataAccessInterface;
import data_access.ExportDataAccess;
import data_access.TimetableDataAccessInterface;
import entity.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Test class for all parts of the export timetable use case.
 * Tests the interactor, as well as the data access itself and its methods.
 */
public class ExportTimetableInteractorTest {
    final static String TEMP_LOCATION =
            System.getProperty("user.dir") + "/src/test/java/usecase/export/tempfiles/exportTest.json";

    final int SUCCESS_VALUE = 1;
    final int CANCEL_VALUE = 2;
    final int ERROR_VALUE = 3;

    final static ExportDataAccess exportDataAccess = new ExportDataAccess();

    /**
     * Data access tests
     */
    @Test
    void TestExportBlank() throws IOException {
        List<Section> noSections = new ArrayList<>();

        JSONObject jsonObject = getJsonWithTempLocation(exportDataAccess, noSections);

        JSONObject correctObject = new JSONObject();

        assertEquals(jsonObject.toString(), correctObject.toString());

    }
    @Test
    void TestExportTwoCourses() throws IOException {
        {
            // Create two course objects in accordance with the relevant fields in Section and Course
            List<Section> course1Sections = new ArrayList<>();
            List<Section> course2Sections = new ArrayList<>();
            Course course1 = new Course(
                    "CSC110",
                    "Foundations of CS 1",
                    "The best course ever",
                    1.0f,
                    "F",
                    course1Sections,
                    null,
                    5
            );
            Course course2 = new Course(
                    "ENG100",
                    "Psychological Torture",
                    "The worst course ever",
                    0.5f,
                    "F",
                    course2Sections,
                    null,
                    1
            );
            Section section1 = new Section(
                    "LEC0101",
                    new ArrayList<>(),
                    0,
                    new ArrayList<>(),
                    0,
                    course1
            );
            Section section2 = new Section(
                    "TUT0101",
                    new ArrayList<>(),
                    0,
                    new ArrayList<>(),
                    0,
                    course1
            );
            Section section3 = new Section(
                    "LEC5101",
                    new ArrayList<>(),
                    0,
                    new ArrayList<>(),
                    0,
                    course2
            );
            course1Sections.add(section1);
            course1Sections.add(section2);
            course2Sections.add(section3);

            // Create a sectionsInTimetable object to pass into the export interactor
            List<Section> sections = new ArrayList<>();
            sections.add(section1);
            sections.add(section2);
            sections.add(section3);

            JSONObject jsonObject = getJsonWithTempLocation(exportDataAccess, sections);

            JSONObject correctObject = new JSONObject();
            JSONArray csc110CorrectSections = new JSONArray()
                    .put("LEC0101")
                    .put("TUT0101");
            JSONArray eng100CorrectSections = new JSONArray()
                    .put("LEC5101");
            correctObject.put("CSC110", csc110CorrectSections);
            correctObject.put("ENG100", eng100CorrectSections);

            assertEquals(jsonObject.toString(), correctObject.toString());
        }
    }


    /**
     * Run exportDataAccess.save(), but return it as a JSONObject instead of simply saving the file.
     * Allows testing if the saved JSON matches expectations.
     * @param exportDataAccess an ExportDataAccess object
     * @param sectionsInTimetable the list of sections to export
     * @return the exported JSON file, as a JSONObject
     */
    public static JSONObject getJsonWithTempLocation(ExportDataAccess exportDataAccess, List<Section> sectionsInTimetable) {
        try {
            exportDataAccess.save(TEMP_LOCATION, sectionsInTimetable);
            String jsonString = Files.readString(Paths.get(TEMP_LOCATION));
            JSONObject jsonObject = new JSONObject(jsonString);
            deleteTempFile();
            return jsonObject;
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean deleteTempFile() throws IOException {
        try {
            Files.delete(Paths.get(TEMP_LOCATION));
            return true;
        }
        catch (NoSuchFileException e) {
            return false;
        }
    }

    /**
     * Test that the right presenter function is being executed when the interactor executes.
     *
     * Success happens whenever a valid filepath is provided.
     * Cancelled happens whenever null is provided as the filepath.
     */
    @Test
    void TestPresenterSuccessful() throws IOException {
        DummyPresenter dummyPresenter = new DummyPresenter();
        ExportTimetableInteractor interactor = new ExportTimetableInteractor(
                dummyPresenter,
                new InMemoryTimetableDataAccess(),
                exportDataAccess
        );

        ExportTimetableInputData inputData = new ExportTimetableInputData(TEMP_LOCATION);
        deleteTempFile();
        interactor.execute(inputData);

        assertEquals(dummyPresenter.getStatus(), SUCCESS_VALUE);

    }
    @Test
    void TestPresenterCancelled() throws IOException {
        DummyPresenter dummyPresenter = new DummyPresenter();
        ExportTimetableInteractor interactor = new ExportTimetableInteractor(
                dummyPresenter,
                new InMemoryTimetableDataAccess(),
                exportDataAccess
        );

        ExportTimetableInputData inputData = new ExportTimetableInputData(null);
        interactor.execute(inputData);

        assertEquals(dummyPresenter.getStatus(), CANCEL_VALUE);

    }
    @Test
    void TestPresenterError() throws IOException {
        DummyPresenter dummyPresenter = new DummyPresenter();
        ExportTimetableInteractor interactor = new ExportTimetableInteractor(
                dummyPresenter,
                new InMemoryTimetableDataAccess(),
                new FaultyExportDataAccess()
        );

        ExportTimetableInputData inputData = new ExportTimetableInputData(TEMP_LOCATION);
        deleteTempFile();
        interactor.execute(inputData);

        assertEquals(dummyPresenter.getStatus(), ERROR_VALUE);

    }

    /**
     * Test interactor null dependency exception for each parameter
     */
    @Test
    void TestNullDependencies() {
        assertThrows(IllegalArgumentException.class, () -> new ExportTimetableInteractor(
                new DummyPresenter(),
                null,
                exportDataAccess
        ));
        assertThrows(IllegalArgumentException.class, () -> new ExportTimetableInteractor(
                null,
                new InMemoryTimetableDataAccess(),
                exportDataAccess
        ));
        assertThrows(IllegalArgumentException.class, () -> new ExportTimetableInteractor(
                new DummyPresenter(),
                new InMemoryTimetableDataAccess(),
                null
        ));

    }


    // ===============================================================
    // Helper classes for testing.
    // ===============================================================

    public class DummyPresenter implements ExportTimetableOutputBoundary {
        private int status;
        // Status integer to detect what presenter method was called.

        public int getStatus() {
            return status;
        }

        @Override
        public void presentSuccess(ExportTimetableOutputData outputData) {
            this.status = SUCCESS_VALUE;
        }

        @Override
        public void presentCancel() {
            this.status = CANCEL_VALUE;
        }

        @Override
        public void presentError(String errorMessage) {
            this.status = ERROR_VALUE;
        }
    }

    private static class InMemoryTimetableDataAccess implements TimetableDataAccessInterface {
        private final Timetable timetable = new Timetable();
        private String currentTerm = null;
        private boolean addSectionResult = true;
        private boolean hasConflicts = false;

        public void setCurrentTerm(String term) { this.currentTerm = term; }
        public void setAddSectionResult(boolean result) { this.addSectionResult = result; }
        public void setHasConflicts(boolean hasConflicts) { this.hasConflicts = hasConflicts; }

        @Override
        public boolean addSection(Section section) {
            if (!addSectionResult) return false;
            timetable.addSectionOfNewCourse(section);
            if (currentTerm == null) currentTerm = section.getCourse().getTerm();
            return true;
        }

        @Override
        public boolean hasSection(Section section) {
            for (TimetableBlock block : timetable.getBlocks()) {
                if (block.getSection().equals(section)) return true;
            }
            return false;
        }

        @Override
        public boolean hasConflicts(Section section) { return hasConflicts; }

        @Override
        public String getCurrentTerm() { return currentTerm; }

        @Override
        public List<Section> getAllSections() {
            List<Section> sections = new ArrayList<>();
            for (TimetableBlock block : timetable.getBlocks()) {
                Section section = block.getSection();
                if (!sections.contains(section)) sections.add(section);
            }
            return sections;
        }

        @Override
        public boolean removeSection(Section section) {
            timetable.removeSection(section);
            return true;
        }

        @Override
        public Timetable getTimetable() { return timetable; }

        @Override
        public void clear() {
            for (Course course : new ArrayList<>(timetable.getCourses())) timetable.removeCourse(course);
            currentTerm = null;
        }
    }


    private static class InMemoryCourseDataAccess implements CourseDataAccessInterface {
        private final List<Course> courses = new ArrayList<>();

        public InMemoryCourseDataAccess() {
            Building ba = new Building("BA", "Bahen", 43.0, 79.0);

            List<Section> csc207Sections = new ArrayList<>();
            Course csc207 = new Course("CSC207", "Software Design", "Intro", 0.5f, "F", csc207Sections, ba, 5);
            Section csc207Sec = new Section("LEC0101", List.of(new TimeSlot(1, LocalTime.of(10, 0), LocalTime.of(12, 0), ba)), 50, List.of("Dr. Smith"), 100, csc207);
            csc207.addSection(csc207Sec);
            courses.add(csc207);

            List<Section> csc236Sections = new ArrayList<>();
            Course csc236 = new Course("CSC236", "Theory", "Theory", 0.5f, "S", csc236Sections, ba, 5);
            Section csc236Sec = new Section("LEC0101", List.of(new TimeSlot(1, LocalTime.of(10, 0), LocalTime.of(12, 0), ba)), 50, List.of("Dr. Winter"), 100, csc236);
            csc236.addSection(csc236Sec);
            courses.add(csc236);

            List<Section> mat137Sections = new ArrayList<>();
            Course mat137 = new Course("MAT137", "Calculus", "Year-long", 1.0f, "Y", mat137Sections, ba, 5);
            Section mat137Sec = new Section("LEC0101", List.of(new TimeSlot(1, LocalTime.of(10, 0), LocalTime.of(12, 0), ba)), 50, List.of("Dr. Math"), 100, mat137);
            mat137.addSection(mat137Sec);
            courses.add(mat137);
        }

        @Override
        public Course findByCourseCode(String courseCode) {
            for (Course course : courses) {
                if (course.getCourseCode().equals(courseCode)) return course;
            }
            return null;
        }

        @Override
        public List<Course> getAllCourses() { return new ArrayList<>(courses); }
    }

    private static class FaultyExportDataAccess implements ExportTimetableDataAccessInterface {

        @Override
        public void save(String filepath, List<Section> sections) {
            throw new RuntimeException("DB error");
        }
    }

    private static class FaultyTimetableDataAccess implements TimetableDataAccessInterface {
        @Override
        public boolean addSection(Section section) { throw new RuntimeException("DB error"); }

        @Override
        public boolean hasSection(Section section) { throw new RuntimeException("DB error"); }

        @Override
        public boolean hasConflicts(Section section) { throw new RuntimeException("DB error"); }

        @Override
        public String getCurrentTerm() { throw new RuntimeException("DB error"); }

        @Override
        public List<Section> getAllSections() { throw new RuntimeException("DB error"); }

        @Override
        public boolean removeSection(Section section) { throw new RuntimeException("DB error"); }

        @Override
        public Timetable getTimetable() { throw new RuntimeException("DB error"); }

        @Override
        public void clear() { throw new RuntimeException("DB error"); }
    }

}


package usecase.importsections;

import data_access.CourseDataAccessInterface;
import data_access.ImportDataAccess;
import data_access.TimetableDataAccessInterface;
import entity.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ImportTimetableInteractorTest {
    final static String EXP_LOCATION_PATH =
            System.getProperty("user.dir") + "/src/test/java/usecase/importsections/testexports/";

    final int SUCCESS_VALUE = 1;
    final int CANCEL_VALUE = 2;
    final int ERROR_VALUE = 3;

    public static ImportDataAccess importDataAccess = new ImportDataAccess();

    /**
     * Test all branches of the presenter flow are executed depending on the imported data
     */
    @Test
    void TestPresenterSuccessful() {
        DummyPresenter dummyPresenter = new DummyPresenter();
        ImportTimetableInputData inputData = new ImportTimetableInputData(
                EXP_LOCATION_PATH + "timetableInCourse.json" // Contains courses present in InCourseDataAccess.
        );
        ImportTimetableInteractor interactor = new ImportTimetableInteractor(
                dummyPresenter,
                new InMemoryTimetableDataAccess(),
                new InMemoryCourseDataAccess(),
                importDataAccess
        );
        interactor.execute(inputData);
        assertEquals(dummyPresenter.getStatus(), SUCCESS_VALUE);
    }
    @Test
    void TestPresenterCancelled() {
        DummyPresenter dummyPresenter = new DummyPresenter();
        ImportTimetableInputData inputData = new ImportTimetableInputData(
                null // Filepath = null if and only if user cancelled the import
        );
        ImportTimetableInteractor interactor = new ImportTimetableInteractor(
                dummyPresenter,
                new InMemoryTimetableDataAccess(),
                new InMemoryCourseDataAccess(),
                importDataAccess
        );
        interactor.execute(inputData);
        assertEquals(dummyPresenter.getStatus(), CANCEL_VALUE);
    }
    @Test
    void TestPresenterError() {
        DummyPresenter dummyPresenter = new DummyPresenter();
        ImportTimetableInputData inputData = new ImportTimetableInputData(
                EXP_LOCATION_PATH + "timetableInCourse.json"
        );
        ImportTimetableInteractor interactor = new ImportTimetableInteractor(
                dummyPresenter,
                new FaultyTimetableDataAccess(), // Timetable data access throws error.
                new InMemoryCourseDataAccess(),
                importDataAccess
        );
        interactor.execute(inputData);
        assertEquals(dummyPresenter.getStatus(), ERROR_VALUE);
    }

    @Test
    void TestCourseNotFoundDetected() {
        DummyPresenter dummyPresenter = new DummyPresenter();
        ImportTimetableInputData inputData = new ImportTimetableInputData(
                EXP_LOCATION_PATH + "timetableNotInCourse.json" // Contains a course not present in InCourseDataAccess.
        );
        ImportTimetableInteractor interactor = new ImportTimetableInteractor(
                dummyPresenter,
                new InMemoryTimetableDataAccess(),
                new InMemoryCourseDataAccess(),
                importDataAccess
        );
        interactor.execute(inputData);
        assertEquals(dummyPresenter.getStatus(), SUCCESS_VALUE);
        assertEquals("CSC375 LEC0101", dummyPresenter.getNotFoundString());
    }

    /**
     * Test interactor null dependency exception for each parameter
     */
    @Test
    void TestNullDependencies() {
        assertThrows(IllegalArgumentException.class, () -> new ImportTimetableInteractor(
                new DummyPresenter(),
                null,
                new InMemoryCourseDataAccess(),
                importDataAccess
        ));
        assertThrows(IllegalArgumentException.class, () -> new ImportTimetableInteractor(
                null,
                new InMemoryTimetableDataAccess(),
                new InMemoryCourseDataAccess(),
                importDataAccess
        ));
        assertThrows(IllegalArgumentException.class, () -> new ImportTimetableInteractor(
                new DummyPresenter(),
                new InMemoryTimetableDataAccess(),
                null,
                importDataAccess
        ));
        assertThrows(IllegalArgumentException.class, () -> new ImportTimetableInteractor(
                new DummyPresenter(),
                new InMemoryTimetableDataAccess(),
                new InMemoryCourseDataAccess(),
                null
        ));

    }

    // ===============================================================
    // Helper classes for testing.
    // ===============================================================

    public class DummyPresenter implements ImportTimetableOutputBoundary {
        // Status integer to detect what presenter method was called.
        private int status;

        private String notFoundString;

        public int getStatus() {
            return status;
        }

        public String getNotFoundString() {
            return notFoundString;
        }

        @Override
        public void presentSuccess(ImportTimetableOutputData outputData) {
            this.status = SUCCESS_VALUE;
            this.notFoundString = outputData.getNotFoundString();
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
            Section csc236Sec = new Section("LEC0101", List.of(new TimeSlot(3, LocalTime.of(10, 0), LocalTime.of(12, 0), ba)), 50, List.of("Dr. Winter"), 100, csc236);
            csc236.addSection(csc236Sec);
            courses.add(csc236);

            List<Section> mat137Sections = new ArrayList<>();
            Course mat137 = new Course("MAT137", "Calculus", "Year-long", 1.0f, "Y", mat137Sections, ba, 5);
            Section mat137Sec = new Section("LEC0101", List.of(new TimeSlot(1, LocalTime.of(10, 0), LocalTime.of(12, 0), ba)), 50, List.of("Dr. Math"), 100, mat137);
            mat137.addSection(mat137Sec);
            Section mat137Tut = new Section("TUT6901", List.of(new TimeSlot(2, LocalTime.of(10, 0), LocalTime.of(12, 0), ba)), 50, List.of("Dr. Math"), 100, mat137);
            mat137.addSection(mat137Tut);
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

    private static class FaultyImportDataAccess implements ImportTimetableDataAccessInterface {

        @Override
        public ImportDataObject open(String filepath) {
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

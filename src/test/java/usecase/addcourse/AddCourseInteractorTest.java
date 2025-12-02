package usecase.addcourse;

import data_access.CourseDataAccessInterface;
import data_access.TimetableDataAccessInterface;
import entity.*;
import interface_adapter.addcourse.AddCourseController.TimeSlotData;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AddCourseInteractorTest {

    @Test
    void successTest() {
        executeTest("CSC207", "LEC0101", "Monday", 10, 12, createSuccessPresenter());
    }

    @Test
    void successTest_NullInstructor() {
        AddCourseOutputBoundary presenter = new AddCourseOutputBoundary() {
            @Override
            public void presentSuccess(AddCourseOutputData outputData) {
                assertEquals("TBA", outputData.getInstructor());
            }

            @Override
            public void presentConflict(AddCourseOutputData outputData) { fail("Unexpected conflict."); }

            @Override
            public void presentError(String errorMessage) { fail("Unexpected error."); }
        };

        executeTestWithInstructor(null, "Tuesday", 14, 16, presenter);
    }

    @Test
    void successTest_EmptyInstructor() {
        AddCourseOutputBoundary presenter = new AddCourseOutputBoundary() {
            @Override
            public void presentSuccess(AddCourseOutputData outputData) {
                assertEquals("TBA", outputData.getInstructor());
            }

            @Override
            public void presentConflict(AddCourseOutputData outputData) { fail("Unexpected conflict."); }

            @Override
            public void presentError(String errorMessage) { fail("Unexpected error."); }
        };

        executeTestWithInstructor("   ", "Wednesday", 9, 11, presenter);
    }

    @Test
    void successTest_YearLongCourse() {
        InMemoryTimetableDataAccess timetableDataAccess = new InMemoryTimetableDataAccess();
        timetableDataAccess.setCurrentTerm("F");

        executeTestWithTimetable(timetableDataAccess, "MAT137", "LEC0101", "Dr. Math", "Thursday", 10, 12, createSuccessPresenter());
    }

    @Test
    void successTest_EmptyTimetable() {
        executeTest("CSC207", "LEC0101", "Friday", 13, 15, createSuccessPresenter());
    }

    @Test
    void successTest_Saturday() {
        executeTest("CSC207", "LEC0101", "Saturday", 10, 12, createSuccessPresenter());
    }

    @Test
    void successTest_Sunday() {
        executeTest("CSC207", "LEC0101", "Sunday", 14, 16, createSuccessPresenter());
    }

    @Test
    void failureTest_NullInput() {
        AddCourseOutputBoundary presenter = createErrorPresenter("Invalid input: No data provided");
        new AddCourseInteractor(new InMemoryTimetableDataAccess(), new InMemoryCourseDataAccess(), presenter).execute(null);
    }

    @Test
    void failureTest_CourseNotFound() {
        executeTest("CSC999", "LEC0101", "Monday", 10, 12, createErrorPresenter("Course not found: CSC999"));
    }

    @Test
    void failureTest_SectionNotFound() {
        AddCourseOutputBoundary presenter = new AddCourseOutputBoundary() {
            @Override
            public void presentSuccess(AddCourseOutputData outputData) { fail("Unexpected success."); }

            @Override
            public void presentConflict(AddCourseOutputData outputData) { fail("Unexpected conflict."); }

            @Override
            public void presentError(String errorMessage) {
                assertTrue(errorMessage.contains("Section LEC9999 not found"));
            }
        };

        executeTest("CSC207", "LEC9999", "Monday", 10, 12, presenter);
    }

    @Test
    void failureTest_TermIncompatibility_FallToWinter() {
        InMemoryTimetableDataAccess timetableDataAccess = new InMemoryTimetableDataAccess();
        timetableDataAccess.setCurrentTerm("F");

        AddCourseOutputBoundary presenter = new AddCourseOutputBoundary() {
            @Override
            public void presentSuccess(AddCourseOutputData outputData) { fail("Unexpected success."); }

            @Override
            public void presentConflict(AddCourseOutputData outputData) { fail("Unexpected conflict."); }

            @Override
            public void presentError(String errorMessage) {
                assertTrue(errorMessage.contains("Cannot mix terms"));
            }
        };

        executeTestWithTimetable(timetableDataAccess, "CSC236", "LEC0101", "Dr. Winter", "Monday", 10, 12, presenter);
    }

    @Test
    void failureTest_TermIncompatibility_WinterToFall() {
        InMemoryTimetableDataAccess timetableDataAccess = new InMemoryTimetableDataAccess();
        timetableDataAccess.setCurrentTerm("S");

        AddCourseOutputBoundary presenter = new AddCourseOutputBoundary() {
            @Override
            public void presentSuccess(AddCourseOutputData outputData) { fail("Unexpected success."); }

            @Override
            public void presentConflict(AddCourseOutputData outputData) { fail("Unexpected conflict."); }

            @Override
            public void presentError(String errorMessage) {
                assertTrue(errorMessage.contains("Cannot mix terms"));
            }
        };

        executeTestWithTimetable(timetableDataAccess, "CSC207", "LEC0101", "Dr. Fall", "Monday", 10, 12, presenter);
    }

    @Test
    void failureTest_DuplicateSection() {
        InMemoryTimetableDataAccess timetableDataAccess = new InMemoryTimetableDataAccess();
        InMemoryCourseDataAccess courseDataAccess = new InMemoryCourseDataAccess();

        List<TimeSlotData> timeSlots = List.of(new TimeSlotData("Monday", 10, 12, "BA"));
        AddCourseInputData inputData = new AddCourseInputData("CSC207", "LEC0101", "Dr. Smith", timeSlots);

        new AddCourseInteractor(timetableDataAccess, courseDataAccess, createSuccessPresenter()).execute(inputData);

        AddCourseOutputBoundary presenter = new AddCourseOutputBoundary() {
            @Override
            public void presentSuccess(AddCourseOutputData outputData) { fail("Unexpected success."); }

            @Override
            public void presentConflict(AddCourseOutputData outputData) { fail("Unexpected conflict."); }

            @Override
            public void presentError(String errorMessage) {
                assertTrue(errorMessage.contains("already in your timetable"));
            }
        };

        new AddCourseInteractor(timetableDataAccess, courseDataAccess, presenter).execute(inputData);
    }

    @Test
    void failureTest_AddFails() {
        InMemoryTimetableDataAccess timetableDataAccess = new InMemoryTimetableDataAccess();
        timetableDataAccess.setAddSectionResult(false);

        executeTestWithTimetable(timetableDataAccess, "CSC207", "LEC0101", "Dr. Smith", "Monday", 10, 12,
                createErrorPresenter("Failed to add section to timetable"));
    }

    @Test
    void failureTest_InvalidTimeRange() {
        AddCourseOutputBoundary presenter = new AddCourseOutputBoundary() {
            @Override
            public void presentSuccess(AddCourseOutputData outputData) { fail("Unexpected success."); }

            @Override
            public void presentConflict(AddCourseOutputData outputData) { fail("Unexpected conflict."); }

            @Override
            public void presentError(String errorMessage) {
                assertTrue(errorMessage.contains("Error adding course"));
            }
        };

        executeTest("CSC207", "LEC0101", "Monday", 12, 10, presenter);
    }

    @Test
    void failureTest_InvalidDay() {
        AddCourseOutputBoundary presenter = new AddCourseOutputBoundary() {
            @Override
            public void presentSuccess(AddCourseOutputData outputData) { fail("Unexpected success."); }

            @Override
            public void presentConflict(AddCourseOutputData outputData) { fail("Unexpected conflict."); }

            @Override
            public void presentError(String errorMessage) {
                assertTrue(errorMessage.contains("Error adding course"));
            }
        };

        executeTest("CSC207", "LEC0101", "InvalidDay", 10, 12, presenter);
    }

    @Test
    void failureTest_Exception() {
        AddCourseOutputBoundary presenter = new AddCourseOutputBoundary() {
            @Override
            public void presentSuccess(AddCourseOutputData outputData) { fail("Unexpected success."); }

            @Override
            public void presentConflict(AddCourseOutputData outputData) { fail("Unexpected conflict."); }

            @Override
            public void presentError(String errorMessage) {
                assertTrue(errorMessage.contains("Error adding course"));
            }
        };

        List<TimeSlotData> timeSlots = List.of(new TimeSlotData("Monday", 10, 12, "BA"));
        AddCourseInputData inputData = new AddCourseInputData("CSC207", "LEC0101", "Dr. Smith", timeSlots);

        new AddCourseInteractor(new FaultyTimetableDataAccess(), new InMemoryCourseDataAccess(), presenter).execute(inputData);
    }

    @Test
    void failureTest_TimeConflict() {
        InMemoryTimetableDataAccess timetableDataAccess = new InMemoryTimetableDataAccess();
        timetableDataAccess.setHasConflicts(true);

        AddCourseOutputBoundary presenter = new AddCourseOutputBoundary() {
            @Override
            public void presentSuccess(AddCourseOutputData outputData) { fail("Unexpected success."); }

            @Override
            public void presentConflict(AddCourseOutputData outputData) { fail("Unexpected conflict."); }

            @Override
            public void presentError(String errorMessage) {
                assertTrue(errorMessage.contains("Time conflict"));
            }
        };

        executeTestWithTimetable(timetableDataAccess, "CSC207", "LEC0101", "Dr. Smith", "Monday", 10, 12, presenter);
    }

    @Test
    void constructorTest_NullDependencies() {
        assertThrows(IllegalArgumentException.class, () -> new AddCourseInteractor(null, new InMemoryCourseDataAccess(), createSuccessPresenter()));
        assertThrows(IllegalArgumentException.class, () -> new AddCourseInteractor(new InMemoryTimetableDataAccess(), null, createSuccessPresenter()));
        assertThrows(IllegalArgumentException.class, () -> new AddCourseInteractor(new InMemoryTimetableDataAccess(), new InMemoryCourseDataAccess(), null));
    }

    // Helper methods
    private void executeTest(String courseCode, String sectionCode, String day, int startHour, int endHour, AddCourseOutputBoundary presenter) {
        executeTestWithTimetable(new InMemoryTimetableDataAccess(), courseCode, sectionCode, "Boris", day, startHour, endHour, presenter);
    }

    private void executeTestWithInstructor(String instructor, String day, int startHour, int endHour, AddCourseOutputBoundary presenter) {
        executeTestWithTimetable(new InMemoryTimetableDataAccess(), "CSC207", "LEC0101", instructor, day, startHour, endHour, presenter);
    }

    private void executeTestWithTimetable(InMemoryTimetableDataAccess timetableDataAccess, String courseCode, String sectionCode, String instructor, String day, int startHour, int endHour, AddCourseOutputBoundary presenter) {
        List<TimeSlotData> timeSlots = List.of(new TimeSlotData(day, startHour, endHour, "BA"));
        AddCourseInputData inputData = new AddCourseInputData(courseCode, sectionCode, instructor, timeSlots);
        new AddCourseInteractor(timetableDataAccess, new InMemoryCourseDataAccess(), presenter).execute(inputData);
    }

    private AddCourseOutputBoundary createSuccessPresenter() {
        return new AddCourseOutputBoundary() {
            @Override
            public void presentSuccess(AddCourseOutputData outputData) {}

            @Override
            public void presentConflict(AddCourseOutputData outputData) { fail("Unexpected conflict."); }

            @Override
            public void presentError(String errorMessage) { fail("Unexpected error: " + errorMessage); }
        };
    }

    private AddCourseOutputBoundary createErrorPresenter(String expectedError) {
        return new AddCourseOutputBoundary() {
            @Override
            public void presentSuccess(AddCourseOutputData outputData) { fail("Unexpected success."); }

            @Override
            public void presentConflict(AddCourseOutputData outputData) { fail("Unexpected conflict."); }

            @Override
            public void presentError(String errorMessage) {
                assertEquals(expectedError, errorMessage);
            }
        };
    }

    // Helper classes
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
package usecase.deletesection;

import data_access.TimetableDataAccessInterface;
import entity.*;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeleteSectionInteractorTest {

    @Test
    void successTest() {
        InMemoryTimetableDataAccess timetableDataAccess = new InMemoryTimetableDataAccess();
        timetableDataAccess.addTestSection("CSC207", "LEC0101");

        DeleteSectionOutputBoundary presenter = new DeleteSectionOutputBoundary() {
            @Override
            public void presentSuccess(DeleteSectionOutputData outputData) {
                assertEquals("CSC207", outputData.getDeletedCourseCode());
            }

            @Override
            public void presentError(String errorMessage) {
                fail("Unexpected error.");
            }
        };

        new DeleteSectionInteractor(timetableDataAccess, presenter).execute(new DeleteSectionInputData("CSC207", "LEC0101"));
    }

    @Test
    void failureTest_SectionNotFound() {
        DeleteSectionOutputBoundary presenter = createErrorPresenter();
        new DeleteSectionInteractor(new InMemoryTimetableDataAccess(), presenter).execute(new DeleteSectionInputData("CSC999", "LEC9999"));
    }

    @Test
    void failureTest_DeleteFails() {
        InMemoryTimetableDataAccess timetableDataAccess = new InMemoryTimetableDataAccess();
        timetableDataAccess.addTestSection("CSC207", "LEC0101");
        timetableDataAccess.setRemoveSectionResult(false);

        DeleteSectionOutputBoundary presenter = createErrorPresenter();
        new DeleteSectionInteractor(timetableDataAccess, presenter).execute(new DeleteSectionInputData("CSC207", "LEC0101"));
    }

    @Test
    void failureTest_Exception() {
        DeleteSectionOutputBoundary presenter = createErrorPresenter();
        new DeleteSectionInteractor(new FaultyTimetableDataAccess(), presenter).execute(new DeleteSectionInputData("CSC207", "LEC0101"));
    }

    @Test
    void constructorTest_NullDependencies() {
        DeleteSectionOutputBoundary presenter = new DeleteSectionOutputBoundary() {
            @Override
            public void presentSuccess(DeleteSectionOutputData outputData) {}
            @Override
            public void presentError(String errorMessage) {}
        };

        assertThrows(IllegalArgumentException.class, () -> new DeleteSectionInteractor(null, presenter));
        assertThrows(IllegalArgumentException.class, () -> new DeleteSectionInteractor(new InMemoryTimetableDataAccess(), null));
    }

    private DeleteSectionOutputBoundary createErrorPresenter() {
        return new DeleteSectionOutputBoundary() {
            @Override
            public void presentSuccess(DeleteSectionOutputData outputData) {
                fail("Unexpected success.");
            }

            @Override
            public void presentError(String errorMessage) {
                assertNotNull(errorMessage);
            }
        };
    }

    // helper classes

    private static class InMemoryTimetableDataAccess implements TimetableDataAccessInterface {
        private final Timetable timetable = new Timetable();
        private final List<Section> sections = new ArrayList<>();
        private boolean removeSectionResult = true;

        public void addTestSection(String courseCode, String sectionCode) {
            Building building = new Building("BA", "Bahen", 43.0, 79.0);
            List<Section> sectionsList = new ArrayList<>();
            Course course = new Course(courseCode, "Test", "Desc", 0.5f, "F", sectionsList, building, 5);
            Section section = new Section(sectionCode, List.of(new TimeSlot(1, LocalTime.of(10, 0), LocalTime.of(12, 0), building)), 50, List.of("Dr. Test"), 100, course);
            course.addSection(section);
            sections.add(section);
            timetable.addSectionOfNewCourse(section);
        }

        public void setRemoveSectionResult(boolean result) { this.removeSectionResult = result; }

        @Override
        public boolean addSection(Section section) {
            timetable.addSectionOfNewCourse(section);
            sections.add(section);
            return true;
        }

        @Override
        public boolean removeSection(Section section) {
            if (!removeSectionResult) return false;
            timetable.removeSection(section);
            sections.remove(section);
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
        public List<Section> getAllSections() { return new ArrayList<>(sections); }

        @Override
        public String getCurrentTerm() { return "F"; }

        @Override
        public boolean hasConflicts(Section section) { return false; }

        @Override
        public Timetable getTimetable() { return timetable; }

        @Override
        public void clear() {
            for (Course course : new ArrayList<>(timetable.getCourses())) timetable.removeCourse(course);
            sections.clear();
        }
    }

    private static class FaultyTimetableDataAccess implements TimetableDataAccessInterface {
        @Override
        public boolean addSection(Section section) { throw new RuntimeException("DB error"); }

        @Override
        public boolean removeSection(Section section) { throw new RuntimeException("DB error"); }

        @Override
        public boolean hasSection(Section section) { throw new RuntimeException("DB error"); }

        @Override
        public List<Section> getAllSections() { throw new RuntimeException("DB error"); }

        @Override
        public String getCurrentTerm() { throw new RuntimeException("DB error"); }

        @Override
        public boolean hasConflicts(Section section) { throw new RuntimeException("DB error"); }

        @Override
        public Timetable getTimetable() { throw new RuntimeException("DB error"); }

        @Override
        public void clear() { throw new RuntimeException("DB error"); }
    }
}
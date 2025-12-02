package usecase.filter_courses;

import entity.Building;
import entity.Course;
import entity.Section;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilterCoursesInteractorTest {

    // ---------------------------------------------------------------------
    // Fake Presenter that matches your real interface EXACTLY
    // ---------------------------------------------------------------------
    private static class FakePresenter implements usecase.filter_courses.FilterCoursesOutputBoundary {
        usecase.filter_courses.FilterCoursesOutputData lastOutput;

        @Override
        public void present(usecase.filter_courses.FilterCoursesOutputData outputData) {
            this.lastOutput = outputData;
        }
    }

    // ---------------------------------------------------------------------
    // Helper to build valid Course objects using your real constructor
    // ---------------------------------------------------------------------
    private Course makeCourse(String code, String name, int breadth) {
        return new Course(
                code,                     // courseCode
                name,                     // courseName
                "desc",                   // description
                0.5f,                     // credits
                "F",                      // term
                new ArrayList<Section>(), // sections
                (Building) null,          // building not needed
                breadth                   // breadthCategory
        );
    }

    // ---------------------------------------------------------------------
    // TEST 1: Filters correctly by breadth category
    // ---------------------------------------------------------------------
    @Test
    void filtersByBreadthCategory() {
        List<Course> all = new ArrayList<>();
        all.add(makeCourse("CSC207", "Software Design", 1));
        all.add(makeCourse("ANT100", "Anthropology", 2));
        all.add(makeCourse("MAT135", "Calculus I", 1));

        FakePresenter presenter = new FakePresenter();
        usecase.filter_courses.FilterCoursesInputBoundary interactor =
                new usecase.filter_courses.FilterCoursesInteractor(all, presenter);

        // empty query, breadth = 1
        usecase.filter_courses.FilterCoursesInputData input = new usecase.filter_courses.FilterCoursesInputData("", 1);
        interactor.execute(input);

        assertNotNull(presenter.lastOutput);

        List<Course> result = presenter.lastOutput.getCourses();
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(c -> c.getBreadthCategory() == 1));
    }

    // ---------------------------------------------------------------------
    // TEST 2: Returns empty list when no matches
    // ---------------------------------------------------------------------
    @Test
    void returnsEmptyWhenNoMatches() {
        List<Course> all = new ArrayList<>();
        all.add(makeCourse("ANT100", "Anthropology", 2));

        FakePresenter presenter = new FakePresenter();
        usecase.filter_courses.FilterCoursesInputBoundary interactor =
                new usecase.filter_courses.FilterCoursesInteractor(all, presenter);

        usecase.filter_courses.FilterCoursesInputData input = new usecase.filter_courses.FilterCoursesInputData("", 1);
        interactor.execute(input);

        assertNotNull(presenter.lastOutput);
        assertTrue(presenter.lastOutput.getCourses().isEmpty());
    }

    // ---------------------------------------------------------------------
    // TEST 3: Query filtering + breadth filtering together
    // ---------------------------------------------------------------------
    @Test
    void filtersWithQueryAndBreadth() {
        List<Course> all = new ArrayList<>();
        all.add(makeCourse("CSC207", "Software Design", 1));
        all.add(makeCourse("MAT135", "Calculus I", 1));
        all.add(makeCourse("COG250", "Cognition", 2));

        FakePresenter presenter = new FakePresenter();
        usecase.filter_courses.FilterCoursesInputBoundary interactor =
                new usecase.filter_courses.FilterCoursesInteractor(all, presenter);

        // query = "calc", breadth = 1 → should match only MAT135
        usecase.filter_courses.FilterCoursesInputData input = new usecase.filter_courses.FilterCoursesInputData("calc", 1);
        interactor.execute(input);

        assertNotNull(presenter.lastOutput);

        List<Course> result = presenter.lastOutput.getCourses();
        assertEquals(1, result.size());
        assertEquals("MAT135", result.get(0).getCourseCode());
    }

    // ---------------------------------------------------------------------
    // TEST 4: Empty query and null breadth → returns everything
    // ---------------------------------------------------------------------
    @Test
    void nullBreadthReturnsAll() {
        List<Course> all = new ArrayList<>();
        all.add(makeCourse("CSC207", "Software Design", 1));
        all.add(makeCourse("MAT135", "Calculus I", 1));

        FakePresenter presenter = new FakePresenter();
        usecase.filter_courses.FilterCoursesInputBoundary interactor =
                new usecase.filter_courses.FilterCoursesInteractor(all, presenter);

        usecase.filter_courses.FilterCoursesInputData input = new usecase.filter_courses.FilterCoursesInputData("", null);
        interactor.execute(input);

        assertNotNull(presenter.lastOutput);
        assertEquals(2, presenter.lastOutput.getCourses().size());
    }
}
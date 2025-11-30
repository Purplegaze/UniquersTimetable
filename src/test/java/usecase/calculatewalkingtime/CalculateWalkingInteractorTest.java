package usecase.calculatewalkingtime;

import entity.*;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CalculateWalkingInteractorTest {

    @Test
    void successTest() {

        // create timetable with a back to back time pair
        Building ba = new Building("BA", "Address", 0.0, 0.0);
        Building ss = new Building("SS", "Address", 0.0, 0.0);

        Course c1 = new Course("CSC108", "Intro", "",
                0.5f, null, "F",
                new ArrayList<>(), ba, 0);

        Course c2 = new Course("CSC148", "Intro2", "",
                0.5f, null, "F",
                new ArrayList<>(), ss, 0);

        TimeSlot ts1 = new TimeSlot(1, LocalTime.of(10, 0), LocalTime.of(11, 0), ba);
        TimeSlot ts2 = new TimeSlot(1, LocalTime.of(11, 0), LocalTime.of(12, 0), ss);

        Section s1 = new Section("L0101", Collections.singletonList(ts1), 0, new ArrayList<>(), 100, c1);
        Section s2 = new Section("L0201", Collections.singletonList(ts2), 0, new ArrayList<>(), 100, c2);

        Timetable timetable = new Timetable();
        timetable.addSectionOfNewCourse(s1);
        timetable.addSectionOfNewCourse(s2);

        CalculateWalkingDataAccessInterface fakeDAO = new CalculateWalkingDataAccessInterface() {
            @Override
            public Timetable getTimetable() {
                return timetable;
            }

            @Override
            public double calculateWalking(Building from, Building to) {
                return 12.3; // should round to 12
            }
        };

        // presenter
        CalculateWalkingOutputBoundary successPresenter = new CalculateWalkingOutputBoundary() {
            @Override
            public void prepareSuccessView(CalculateWalkingOutputData data) {
                Map<String, Integer> walkingTimes = data.getWalkingTimes();
                assertEquals(1, walkingTimes.size());
                assertTrue(walkingTimes.values().contains(12));
            }

            @Override
            public void prepareFailView(String error) {
                fail("Unexpected failure: " + error);
            }
        };

        CalculateWalkingInputBoundary interactor = new CalculateWalkingInteractor(fakeDAO, successPresenter);
        interactor.execute(new CalculateWalkingInputData(timetable));
    }


    @Test
    void failureNoTimetableTest() {

        CalculateWalkingDataAccessInterface fakeDAO = new CalculateWalkingDataAccessInterface() {
            @Override
            public Timetable getTimetable() {
                return null;
            }

            @Override
            public double calculateWalking(Building from, Building to) {
                return 0;
            }
        };

        CalculateWalkingOutputBoundary failurePresenter = new CalculateWalkingOutputBoundary() {
            @Override
            public void prepareSuccessView(CalculateWalkingOutputData data) {
                fail("Unexpected success.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("No courses found in timetable.", error);
            }
        };

        CalculateWalkingInputBoundary interactor = new CalculateWalkingInteractor(fakeDAO, failurePresenter);
        interactor.execute(new CalculateWalkingInputData(null));
    }


    @Test
    void failureNoBackToBackTest() {

        Building ba = new Building("BA", "Address", 0.0, 0.0);
        Building ss = new Building("SS", "Address", 0.0, 0.0);

        Course c1 = new Course("BIO120", "Bio", "", 0.5f, null, "F", new ArrayList<>(), ba, 0);
        Course c2 = new Course("MAT135", "Calc", "", 0.5f, null, "F", new ArrayList<>(), ss, 0);

        TimeSlot ts1 = new TimeSlot(1, LocalTime.of(10, 0), LocalTime.of(11, 0), ba);
        TimeSlot ts2 = new TimeSlot(1, LocalTime.of(12, 0), LocalTime.of(13, 0), ss); // not back-to-back

        Section s1 = new Section("L0101", Collections.singletonList(ts1), 0, new ArrayList<>(), 100, c1);
        Section s2 = new Section("L0201", Collections.singletonList(ts2), 0, new ArrayList<>(), 100, c2);

        Timetable timetable = new Timetable();
        timetable.addSectionOfNewCourse(s1);
        timetable.addSectionOfNewCourse(s2);

        CalculateWalkingDataAccessInterface fakeDAO = new CalculateWalkingDataAccessInterface() {
            @Override
            public Timetable getTimetable() {
                return timetable;
            }

            @Override
            public double calculateWalking(Building from, Building to) {
                return 10;
            }
        };

        CalculateWalkingOutputBoundary failurePresenter = new CalculateWalkingOutputBoundary() {
            @Override
            public void prepareSuccessView(CalculateWalkingOutputData data) {
                fail("Unexpected success.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("No back-to-back classes found.", error);
            }
        };

        CalculateWalkingInputBoundary interactor = new CalculateWalkingInteractor(fakeDAO, failurePresenter);

        interactor.execute(new CalculateWalkingInputData(timetable));
    }
}

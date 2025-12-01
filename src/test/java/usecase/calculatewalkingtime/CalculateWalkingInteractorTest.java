package usecase.calculatewalkingtime;

import entity.*;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CalculateWalkingInteractorTest {

    @Test
    void nullNextBuildingTest() {

        Building ba = new Building("BA", "Address", 0, 0);
        Building nullBuilding = new Building(null, "Nowhere", 0, 0);

        Course c1 = new Course("CSC209", "Systems", "", 0.5f, "F", new ArrayList<>(), ba, 0);
        Course c2 = new Course("CSC369", "Operating Systems", "", 0.5f, "F", new ArrayList<>(), nullBuilding, 0);

        TimeSlot t1 = new TimeSlot(1, LocalTime.of(9,0), LocalTime.of(10,0), ba);
        TimeSlot t2 = new TimeSlot(1, LocalTime.of(10,0), LocalTime.of(11,0), nullBuilding);

        Timetable tt = new Timetable();
        tt.addSectionOfNewCourse(new Section("S1", List.of(t1), 0,new ArrayList<>(),100,c1));
        tt.addSectionOfNewCourse(new Section("S2", List.of(t2), 0,new ArrayList<>(),100,c2));

        CalculateWalkingDataAccessInterface dao = new CalculateWalkingDataAccessInterface() {
            @Override public Timetable getTimetable() { return tt; }
            @Override public double calculateWalking(Building from, Building to) { return 99; }
        };

        CalculateWalkingOutputBoundary presenter = new CalculateWalkingOutputBoundary() {
            @Override
            public void prepareSuccessView(CalculateWalkingOutputData data) {
                assertTrue(data.getWalkingTimes().values().contains(-1));
            }

            @Override
            public void prepareFailView(String error) {
                fail("Should succeed with -1.");
            }
        };

        new CalculateWalkingInteractor(dao, presenter)
                .execute(new CalculateWalkingInputData(tt));
    }

    @Test
    void nullToBuildingCodeTest() {

        Building ba = new Building("BA", "Address", 0, 0);
        Building nullBuilding = new Building(null, "Unknown", 0, 0);

        Course c1 = new Course("ANT100", "Anthro", "", 0.5f, "F",
                new ArrayList<>(), ba, 0);
        Course c2 = new Course("CHI101", "Chinese", "", 0.5f, "F",
                new ArrayList<>(), nullBuilding, 0);

        TimeSlot t1 = new TimeSlot(1, LocalTime.of(9,0), LocalTime.of(10,0), ba);
        TimeSlot t2 = new TimeSlot(1, LocalTime.of(10,0), LocalTime.of(11,0), nullBuilding);

        Timetable tt = new Timetable();
        tt.addSectionOfNewCourse(new Section("L1", List.of(t1), 0, new ArrayList<>(), 100, c1));
        tt.addSectionOfNewCourse(new Section("L2", List.of(t2), 0, new ArrayList<>(), 100, c2));

        CalculateWalkingDataAccessInterface dao = new CalculateWalkingDataAccessInterface() {
            @Override public Timetable getTimetable() { return tt; }
            @Override public double calculateWalking(Building from, Building to) { return 99; }
        };

        CalculateWalkingOutputBoundary presenter = new CalculateWalkingOutputBoundary() {
            @Override
            public void prepareSuccessView(CalculateWalkingOutputData data) {
                assertTrue(data.getWalkingTimes().values().contains(-1)); // null → -1
            }

            @Override
            public void prepareFailView(String error) {
                fail("Should succeed with -1 placeholder value.");
            }
        };

        new CalculateWalkingInteractor(dao, presenter)
                .execute(new CalculateWalkingInputData(tt));
    }


    @Test
    void successTest() {

        Building ba = new Building("BA", "Address", 0.0, 0.0);
        Building ss = new Building("SS", "Address", 0.0, 0.0);

        Course c1 = new Course("CSC108", "Intro", "",
                0.5f, "F",
                new ArrayList<>(), ba, 0);

        Course c2 = new Course("CSC148", "Intro2", "",
                0.5f, "F",
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
                return 12.3;
            }
        };

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
    void roundingUpTest() {
        Building ba = new Building("BA", "Address", 0, 0);
        Building ss = new Building("SS", "Address", 0, 0);

        Course c1 = new Course("PSL300", "Physiology", "", 0.5f, "F", new ArrayList<>(), ba, 0);
        Course c2 = new Course("STA257", "Stats", "", 0.5f, "F", new ArrayList<>(), ss, 0);

        TimeSlot t1 = new TimeSlot(1, LocalTime.of(9,0), LocalTime.of(10,0), ba);
        TimeSlot t2 = new TimeSlot(1, LocalTime.of(10,0), LocalTime.of(11,0), ss);

        Timetable tt = new Timetable();
        tt.addSectionOfNewCourse(new Section("S1", List.of(t1), 0,new ArrayList<>(),100,c1));
        tt.addSectionOfNewCourse(new Section("S2", List.of(t2), 0,new ArrayList<>(),100,c2));

        CalculateWalkingDataAccessInterface dao = new CalculateWalkingDataAccessInterface() {
            @Override public Timetable getTimetable() { return tt; }
            @Override public double calculateWalking(Building from, Building to) {
                return 12.6; // forces rounding UP
            }
        };

        CalculateWalkingOutputBoundary presenter = new CalculateWalkingOutputBoundary() {
            @Override
            public void prepareSuccessView(CalculateWalkingOutputData data) {
                assertTrue(data.getWalkingTimes().values().contains(13));
            }

            @Override
            public void prepareFailView(String error) {
                fail("Should succeed.");
            }
        };

        new CalculateWalkingInteractor(dao, presenter).execute(new CalculateWalkingInputData(tt));
    }


    @Test
    void successTbdBuildingTest() {
        Building tbd = new Building("TBD", "Unknown", 0, 0);
        Building ba = new Building("BA", "Address", 0, 0);

        Timetable timetable = new Timetable();

        Course c1 = new Course("CSC108", "Intro", "", 0.5f, "F", new ArrayList<>(), tbd, 0);
        Course c2 = new Course("CSC148", "Intro2", "", 0.5f, "F", new ArrayList<>(), ba, 0);

        TimeSlot t1 = new TimeSlot(1, LocalTime.of(10,0), LocalTime.of(11,0), tbd);
        TimeSlot t2 = new TimeSlot(1, LocalTime.of(11,0), LocalTime.of(12,0), ba);

        timetable.addSectionOfNewCourse(new Section("A", List.of(t1), 0, new ArrayList<>(), 100, c1));
        timetable.addSectionOfNewCourse(new Section("B", List.of(t2), 0, new ArrayList<>(), 100, c2));

        CalculateWalkingDataAccessInterface dao = new CalculateWalkingDataAccessInterface() {
            @Override public Timetable getTimetable() { return timetable; }
            @Override public double calculateWalking(Building from, Building to) { return 10; }
        };

        CalculateWalkingOutputBoundary presenter = new CalculateWalkingOutputBoundary() {
            @Override public void prepareSuccessView(CalculateWalkingOutputData data) {
                assertTrue(data.getWalkingTimes().values().contains(-1));
            }

            @Override public void prepareFailView(String error) {
                fail("Should succeed by marking as -1 (unknown).");
            }
        };

        new CalculateWalkingInteractor(dao, presenter).execute(new CalculateWalkingInputData(timetable));
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

        Course c1 = new Course("BIO120", "Bio", "", 0.5f, "F", new ArrayList<>(), ba, 0);
        Course c2 = new Course("MAT135", "Calc", "", 0.5f, "F", new ArrayList<>(), ss, 0);

        TimeSlot ts1 = new TimeSlot(1, LocalTime.of(10, 0), LocalTime.of(11, 0), ba);
        TimeSlot ts2 = new TimeSlot(1, LocalTime.of(12, 0), LocalTime.of(13, 0), ss);

        Section s1 = new Section("L0101", Collections.singletonList(ts1), 0, new ArrayList<>(), 100, c1);
        Section s2 = new Section("L0201", Collections.singletonList(ts2), 0, new ArrayList<>(), 100, c2);

        Timetable timetable = new Timetable();
        timetable.addSectionOfNewCourse(s1);
    }
    @Test
    void singleCourseNoNextTest() {

        Building ba = new Building("BA", "Address", 0, 0);
        Course c1 = new Course("CSC101", "Test", "", 0.5f, "F",
                new ArrayList<>(), ba, 0);

        TimeSlot ts1 = new TimeSlot(1, LocalTime.of(9,0), LocalTime.of(10,0), ba);

        Timetable timetable = new Timetable();
        timetable.addSectionOfNewCourse(
                new Section("L0101", List.of(ts1), 0, new ArrayList<>(), 100, c1)
        );

        CalculateWalkingDataAccessInterface dao = new CalculateWalkingDataAccessInterface() {
            @Override public Timetable getTimetable() { return timetable; }
            @Override public double calculateWalking(Building from, Building to) { return 999; }
        };

        CalculateWalkingOutputBoundary presenter = new CalculateWalkingOutputBoundary() {
            @Override public void prepareSuccessView(CalculateWalkingOutputData data) {
                fail("Should not succeed — no next course.");
            }

            @Override public void prepareFailView(String error) {
                assertEquals("No back-to-back classes found.", error);
            }
        };

        CalculateWalkingInputBoundary interactor =
                new CalculateWalkingInteractor(dao, presenter);

        interactor.execute(new CalculateWalkingInputData(timetable));
    }

}

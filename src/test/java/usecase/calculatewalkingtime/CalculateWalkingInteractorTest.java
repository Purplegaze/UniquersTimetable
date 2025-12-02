package usecase.calculatewalkingtime;

import data_access.TimetableDataAccessInterface;
import entity.*;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CalculateWalkingInteractorTest {

    private static class FakeTimetableDAO implements TimetableDataAccessInterface {
        private final Timetable timetable;

        FakeTimetableDAO(Timetable timetable) {
            this.timetable = timetable;
        }

        @Override public Timetable getTimetable() { return timetable; }
        @Override public boolean addSection(Section section) { return false; }
        @Override public boolean removeSection(Section section) { return false; }
        @Override public boolean hasConflicts(Section section) { return false; }
        @Override public boolean hasSection(Section section) { return false; }
        @Override public List<Section> getAllSections() { return List.of(); }
        @Override public void clear() {}
        @Override public String getCurrentTerm() { return null; }
    }

    private static class FakeWalkingDAO implements CalculateWalkingDataAccessInterface {

        private final double value;

        FakeWalkingDAO(double value) {
            this.value = value;
        }

        @Override
        public double calculateWalking(Building from, Building to) {
            return value;
        }

        @Override
        public Timetable getTimetable() {
            return null;
        }
    }


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

        FakeWalkingDAO walkingDAO = new FakeWalkingDAO(99);
        FakeTimetableDAO timetableDAO = new FakeTimetableDAO(tt);

        CalculateWalkingOutputBoundary presenter = new CalculateWalkingOutputBoundary() {
            @Override public void prepareSuccessView(CalculateWalkingOutputData data) {
                assertTrue(data.getWalkingTimes().values().contains(-1));
            }
            @Override public void prepareFailView(String error) {
                fail("Should succeed with -1 placeholder.");
            }
        };

        new CalculateWalkingInteractor(walkingDAO, timetableDAO, presenter)
                .execute();
    }


    @Test
    void nullToBuildingCodeTest() {

        Building ba = new Building("BA", "Address", 0, 0);
        Building nullBuilding = new Building(null, "Unknown", 0, 0);

        Course c1 = new Course("ANT100", "Anthro", "", 0.5f, "F", new ArrayList<>(), ba, 0);
        Course c2 = new Course("CHI101", "Chinese", "", 0.5f, "F", new ArrayList<>(), nullBuilding, 0);

        TimeSlot t1 = new TimeSlot(1, LocalTime.of(9,0), LocalTime.of(10,0), ba);
        TimeSlot t2 = new TimeSlot(1, LocalTime.of(10,0), LocalTime.of(11,0), nullBuilding);

        Timetable tt = new Timetable();
        tt.addSectionOfNewCourse(new Section("L1", List.of(t1), 0,new ArrayList<>(),100,c1));
        tt.addSectionOfNewCourse(new Section("L2", List.of(t2), 0,new ArrayList<>(),100,c2));

        FakeWalkingDAO walkingDAO = new FakeWalkingDAO(99);
        FakeTimetableDAO timetableDAO = new FakeTimetableDAO(tt);

        CalculateWalkingOutputBoundary presenter = new CalculateWalkingOutputBoundary() {
            @Override public void prepareSuccessView(CalculateWalkingOutputData data) {
                assertTrue(data.getWalkingTimes().values().contains(-1));
            }
            @Override public void prepareFailView(String error) {
                fail("Should succeed.");
            }
        };

        new CalculateWalkingInteractor(walkingDAO, timetableDAO, presenter)
                .execute();
    }


    @Test
    void successTest() {

        // create timetable with a back to back time pair
        Building ba = new Building("BA", "Address", 0.0, 0.0);
        Building ss = new Building("SS", "Address", 0.0, 0.0);

        Course c1 = new Course("CSC108", "Intro", "", 0.5f, "F", new ArrayList<>(), ba, 0);
        Course c2 = new Course("CSC148", "Intro2", "", 0.5f, "F", new ArrayList<>(), ss, 0);

        TimeSlot ts1 = new TimeSlot(1, LocalTime.of(10, 0), LocalTime.of(11, 0), ba);
        TimeSlot ts2 = new TimeSlot(1, LocalTime.of(11, 0), LocalTime.of(12, 0), ss);

        Timetable timetable = new Timetable();
        timetable.addSectionOfNewCourse(new Section("L0101", List.of(ts1), 0,new ArrayList<>(),100,c1));
        timetable.addSectionOfNewCourse(new Section("L0201", List.of(ts2), 0,new ArrayList<>(),100,c2));

        FakeWalkingDAO walkingDAO = new FakeWalkingDAO(12.3);
        FakeTimetableDAO timetableDAO = new FakeTimetableDAO(timetable);

        CalculateWalkingOutputBoundary presenter = new CalculateWalkingOutputBoundary() {
            @Override public void prepareSuccessView(CalculateWalkingOutputData data) {
                assertEquals(1, data.getWalkingTimes().size());
                assertTrue(data.getWalkingTimes().values().contains(12)); // rounded down
            }
            @Override public void prepareFailView(String error) {
                fail("Unexpected failure: " + error);
            }
        };

        new CalculateWalkingInteractor(walkingDAO, timetableDAO, presenter)
                .execute();
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

        FakeWalkingDAO walkingDAO = new FakeWalkingDAO(12.6);
        FakeTimetableDAO timetableDAO = new FakeTimetableDAO(tt);

        CalculateWalkingOutputBoundary presenter = new CalculateWalkingOutputBoundary() {
            @Override public void prepareSuccessView(CalculateWalkingOutputData data) {
                assertTrue(data.getWalkingTimes().values().contains(13)); // rounded up
            }
            @Override public void prepareFailView(String error) {
                fail("Expected success.");
            }
        };

        new CalculateWalkingInteractor(walkingDAO, timetableDAO, presenter)
                .execute();
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

        timetable.addSectionOfNewCourse(new Section("A", List.of(t1), 0,new ArrayList<>(),100,c1));
        timetable.addSectionOfNewCourse(new Section("B", List.of(t2), 0,new ArrayList<>(),100,c2));

        FakeWalkingDAO walkingDAO = new FakeWalkingDAO(10);
        FakeTimetableDAO timetableDAO = new FakeTimetableDAO(timetable);

        CalculateWalkingOutputBoundary presenter = new CalculateWalkingOutputBoundary() {
            @Override public void prepareSuccessView(CalculateWalkingOutputData data) {
                assertTrue(data.getWalkingTimes().values().contains(-1));
            }
            @Override public void prepareFailView(String error) {
                fail("Expected success.");
            }
        };

        new CalculateWalkingInteractor(walkingDAO, timetableDAO, presenter)
                .execute();
    }


    @Test
    void failureNoTimetableTest() {

        FakeWalkingDAO walkingDAO = new FakeWalkingDAO(0);
        FakeTimetableDAO timetableDAO = new FakeTimetableDAO(null);

        CalculateWalkingOutputBoundary presenter = new CalculateWalkingOutputBoundary() {
            @Override public void prepareSuccessView(CalculateWalkingOutputData data) {
                fail("Unexpected success.");
            }
            @Override public void prepareFailView(String error) {
                assertEquals("No courses found in timetable.", error);
            }
        };

        new CalculateWalkingInteractor(walkingDAO, timetableDAO, presenter)
                .execute();
    }


    @Test
    void failureNoBackToBackTest() {

        Building ba = new Building("BA", "Address", 0.0, 0.0);

        Course c1 = new Course("BIO120", "Bio", "", 0.5f, "F", new ArrayList<>(), ba, 0);

        TimeSlot ts1 = new TimeSlot(1, LocalTime.of(10, 0), LocalTime.of(11, 0), ba);

        Section s1 = new Section("L0101", Collections.singletonList(ts1), 0,new ArrayList<>(),100,c1);

        Timetable timetable = new Timetable();
        timetable.addSectionOfNewCourse(s1);

        FakeWalkingDAO walkingDAO = new FakeWalkingDAO(999);
        FakeTimetableDAO timetableDAO = new FakeTimetableDAO(timetable);

        CalculateWalkingOutputBoundary presenter = new CalculateWalkingOutputBoundary() {
            @Override public void prepareSuccessView(CalculateWalkingOutputData data) {
                fail("Should not succeed — no back-to-back classes.");
            }
            @Override public void prepareFailView(String error) {
                assertEquals("No back-to-back classes found.", error);
            }
        };

        new CalculateWalkingInteractor(walkingDAO, timetableDAO, presenter)
                .execute();
    }
}

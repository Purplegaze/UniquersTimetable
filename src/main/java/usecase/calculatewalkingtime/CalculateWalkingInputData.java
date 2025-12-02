package usecase.calculatewalkingtime;

import entity.Timetable;

public class CalculateWalkingInputData {
    private final Timetable timetable;

    public CalculateWalkingInputData(Timetable timetable) {
        this.timetable = timetable;
    }

    public Timetable getTimetable() {
        return timetable;
    }
}
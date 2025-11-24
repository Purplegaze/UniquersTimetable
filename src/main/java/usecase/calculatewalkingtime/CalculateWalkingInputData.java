package usecase.calculatewalkingtime;
import entity.Timetable;

/**
 * The input data for the Calculate Walking Time Use Case
 */
public class CalculateWalkingInputData{

    private final Timetable timetable;

    public CalculateWalkingInputData(Timetable timetable) {
        this.timetable = timetable;
    }

    public Timetable getTimetable() {
        return timetable;
    }

}

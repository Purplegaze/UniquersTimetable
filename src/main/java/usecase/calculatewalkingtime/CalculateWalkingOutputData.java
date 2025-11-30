package usecase.calculatewalkingtime;

import java.util.Map;

public class CalculateWalkingOutputData {
    // Map from "CourseA -> CourseB" to walking time in minutes of all back to back classes in timetable
    private final Map<String, Integer> walkingTimes;

    public CalculateWalkingOutputData(Map<String, Integer> walkingTimes) {
        this.walkingTimes = walkingTimes;
    }

    public Map<String, Integer> getWalkingTimes() {
        return walkingTimes;
    }
}
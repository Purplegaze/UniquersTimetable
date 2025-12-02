package usecase.calculatewalkingtime;

import java.util.Map;

public class CalculateWalkingOutputData {

    private final Map<String, Integer> walkingTimes;
    private final boolean hasLongWalk;

    public CalculateWalkingOutputData(Map<String, Integer> walkingTimes,
                                      boolean hasLongWalk) {
        this.walkingTimes = walkingTimes;
        this.hasLongWalk = hasLongWalk;
    }

    public Map<String, Integer> getWalkingTimes() {
        return walkingTimes;
    }

    public boolean hasLongWalk() {
        return hasLongWalk;
    }
}

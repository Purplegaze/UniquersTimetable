package interface_adapter.calculatewalkingtime;

import usecase.calculatewalkingtime.CalculateWalkingOutputBoundary;
import usecase.calculatewalkingtime.CalculateWalkingOutputData;
import java.util.List;

public interface CalculateWalkingInterface {

    void displayWalkingTimes(String text);

    void showError(String message);

    void highlightLongWalks(List<String> entries);
}

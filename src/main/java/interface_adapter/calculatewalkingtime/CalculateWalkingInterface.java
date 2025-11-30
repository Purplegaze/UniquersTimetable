package interface_adapter.calculatewalkingtime;

import usecase.calculatewalkingtime.CalculateWalkingOutputBoundary;
import usecase.calculatewalkingtime.CalculateWalkingOutputData;

public interface CalculateWalkingInterface {

    void displayWalkingTimes(String string);

    void showError(String errorMessage);
}

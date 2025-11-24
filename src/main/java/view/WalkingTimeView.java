package view;

import usecase.calculatewalkingtime.CalculateWalkingOutputBoundary;
import usecase.calculatewalkingtime.CalculateWalkingOutputData;

import java.util.Map;

//TODO: Add Swing UI, for now left as console output as placeholder
public class WalkingTimeView implements CalculateWalkingOutputBoundary {

    @Override
    public void prepareSuccessView(CalculateWalkingOutputData outputData) {
        Map<String, Integer> walkingTimes = outputData.getWalkingTimes();

        if (walkingTimes.isEmpty()) {
            System.out.println("No back-to-back classes found.");
        } else {
            System.out.println("Walking times between consecutive classes:");
            for (Map.Entry<String, Integer> entry : walkingTimes.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue() + " minutes");
            }
        }
    }

    @Override
    public void prepareFailView(String errorMessage) { System.out.println("Error calculating walking times: " + errorMessage);}
}

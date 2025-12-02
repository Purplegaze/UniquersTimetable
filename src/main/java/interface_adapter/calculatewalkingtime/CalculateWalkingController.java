package interface_adapter.calculatewalkingtime;

import entity.Timetable;
import usecase.calculatewalkingtime.CalculateWalkingInputBoundary;
import usecase.calculatewalkingtime.CalculateWalkingInputData;



/**
 * The Controller for the Calculate Walking Time Use Case.
 */
public class CalculateWalkingController {

    private final CalculateWalkingInputBoundary useCase;


    public CalculateWalkingController(CalculateWalkingInputBoundary useCase) {
        this.useCase = useCase;
    }


    public void execute(Timetable timetable) {
        CalculateWalkingInputData inputData = new CalculateWalkingInputData(timetable);
        useCase.execute(inputData);
    }
}



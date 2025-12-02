package interface_adapter.calculatewalkingtime;

import usecase.calculatewalkingtime.CalculateWalkingInputBoundary;


/**
 * The Controller for the Calculate Walking Time Use Case.
 */
public class CalculateWalkingController {

    private final CalculateWalkingInputBoundary useCase;


    public CalculateWalkingController(CalculateWalkingInputBoundary useCase) {
        this.useCase = useCase;
    }

    /**
     * Executes the Calculate Walking Time Use Case without the view knowing the timetable.
     */
    public void execute() {
        useCase.execute();
    }

}

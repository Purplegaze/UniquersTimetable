package interface_adapter.calculatewalkingtime;

import entity.Timetable;
import usecase.calculatewalkingtime.CalculateWalkingInputBoundary;
import usecase.calculatewalkingtime.CalculateWalkingInputData;

/**
 * The Controller for the Calculate Walking Time Use Case.
 */
public class CalculateWalkingController {

    private final CalculateWalkingInputBoundary calculateWalkingUseCaseInteractor;

    public CalculateWalkingController(CalculateWalkingInputBoundary calculateWalkingUseCaseInteractor) {
        this.calculateWalkingUseCaseInteractor = calculateWalkingUseCaseInteractor;
    }

    /**
     * Executes the Calculate Walking Time Use Case.
     *
     * @param tb the current timetable
     */
    public void execute(Timetable tb) {
        CalculateWalkingInputData inputData = new CalculateWalkingInputData(tb);
        calculateWalkingUseCaseInteractor.execute(inputData);
    }
}

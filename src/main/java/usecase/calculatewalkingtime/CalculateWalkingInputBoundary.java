package usecase.calculatewalkingtime;

/**
 * The Calculate Walking Time Use Case Input Boundary
 */
public interface CalculateWalkingInputBoundary {

    /**
     * Execute the Calculate Walking Time Use Case.
     * @param calculateWalkingInputData the input data for this use case
     */
    void execute(CalculateWalkingInputData calculateWalkingInputData);

}

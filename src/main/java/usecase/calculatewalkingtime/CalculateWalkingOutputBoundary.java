package usecase.calculatewalkingtime;

/**
 * The output boundary for the Calculate Walking Use Case.
 */
public interface CalculateWalkingOutputBoundary {
    /**
     * Prepares the success view for the Calculate Walking Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(CalculateWalkingOutputData outputData);

    /**
     * Prepares the failure view for the Calculate Walking Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}

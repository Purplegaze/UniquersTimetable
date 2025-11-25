package interface_adapter.calculatewalkingtime;

import usecase.calculatewalkingtime.CalculateWalkingInputBoundary;
import usecase.calculatewalkingtime.CalculateWalkingInputData;

/**
 * The controller for the Calculate Walking Use Case.
 */
public class CalculateWalkingController {

    private final CalculateWalkingInputBoundary calculateWalkingUseCaseInteractor;

    public CalculateWalkingController(CalculateWalkingInputBoundary calculateWalkingUseCaseInteractor) {
        this.calculateWalkingUseCaseInteractor = loginUseCaseInteractor;
    }

    /**
     * Executes the Login Use Case.
     * @param username the username of the user logging in
     * @param password the password of the user logging in
     */
    public void execute(String username, String password) {
        final LoginInputData loginInputData = new LoginInputData(
                username, password);

        loginUseCaseInteractor.execute(loginInputData);
    }
}

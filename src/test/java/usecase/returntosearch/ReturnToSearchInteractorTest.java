package usecase.returntosearch;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReturnToSearchInteractorTest {

    @Test
    public void successTest() {
        // Prepare the input data
        String expectedCourseCode = "CSC207";
        ReturnToSearchInputData inputData = new ReturnToSearchInputData(expectedCourseCode);

        // Create a mock presenter to capture the output
        ReturnToSearchOutputBoundary successPresenter = new ReturnToSearchOutputBoundary() {
            @Override
            public void prepareSuccessView(ReturnToSearchOutputData outputData) {
                // Verify the output data matches the input
                assertEquals(expectedCourseCode, outputData.getCourseCode());
            }
        };

        // Execute the Interactor
        ReturnToSearchInteractor interactor = new ReturnToSearchInteractor(successPresenter);
        interactor.execute(inputData);
    }
}
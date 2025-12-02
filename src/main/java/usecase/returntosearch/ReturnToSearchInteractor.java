package usecase.returntosearch;

public class ReturnToSearchInteractor implements ReturnToSearchInputBoundary {
    private final ReturnToSearchOutputBoundary presenter;

    public ReturnToSearchInteractor(ReturnToSearchOutputBoundary presenter) {
        this.presenter = presenter;
    }

    @Override
    public void execute(ReturnToSearchInputData inputData) {
        // The logic is simple: pass the course code to the presenter to update the view
        ReturnToSearchOutputData outputData = new ReturnToSearchOutputData(inputData.getCourseCode());
        presenter.prepareSuccessView(outputData);
    }
}
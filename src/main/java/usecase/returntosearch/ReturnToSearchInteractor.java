package usecase.returntosearch;

public class ReturnToSearchInteractor implements ReturnToSearchInputBoundary {
    private final ReturnToSearchOutputBoundary presenter;

    public ReturnToSearchInteractor(ReturnToSearchOutputBoundary presenter) {
        this.presenter = presenter;
    }

    @Override
    public void execute(ReturnToSearchInputData inputData) {
        ReturnToSearchOutputData outputData = new ReturnToSearchOutputData(inputData.getCourseCode());
        presenter.prepareSuccessView(outputData);
    }
}
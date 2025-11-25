package interface_adapter.returntosearch;

import usecase.returntosearch.ReturnToSearchInputBoundary;
import usecase.returntosearch.ReturnToSearchInputData;

public class ReturnToSearchController {
    private final ReturnToSearchInputBoundary interactor;

    public ReturnToSearchController(ReturnToSearchInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String courseCode) {
        interactor.execute(new ReturnToSearchInputData(courseCode));
    }
}
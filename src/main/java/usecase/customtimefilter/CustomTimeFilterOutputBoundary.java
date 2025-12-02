package usecase.customtimefilter;

public interface CustomTimeFilterOutputBoundary {

    void presentResults(CustomTimeFilterOutputData outputData);

    void presentNoResults();

    void presentError(String errorMessage);
}
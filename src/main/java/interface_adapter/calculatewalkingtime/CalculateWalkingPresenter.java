package interface_adapter.calculatewalkingtime;

import usecase.calculatewalkingtime.CalculateWalkingOutputBoundary;
import usecase.calculatewalkingtime.CalculateWalkingOutputData;

public class CalculateWalkingPresenter implements CalculateWalkingOutputBoundary {

    private final CalculateWalkingViewModel viewModel;

    public CalculateWalkingPresenter(CalculateWalkingViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(CalculateWalkingOutputData outputData) {
        CalculateWalkingState newState = new CalculateWalkingState((CalculateWalkingState) viewModel.getState());

        StringBuilder sb = new StringBuilder();
        outputData.getWalkingTimes().forEach((key, value) ->
                sb.append(key).append(": ").append(value).append(" min\n")
        );

        newState.setWalkingTimesText(sb.toString());
        newState.setErrorMessage(null);

        viewModel.setState(newState);
        viewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        CalculateWalkingState newState = new CalculateWalkingState((CalculateWalkingState) viewModel.getState());
        newState.setWalkingTimesText("");
        newState.setErrorMessage(errorMessage);

        viewModel.setState(newState);
        viewModel.firePropertyChange();
    }
}

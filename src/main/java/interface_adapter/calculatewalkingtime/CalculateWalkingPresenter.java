package interface_adapter.calculatewalkingtime;

import interface_adapter.ViewManagerModel;
import usecase.calculatewalkingtime.CalculateWalkingOutputBoundary;
import usecase.calculatewalkingtime.CalculateWalkingOutputData;

public class CalculateWalkingPresenter implements CalculateWalkingOutputBoundary {

    private final CalculateWalkingViewModel calculateWalkingViewModel;
    private final ViewManagerModel viewManagerModel;

    public CalculateWalkingPresenter(ViewManagerModel viewManagerModel,
                                     CalculateWalkingViewModel calculateWalkingViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.calculateWalkingViewModel = calculateWalkingViewModel;
    }

    @Override
    public void prepareSuccessView(CalculateWalkingOutputData outputData) {

        // Build display text
        StringBuilder sb = new StringBuilder();
        for (String key : outputData.getWalkingTimes().keySet()) {
            sb.append(key)
                    .append(": ")
                    .append(outputData.getWalkingTimes().get(key))
                    .append(" min\n");
        }

        CalculateWalkingState state = calculateWalkingViewModel.getState();
        state.setWalkingTimesText(sb.toString());
        state.setErrorMessage(null);

        calculateWalkingViewModel.firePropertyChange("walkingTime");
    }

    @Override
    public void prepareFailView(String error) {

        // Update error state
        CalculateWalkingState state = calculateWalkingViewModel.getState();
        state.setErrorMessage(error);

        // Notify the view
        calculateWalkingViewModel.firePropertyChange("walkingTime");
    }
}

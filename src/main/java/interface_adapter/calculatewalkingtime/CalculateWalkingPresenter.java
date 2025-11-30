package interface_adapter.calculatewalkingtime;

import usecase.calculatewalkingtime.CalculateWalkingOutputBoundary;
import usecase.calculatewalkingtime.CalculateWalkingOutputData;

/**
 * Presenter for Calculate Walking Time use case.
 */
public class CalculateWalkingPresenter implements CalculateWalkingOutputBoundary {

    private final CalculateWalkingInterface view;

    public CalculateWalkingPresenter(CalculateWalkingInterface view) {
        if (view == null) {
            throw new IllegalArgumentException("View cannot be null");
        }
        this.view = view;
    }

    @Override
    public void prepareSuccessView(CalculateWalkingOutputData outputData) {
        StringBuilder sb = new StringBuilder();

        outputData.getWalkingTimes().forEach((key, value) -> {
            sb.append(key)
                    .append(": ")
                    .append(value)
                    .append(" min\n");
        });

        view.displayWalkingTimes(sb.toString());
    }

    @Override
    public void prepareFailView(String errorMessage) {
        view.showError(errorMessage);
    }
}

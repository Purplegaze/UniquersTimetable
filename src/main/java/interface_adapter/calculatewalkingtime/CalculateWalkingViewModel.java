package interface_adapter.calculatewalkingtime;

import interface_adapter.ViewModel;

/**
 * The View Model for the Calculate Walking Time View.
 */
public class CalculateWalkingViewModel extends ViewModel<CalculateWalkingState> {

    public CalculateWalkingViewModel() {
        super("calculate walking time");
        setState(new CalculateWalkingState());
    }
}

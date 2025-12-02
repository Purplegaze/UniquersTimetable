package interface_adapter.calculatewalkingtime;

import interface_adapter.ViewModel;

public class CalculateWalkingViewModel extends ViewModel<CalculateWalkingState> {

    public CalculateWalkingViewModel() {
        super("calculate walking time");
        setState(new CalculateWalkingState());
    }
}

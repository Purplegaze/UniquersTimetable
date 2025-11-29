package interface_adapter.calculatewalkingtime;

/**
 * The State information for the Calculate Walking Time View.
 */
public class CalculateWalkingState {

    private String walkingTimesText = "";
    private String errorMessage;

    public CalculateWalkingState(CalculateWalkingState copy) {
        this.walkingTimesText = copy.walkingTimesText;
        this.errorMessage = copy.errorMessage;
    }

    public CalculateWalkingState() {
    }

    public String getWalkingTimesText() {
        return walkingTimesText;
    }

    public void setWalkingTimesText(String walkingTimesText) {
        this.walkingTimesText = walkingTimesText;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

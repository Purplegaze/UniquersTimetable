//package view;
//
//import interface_adapter.calculatewalkingtime.CalculateWalkingInterface;
//
///**
// * Adapter to bridge the existing WalkingTimeView
// * with the CalculateWalkingInterface used by the presenter.
// */
//public class WalkingTimeViewAdapter implements CalculateWalkingInterface {
//
//    private final WalkingTimeView walkingTimeView;
//
//    public WalkingTimeViewAdapter(WalkingTimeView walkingTimeView) {
//        if (walkingTimeView == null) {
//            throw new IllegalArgumentException("WalkingTimeView cannot be null");
//        }
//        this.walkingTimeView = walkingTimeView;
//    }
//
//    @Override
//    public void displayWalkingTimes(String text) {
//        walkingTimeView.displayWalkingTimes(text);
//    }
//
//    @Override
//    public void showError(String message) {
//        walkingTimeView.showErrorMessage(message);
//    }
//
//    public WalkingTimeView getWalkingTimeView() {
//        return walkingTimeView;
//    }
//}

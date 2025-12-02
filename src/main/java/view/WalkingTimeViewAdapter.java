package view;

import interface_adapter.calculatewalkingtime.CalculateWalkingInterface;

import java.util.List;

public class WalkingTimeViewAdapter implements CalculateWalkingInterface {

    private final WalkingTimeView walkingTimeView;
    private final TimetableView   timetableView;

    public WalkingTimeViewAdapter(WalkingTimeView walkingTimeView,
                                  TimetableView timetableView) {
        this.walkingTimeView = walkingTimeView;
        this.timetableView   = timetableView;
    }

    @Override
    public void displayWalkingTimes(String text) {
        walkingTimeView.displayWalkingTimes(text);
    }

    @Override
    public void showError(String message) {
        walkingTimeView.showErrorMessage(message);
        timetableView.clearHighlights();
    }

    @Override
    public void highlightLongWalks(List<String> entries) {
        timetableView.clearHighlights();
        if (entries == null) return;

        for (String entry : entries) {
            int colon = entry.indexOf(':');
            if (colon < 0) continue;

            String day = entry.substring(0, colon).trim();
            String rest = entry.substring(colon + 1).trim(); // "APM446H1 → EAS460H1"

            String[] parts = rest.split("→");
            if (parts.length != 2) continue;

            String code1 = parts[0].trim();
            String code2 = parts[1].trim();

            timetableView.highlightCoursesOnDay(day, code1, code2);
        }
    }
}

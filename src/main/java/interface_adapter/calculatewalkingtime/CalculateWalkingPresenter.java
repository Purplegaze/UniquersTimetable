package interface_adapter.calculatewalkingtime;

import usecase.calculatewalkingtime.CalculateWalkingOutputBoundary;
import usecase.calculatewalkingtime.CalculateWalkingOutputData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public void prepareSuccessView(CalculateWalkingOutputData data) {

        Map<String, Integer> times = data.getWalkingTimes();
        boolean hasLongWalk = data.hasLongWalk();

        List<String> longWalkEntries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : times.entrySet()) {
            String raw = entry.getKey();
            int minutes = entry.getValue();

            if (minutes > 10) {
                String day = raw.substring(0, raw.indexOf(':'));

                int hour = extractStartHour(raw);

                longWalkEntries.add(day + "-" + hour);
            }
        }

        String text = format(times);
        if (hasLongWalk) {
            text = "[LONG_WALK_WARNING]\n" + text;
        }

        view.displayWalkingTimes(text);

        if (hasLongWalk) {
            view.highlightLongWalks(longWalkEntries);
        }
    }

    @Override
    public void prepareFailView(String errorMessage) {
        view.showError(errorMessage);
    }

    private String format(Map<String, Integer> times) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, Integer> entry : times.entrySet()) {
            String key = entry.getKey();
            int minutes = entry.getValue();

            sb.append(key).append(": ");
            if (minutes < 0) {
                sb.append("unknown");
            } else {
                sb.append(minutes).append(" min");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private int extractStartHour(String raw) {
        int colon = raw.indexOf(':');
        int dash = raw.indexOf('-', colon);

        if (colon == -1 || dash == -1) return -1;

        try {
            return Integer.parseInt(raw.substring(colon + 2, dash));
        } catch (Exception e) {
            return -1;
        }
    }
}

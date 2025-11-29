package data_access;

import entity.Building;
import entity.Timetable;
import org.json.JSONObject;
import usecase.calculatewalkingtime.CalculateWalkingDataAccessInterface;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class WalkingTimeDataAccessObject implements CalculateWalkingDataAccessInterface {

    private final Map<String, Map<String, Double>> walkingTimes = new HashMap<>();
    private final TimetableDataAccessInterface timetableDataAccess;

    public WalkingTimeDataAccessObject(TimetableDataAccessInterface timetableDataAccess) {
        this.timetableDataAccess = timetableDataAccess;
        loadWalkingTimesFromJson();
    }

    private void loadWalkingTimesFromJson() {
        try {
            InputStream is = getClass().getResourceAsStream("/walking_cache_ors.json");
            if (is == null) throw new RuntimeException("walking_cache_ors.json not found in resources!");

            String jsonText = new Scanner(is, StandardCharsets.UTF_8).useDelimiter("\\A").next();
            JSONObject jsonObject = new JSONObject(jsonText);

            for (String from : jsonObject.keySet()) {
                JSONObject inner = jsonObject.getJSONObject(from);
                Map<String, Double> innerMap = new HashMap<>();
                for (String to : inner.keySet()) {
                    innerMap.put(to, inner.getDouble(to));
                }
                walkingTimes.put(from, innerMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load walking time data.");
        }
    }

    @Override
    public Timetable getTimetable() {
        return timetableDataAccess.getTimetable();
    }

    @Override
    public double calculateWalking(Building building1, Building building2) {
        return walkingTimes
                .getOrDefault(building1.getBuildingCode(), Map.of())
                .getOrDefault(building2.getBuildingCode(), 7.0);
    }
}

package data_access;

import entity.Building;
import entity.Timetable;
import usecase.calculatewalkingtime.CalculateWalkingDataAccessInterface;

import org.json.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class WalkingTimeDataAccessObject implements CalculateWalkingDataAccessInterface {

    private final Map<String, Map<String, Double>> walkingTimes;

    public WalkingTimeDataAccessObject() {
        walkingTimes = loadWalkingTimesFromJson();
    }

    private Map<String, Map<String, Double>> loadWalkingTimesFromJson() {
        Map<String, Map<String, Double>> result = new HashMap<>();

        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("walking_cache_ors.json");

            if (is == null) throw new RuntimeException("Walking cache JSON NOT FOUND in resources.");

            String jsonText = new Scanner(is, StandardCharsets.UTF_8).useDelimiter("\\A").next();
            JSONObject jsonObject = new JSONObject(jsonText);

            for (String fromBuilding : jsonObject.keySet()) {
                JSONObject inner = jsonObject.getJSONObject(fromBuilding);
                Map<String, Double> innerMap = new HashMap<>();
                for (String toBuilding : inner.keySet()) {
                    innerMap.put(toBuilding.trim().toUpperCase(), inner.getDouble(toBuilding));
                }
                result.put(fromBuilding.trim().toUpperCase(), innerMap);
            }

            System.out.println("Walking cache loaded: " + result.size() + " building entries.");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public double calculateWalking(Building b1, Building b2) {
        String from = normalize(b1.getBuildingCode());
        String to   = normalize(b2.getBuildingCode());

        return walkingTimes.getOrDefault(from, Map.of())
                .getOrDefault(to, 0.0);
    }

    private String normalize(String code) {
        if (code == null) return "";
        return code.trim().toUpperCase();
    }

    @Override
    public Timetable getTimetable() {
        return null;
    }
}

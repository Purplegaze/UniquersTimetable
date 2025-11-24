package data_access;
import entity.Building;
import usecase.calculatewalkingtime.CalculateWalkingDataAccessInterface;

import org.json.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class WalkingTimeDataAccessObject implements CalculateWalkingDataAccessInterface {
    private final Map<String, Map<String, Double>> walkingTimes;

    public WalkingTimeDataAccessObject() {
        walkingTimes = loadWalkingTimesFromJson();
    }

    private Map<String, Map<String, Double>> loadWalkingTimesFromJson() {
        Map<String, Map<String, Double>> result = new HashMap<>();
        try {
            InputStream is = getClass().getResourceAsStream("main/src/resources/walking_cache_ors.json");
            if (is == null) throw new RuntimeException("Walking cache JSON not found");

            String jsonText = new Scanner(is, StandardCharsets.UTF_8).useDelimiter("\\A").next();
            JSONObject jsonObject = new JSONObject(jsonText);

            for (String fromBuilding : jsonObject.keySet()) {
                JSONObject inner = jsonObject.getJSONObject(fromBuilding);
                Map<String, Double> innerMap = new HashMap<>();
                for (String toBuilding : inner.keySet()) {
                    innerMap.put(toBuilding, inner.getDouble(toBuilding));
                }
                result.put(fromBuilding, innerMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public double calculateWalking(Building building1, Building building2) {
        return walkingTimes.getOrDefault(building1.getBuildingCode(), Map.of())
                .getOrDefault(building2.getBuildingCode(), 0.0)
                .intValue();
    }
}

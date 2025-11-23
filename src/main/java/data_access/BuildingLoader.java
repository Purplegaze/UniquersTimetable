package data_access;
import entity.Building;

import org.json.*;
import java.nio.file.*;
import java.util.*;


public class BuildingLoader {

    public static List<Building> loadBuildings(String path) throws Exception {
        String json = Files.readString(Paths.get(path));
        JSONObject root = new JSONObject(json);
        JSONArray arr = root.getJSONArray("buildings");

        List<Building> buildings = new ArrayList<>();

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);

            String code = obj.getString("code");
            String name = obj.getString("name");
            String address = obj.getString("address");

            // latitude and longitude might be null
            Double latitude = obj.isNull("latitude") ? null : obj.getDouble("latitude");
            Double longitude = obj.isNull("longitude") ? null : obj.getDouble("longitude");

            if (latitude != null && longitude != null) {
                buildings.add(new Building(code, address, latitude, longitude));
            }
        }

        return buildings;
    }
}
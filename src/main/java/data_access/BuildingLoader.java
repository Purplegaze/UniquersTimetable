package data_access;
import entity.Building;

import org.json.*;
import java.nio.file.*;
import java.util.*;

public class BuildingLoader {

    public static List<Building> loadBuildings(String path) throws Exception {
        String json = Files.readString(Paths.get(path));
        JSONArray arr = new JSONArray(json);

        List<Building> buildings = new ArrayList<>();

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);

            String code = obj.getString("code");
            String name = obj.getString("name");
            String address = obj.getString("address");

            buildings.add(new Building(code, name, address));
        }

        return buildings;
    }
}

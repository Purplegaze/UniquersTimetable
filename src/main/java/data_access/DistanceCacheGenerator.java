package data_access;

import entity.Building;
import org.json.JSONObject;

import java.io.FileWriter;
import java.util.List;

public class DistanceCacheGenerator {

    private final List<Building> buildings;
    private final int BATCH_SIZE = 25;

    public DistanceCacheGenerator(List<Building> buildings) {
        this.buildings = buildings;
    }

    public void generateCache(String outputPath) throws Exception {
        JSONObject cache = new JSONObject();

        for (int i = 0; i < buildings.size(); i += BATCH_SIZE) {
            List<Building> originBatch = buildings.subList(i, Math.min(i + BATCH_SIZE, buildings.size()));

            for (int j = 0; j < buildings.size(); j += BATCH_SIZE) {
                List<Building> destBatch = buildings.subList(j, Math.min(j + BATCH_SIZE, buildings.size()));

                // Get addresses
                List<String> originAddresses = originBatch.stream().map(Building::getAddress).toList();
                List<String> destAddresses = destBatch.stream().map(Building::getAddress).toList();

                // Call API once for this batch
                JSONObject result = GoogleMapsAPI.getWalkingTimeBatch(originAddresses, destAddresses);

                // Parse response and populate cache
                for (int o = 0; o < originBatch.size(); o++) {
                    JSONObject timesFromOrigin = cache.optJSONObject(originBatch.get(o).getBuildingCode());
                    if (timesFromOrigin == null) {
                        timesFromOrigin = new JSONObject();
                        cache.put(originBatch.get(o).getBuildingCode(), timesFromOrigin);
                    }

                    for (int d = 0; d < destBatch.size(); d++) {
                        if (originBatch.get(o).getBuildingCode().equals(destBatch.get(d).getBuildingCode())) {
                            timesFromOrigin.put(destBatch.get(d).getBuildingCode(), "0 mins");
                        } else {
                            String duration = result.getJSONArray("rows")
                                    .getJSONObject(o)
                                    .getJSONArray("elements")
                                    .getJSONObject(d)
                                    .getJSONObject("duration")
                                    .getString("text");
                            timesFromOrigin.put(destBatch.get(d).getBuildingCode(), duration);
                        }
                    }
                }
            }
        }

        // Write cache to file
        try (FileWriter file = new FileWriter(outputPath)) {
            file.write(cache.toString(4));
        }

        System.out.println("Walking time cache generated at: " + outputPath);
    }
}

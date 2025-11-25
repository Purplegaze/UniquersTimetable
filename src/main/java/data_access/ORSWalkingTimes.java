package data_access;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ORSWalkingTimes {

    private static final OkHttpClient client = new OkHttpClient();
    private static final String ORS_KEY = System.getenv("ORS_KEY");
    private static final String MATRIX_URL = "https://api.openrouteservice.org/v2/matrix/foot-walking";
    private static final int MAX_RETRIES = 5;
    private static final int RATE_LIMIT_MS = 1600; // ~37 requests/minute

    public static void main(String[] args) throws Exception {
        String inputPath = "src/main/resources/buildings_geocoded.json";
        String outputPath = "walking_cache_ors.json";

        // Load buildings
        String text = new String(Files.readAllBytes(Paths.get(inputPath)));
        JSONObject root = new JSONObject(text);
        JSONArray buildings = root.getJSONArray("buildings");

        // Load or initialize cache
        Map<String, Map<String, Double>> cache = new HashMap<>();
        try {
            String cacheText = new String(Files.readAllBytes(Paths.get(outputPath)));
            JSONObject cacheJson = new JSONObject(cacheText);
            for (String from : cacheJson.keySet()) {
                JSONObject inner = cacheJson.getJSONObject(from);
                Map<String, Double> innerMap = new HashMap<>();
                for (String to : inner.keySet()) {
                    innerMap.put(to, inner.getDouble(to));
                }
                cache.put(from, innerMap);
            }
            System.out.println("Loaded existing cache.");
        } catch (Exception e) {
            System.out.println("No existing cache found, starting fresh.");
        }

        // Build a map of building codes to coordinates
        Map<String, double[]> buildingCoords = new HashMap<>();
        for (int i = 0; i < buildings.length(); i++) {
            JSONObject b = buildings.getJSONObject(i);
            if (!b.isNull("latitude") && !b.isNull("longitude")) {
                try {
                    double lat = b.getDouble("latitude");
                    double lon = b.getDouble("longitude");
                    buildingCoords.put(b.getString("code"), new double[]{lon, lat});
                } catch (Exception ignored) {
                }
            }
        }

        // Iterate over all building pairs
        String[] codes = buildingCoords.keySet().toArray(new String[0]);
        for (int i = 0; i < codes.length; i++) {
            String srcCode = codes[i];
            double[] srcCoords = buildingCoords.get(srcCode);

            // Build a list of target buildings that are not yet cached
            JSONArray targets = new JSONArray();
            Map<Integer, String> indexToCode = new HashMap<>();
            int idx = 0;
            for (int j = i + 1; j < codes.length; j++) {
                String tgtCode = codes[j];

                if (cache.containsKey(srcCode) && cache.get(srcCode).containsKey(tgtCode)) continue;

                targets.put(new JSONArray(buildingCoords.get(tgtCode)));
                indexToCode.put(idx, tgtCode);
                idx++;
            }

            if (targets.length() == 0) continue; // nothing to do for this source

            sendMatrixRequest(srcCode, srcCoords, targets, indexToCode, cache, outputPath);
        }

        System.out.println("Finished building walking time cache.");
    }

    private static void sendMatrixRequest(String srcCode, double[] srcCoords, JSONArray targets,
                                          Map<Integer, String> indexToCode,
                                          Map<String, Map<String, Double>> cache,
                                          String outputPath) {

        int retries = 0;
        boolean success = false;

        while (!success && retries < MAX_RETRIES) {
            try {
                JSONArray locations = new JSONArray();
                locations.put(new JSONArray(srcCoords));
                for (int i = 0; i < targets.length(); i++) {
                    locations.put(targets.getJSONArray(i));
                }

                JSONObject body = new JSONObject();
                body.put("locations", locations);
                body.put("metrics", new JSONArray().put("duration"));
                body.put("units", "m");
                body.put("resolve_locations", false);

                RequestBody requestBody = RequestBody.create(body.toString(),
                        MediaType.parse("application/json; charset=utf-8"));

                Request request = new Request.Builder()
                        .url(MATRIX_URL)
                        .post(requestBody)
                        .addHeader("Authorization", ORS_KEY)
                        .build();

                Response response = client.newCall(request).execute();

                if (response.code() == 429) { // Too many requests
                    retries++;
                    int waitTime = 1500 * retries;
                    System.out.println("429 hit for " + srcCode + ", waiting " + waitTime + "ms");
                    Thread.sleep(waitTime);
                    continue;
                }

                if (!response.isSuccessful()) {
                    System.err.println("ORS request failed for source " + srcCode + " with code: " + response.code());
                    return;
                }

                JSONObject json = new JSONObject(response.body().string());
                JSONArray durations = json.getJSONArray("durations");

                JSONArray row = durations.getJSONArray(0);
                for (int i = 1; i < row.length(); i++) {
                    String tgtCode = indexToCode.get(i - 1);
                    double durationMinutes = row.getDouble(i) / 60.0;

                    cache.computeIfAbsent(srcCode, k -> new HashMap<>()).put(tgtCode, durationMinutes);
                    cache.computeIfAbsent(tgtCode, k -> new HashMap<>()).put(srcCode, durationMinutes);
                }

                saveCache(cache, outputPath);

                // Pause to respect rate limits
                Thread.sleep(RATE_LIMIT_MS);
                success = true;

            } catch (Exception e) {
                retries++;
                int waitTime = 2000 * retries;
                System.err.println("Error processing " + srcCode + ", retry " + retries + " after " + waitTime + "ms: " + e.getMessage());
                try { Thread.sleep(waitTime); } catch (InterruptedException ignored) {}
            }
        }

        if (!success) {
            System.err.println("Failed to process source " + srcCode + " after multiple retries.");
        }
    }

    private static void saveCache(Map<String, Map<String, Double>> cache, String outputPath) {
        try (FileWriter file = new FileWriter(outputPath)) {
            JSONObject saveJson = new JSONObject();
            for (String f : cache.keySet()) {
                saveJson.put(f, new JSONObject(cache.get(f)));
            }
            file.write(saveJson.toString(2));
        } catch (Exception ex) {
            System.err.println("Error saving cache: " + ex.getMessage());
        }
    }
}

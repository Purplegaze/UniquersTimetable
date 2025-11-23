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
    private static final int MAX_LOCATIONS = 50; // ORS limit

    public static void main(String[] args) throws Exception {
        String inputPath = "src/main/resources/buildings_geocoded.json";
        String outputPath = "walking_cache_ors.json";

        // Load buildings JSON
        String text = new String(Files.readAllBytes(Paths.get(inputPath)));
        JSONObject root = new JSONObject(text);
        JSONArray buildings = root.getJSONArray("buildings");

        // Load existing cache
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
        Map<String, double[]> codeToCoords = new HashMap<>();
        for (int i = 0; i < buildings.length(); i++) {
            JSONObject b = buildings.getJSONObject(i);
            if (!b.isNull("latitude") && !b.isNull("longitude")) {
                codeToCoords.put(b.getString("code"),
                        new double[]{b.getDouble("longitude"), b.getDouble("latitude")});
            }
        }

        String[] codes = codeToCoords.keySet().toArray(new String[0]);

        // Loop through each building as source
        for (int srcIdx = 0; srcIdx < codes.length; srcIdx++) {
            String srcCode = codes[srcIdx];
            double[] srcCoords = codeToCoords.get(srcCode);

            // Determine targets that are not already cached
            JSONArray targets = new JSONArray();
            Map<Integer, String> indexToCode = new HashMap<>();
            int idx = 0;

            for (int tgtIdx = 0; tgtIdx < codes.length; tgtIdx++) {
                String tgtCode = codes[tgtIdx];
                if (srcCode.equals(tgtCode)) continue;

                // Skip if already cached
                if (cache.containsKey(srcCode) && cache.get(srcCode).containsKey(tgtCode)) continue;

                double[] tgtCoords = codeToCoords.get(tgtCode);
                targets.put(new JSONArray(tgtCoords));
                indexToCode.put(idx, tgtCode);
                idx++;

                // Send request in batches of MAX_LOCATIONS-1 (since src counts as 1)
                if (targets.length() == MAX_LOCATIONS - 1) {
                    sendMatrixRequest(srcCode, srcCoords, targets, indexToCode, cache);
                    targets = new JSONArray();
                    indexToCode = new HashMap<>();
                    idx = 0;
                }
            }

            // Send remaining targets if any
            if (targets.length() > 0) {
                sendMatrixRequest(srcCode, srcCoords, targets, indexToCode, cache);
            }
        }

        // Save cache at the end
        saveCache(cache, outputPath);
        System.out.println("Walking time cache saved to " + outputPath);
    }

    private static void sendMatrixRequest(String srcCode, double[] srcCoords, JSONArray targets,
                                          Map<Integer, String> indexToCode,
                                          Map<String, Map<String, Double>> cache) {
        try {
            // Build locations array: source first
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
            if (!response.isSuccessful()) {
                System.err.println("ORS request failed for source " + srcCode + " with code: " + response.code());
                return;
            }

            JSONObject json = new JSONObject(response.body().string());
            JSONArray durations = json.getJSONArray("durations"); // durations in seconds

            // First row is source → targets
            JSONArray row = durations.getJSONArray(0);
            for (int i = 1; i < row.length(); i++) {
                String tgtCode = indexToCode.get(i - 1);
                double durationMinutes = row.getDouble(i) / 60.0;

                cache.computeIfAbsent(srcCode, k -> new HashMap<>()).put(tgtCode, durationMinutes);
                cache.computeIfAbsent(tgtCode, k -> new HashMap<>()).put(srcCode, durationMinutes);
            }

            // Save cache after each successful request
            saveCache(cache, "walking_cache_ors.json");

        } catch (Exception e) {
            System.err.println("Error processing source " + srcCode + ": " + e.getMessage());
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

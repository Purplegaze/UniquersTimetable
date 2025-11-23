package data_access;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ORSWalkingTimes {

    private static final OkHttpClient client = new OkHttpClient();
    private static final String ORS_KEY = System.getenv("ORS_KEY");
    private static final String MATRIX_URL = "https://api.openrouteservice.org/v2/matrix/foot-walking";
    private static final int BATCH_SIZE = 40; // safe batch size for ORS free tier

    public static void main(String[] args) throws Exception {
        String inputPath = "src/main/resources/buildings_geocoded.json";
        String outputPath = "walking_cache_ors.json";

        // Load buildings JSON
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

        // Build coordinates list and index map
        List<double[]> allCoords = new ArrayList<>();
        Map<Integer, String> indexToCode = new HashMap<>();
        for (int i = 0; i < buildings.length(); i++) {
            JSONObject b = buildings.getJSONObject(i);
            if (!b.isNull("latitude") && !b.isNull("longitude")) {
                allCoords.add(new double[]{b.getDouble("longitude"), b.getDouble("latitude")});
                int coordIndex = allCoords.size() - 1;
                indexToCode.put(coordIndex, b.getString("code"));
            }
        }

        // Generate batches for all combinations using sliding windows
        int total = allCoords.size();
        for (int iStart = 0; iStart < total; iStart += BATCH_SIZE) {
            int iEnd = Math.min(iStart + BATCH_SIZE, total);
            for (int jStart = iStart; jStart < total; jStart += BATCH_SIZE) {
                int jEnd = Math.min(jStart + BATCH_SIZE, total);

                // Build batch coordinates
                JSONArray batch = new JSONArray();
                Map<Integer, String> batchIndexToCode = new HashMap<>();
                Map<Integer, Integer> batchToGlobalIndex = new HashMap<>();
                int batchIndex = 0;
                for (int i = iStart; i < iEnd; i++) {
                    for (int j = jStart; j < jEnd; j++) {
                        // Only add coords for the larger ORS call (start batch at min index)
                        if (i == jStart) {
                            batch.put(new JSONArray(allCoords.get(j)));
                            batchIndexToCode.put(batchIndex, indexToCode.get(j));
                            batchToGlobalIndex.put(batchIndex, j);
                            batchIndex++;
                        }
                    }
                }

                // Skip if batch empty
                if (batch.length() == 0) continue;

                JSONObject body = new JSONObject();
                body.put("locations", batch);
                body.put("metrics", new JSONArray().put("duration"));
                body.put("units", "m");
                body.put("resolve_locations", false);

                RequestBody requestBody = RequestBody.create(body.toString(),
                        MediaType.parse("application/json; charset=utf-8"));

                try {
                    Request request = new Request.Builder()
                            .url(MATRIX_URL)
                            .post(requestBody)
                            .addHeader("Authorization", ORS_KEY)
                            .build();

                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        System.err.println("ORS request failed with code: " + response.code());
                        continue; // skip this batch but continue
                    }

                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray durations = json.getJSONArray("durations");

                    // Map durations to cache
                    for (int m = 0; m < durations.length(); m++) {
                        for (int n = m + 1; n < durations.length(); n++) {
                            String codeFrom = batchIndexToCode.get(m);
                            String codeTo = batchIndexToCode.get(n);

                            // Skip if already cached
                            if (cache.containsKey(codeFrom) && cache.get(codeFrom).containsKey(codeTo)) continue;

                            double durationMinutes = durations.getJSONArray(m).getDouble(n) / 60.0;

                            cache.computeIfAbsent(codeFrom, k -> new HashMap<>()).put(codeTo, durationMinutes);
                            cache.computeIfAbsent(codeTo, k -> new HashMap<>()).put(codeFrom, durationMinutes);
                        }
                    }

                } catch (Exception e) {
                    System.err.println("Error processing batch " + iStart + "-" + iEnd + " vs " + jStart + "-" + jEnd + ": " + e.getMessage());
                } finally {
                    // Always save cache after each batch
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
        }

        System.out.println("Walking time cache saved to " + outputPath);
    }
}

package data_access;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class GoogleGeocodingAPI {

    private static final OkHttpClient client = new OkHttpClient();
    private static final String API_KEY = System.getenv("API_KEY");
    private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    public static void main(String[] args) throws Exception {
        System.out.println(System.getenv("API_KEY"));
        String inputPath = "src/main/resources/buildings.json";
        String outputPath = "buildings_geocoded.json";

        // Load JSON file
        String text = new String(Files.readAllBytes(Paths.get(inputPath)));
        JSONObject root = new JSONObject(text);
        JSONArray buildings = root.getJSONArray("buildings");

        // Cache for previously geocoded addresses
        Map<String, double[]> cache = new HashMap<>();

        // Loop through buildings
        for (int i = 0; i < buildings.length(); i++) {

            JSONObject b = buildings.getJSONObject(i);
            String address = b.getString("address");

            System.out.println("Processing: " + address);
            try{
                double[] latlng;

                if (cache.containsKey(address)) {
                    // Reuse cached coordinates
                    latlng = cache.get(address);
                    System.out.println("Using cached coordinates");
                } else {
                    // Geocode and store in cache
                    latlng = geocode(address);
                    cache.put(address, latlng);
                    // Small delay to avoid hitting rate limits
                    Thread.sleep(2000);
                }

                b.put("latitude", latlng[0]);
                b.put("longitude", latlng[1]);

            } catch (Exception e) {
                System.err.println("Failed for " + address + ": " + e.getMessage());
                b.put("latitude", JSONObject.NULL);
                b.put("longitude", JSONObject.NULL);
            }

            // Save after each iteration to preserve progress
            try (FileWriter file = new FileWriter(outputPath)) {
                file.write(root.toString(2)); // pretty print
            }
        }

        System.out.println("Output saved to " + outputPath);
    }

    public static double[] geocode(String address) throws Exception {

        String encoded = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String url = GEOCODE_URL + "?address=" + encoded + "&key=" + API_KEY;

        Request request =  new Request.Builder()
                .url(url)
                .header("User-Agent", "Java-Geocoding-Client")
                .build();

        Response response = client.newCall(request).execute();

        JSONObject json = new JSONObject(response.body().string());

        if (!json.getString("status").equals("OK")) {
            System.err.println("ERROR: Could not geocode: " + address);
            throw new RuntimeException(json.getString("status"));
        }

        JSONObject location = json.getJSONArray("results")
                .getJSONObject(0)
                .getJSONObject("geometry")
                .getJSONObject("location");

        return new double[]{
                location.getDouble("lat"),
                location.getDouble("lng")
        };
    }
}

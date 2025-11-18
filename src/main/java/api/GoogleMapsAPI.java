package api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.*;

public class GoogleMapsAPI {
    private static final OkHttpClient client = new OkHttpClient();
    private static final String API_KEY = System.getenv("API_KEY");
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";

    public static String getWalkingTime(String origin, String destination) throws Exception {
        String mapsurl = BASE_URL + "?origins=" + origin.replace(" ", "+") +
                "&destinations=" + destination.replace(" ", "+") + "&mode=walking"
                + "&key=" + API_KEY;

        Request request = new Request.Builder().url(mapsurl).build();
        Response response = client.newCall(request).execute();

        String json = response.body().string();

        JSONObject jsonObject = new JSONObject(json);
        JSONObject element = jsonObject.getJSONArray("rows").getJSONObject(0).
                getJSONArray("elements").getJSONObject(0);

        return element.getJSONObject("duration").getString("text");
    }
}


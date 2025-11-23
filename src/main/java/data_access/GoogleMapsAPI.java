package data_access;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.*;

import java.util.List;

public class GoogleMapsAPI {
    private static final int BATCH_SIZE = 25;
    private static final OkHttpClient client = new OkHttpClient();
    private static final String API_KEY = System.getenv("API_KEY");
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";

//    public static String getWalkingTime(String origin, String destination) throws Exception {
//        String mapsurl = BASE_URL + "?origins=" + origin.replace(" ", "+") +
//                "&destinations=" + destination.replace(" ", "+") + "&mode=walking"
//                + "&key=" + API_KEY;
//
//        Request request = new Request.Builder().url(mapsurl).build();
//        Response response = client.newCall(request).execute();
//
//        String json = response.body().string();
//
//        JSONObject jsonObject = new JSONObject(json);
//        JSONObject element = jsonObject.getJSONArray("rows").getJSONObject(0).
//                getJSONArray("elements").getJSONObject(0);
//
//        return element.getJSONObject("duration").getString("text");
//    }

    //Batch calling API
    public static JSONObject getWalkingTimeBatch(List<String> origins, List<String> destinations) throws Exception {
        String originsParam = String.join("|", origins).replace(" ", "+");
        String destinationsParam = String.join("|", destinations).replace(" ", "+");

        String url = BASE_URL + "?origins=" + originsParam +
                "&destinations=" + destinationsParam +
                "&mode=walking&key=" + API_KEY;

        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        String json = response.body().string();

        return new JSONObject(json);
    }
}


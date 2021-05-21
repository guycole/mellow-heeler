package net.braingang.mellow_heeler;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *  geographic location container
 */
public class GeoLoc {
    private Long fixTimeMs;
    private Float accuracy;
    private Double altitude;
    private Double latitude;
    private Double longitude;
    private String provider;

    public GeoLoc(Location location) {
        accuracy = location.getAccuracy();
        altitude = location.getAltitude();
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        provider = location.getProvider();
        fixTimeMs = location.getTime();
    }

    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("fixTimeMs", fixTimeMs);
        jsonObject.put("accuracy", accuracy);
        jsonObject.put("altitude", altitude);
        jsonObject.put("latitude", latitude);
        jsonObject.put("longitude", longitude);
        jsonObject.put("provider", provider);

        return jsonObject;
    }
}

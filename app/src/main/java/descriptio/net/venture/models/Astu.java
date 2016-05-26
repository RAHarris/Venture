package descriptio.net.venture.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import descriptio.net.venture.io.JsonReader;

/**
 * Created by rahar on 10/7/2015.
 */
public class Astu {

    public final long id;
    private String name;
    private String overview;
    private String region;
    private String imageUrl;
    private Map<int[], String> periods;

    private List<Thauma> topoi;

    private final String LOGCAT_TAG = "Astu";

    public Astu(InputStream in, long id) throws IOException{
        try {
            JsonReader reader = new JsonReader(in);
            this.name = reader.getAstuName();
            this.overview = reader.getAstuRegion();
            this.region = reader.getAstuRegion();
            this.imageUrl = reader.getAstuImageUrl();
            this.topoi = reader.getAstuThaumata();
        } catch (JSONException e) {
            // alert the user in some way that one of the astea is wonky
            Log.e(LOGCAT_TAG, "json error", e);
        }
        this.id = id;
    }

    public Astu(JSONObject object, long id) {
        try {
            JsonReader reader = new JsonReader(object);
            this.name = reader.getAstuName();
            this.overview = reader.getAstuRegion();
            this.region = reader.getAstuRegion();
            this.imageUrl = reader.getAstuImageUrl();
            this.topoi = reader.getAstuThaumata();
        } catch (JSONException e) {
            Log.e(LOGCAT_TAG, "json error", e);
        }
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public String getOverview() { return this.overview; }

    public String getRegion() {
        return this.region;
    }

    public String getImageUrl() { return this.imageUrl; }

    public List<Thauma> getThaumata() { return this.topoi; }

    @Override
    public String toString() {
        String name = this.name != null ? this.name : "";
        String overview = this.overview != null ? this.overview : "";
        String region = this.region != null ? this.region : "";

        return "astu name: " + name + ", astu id: " + this.id + ", astu overview: " + overview + ", astu region: " + region + ", topoi: " + (topoi == null ? 0: topoi.size());
    }
}

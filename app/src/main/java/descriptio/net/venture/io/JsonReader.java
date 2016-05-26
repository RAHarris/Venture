package descriptio.net.venture.io;
        import android.util.Log;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.util.ArrayList;
        import java.util.List;

        import descriptio.net.venture.models.Astu;
        import descriptio.net.venture.models.Thauma;

/**
 * Created by rahar on 10/7/2015.
 */

public class JsonReader {

    private final String LOGCAT_TAG = "JsonReader";
    private JSONObject json;

    public JsonReader(InputStream in) throws JSONException {

        JSONObject temp_json = new JSONObject( loadText(in) );
        json = temp_json.getJSONObject("city");
    }

    public JsonReader(JSONObject obj) throws JSONException {
        json = obj.getJSONObject("city");
    }

    public String getAstuName() throws JSONException {
        return json.getString("name");
    }

    public String getAstuRegion() throws JSONException {
        return json.getString("region");
    }

    public String getAstuImageUrl() throws JSONException {
        return json.getString("image");
    }

    public ArrayList<Thauma> getAstuThaumata() throws JSONException {
        ArrayList<Thauma> city_topoi = new ArrayList<Thauma>();
        JSONArray loci = json.getJSONArray("locations");
        for (int i = 0; i < loci.length(); i++) {
            JSONObject obj = loci.getJSONObject(i);
            JSONObject coordinates = obj.getJSONObject("coords");
            double[] coords = new double[]{coordinates.getDouble("lat"), coordinates.getDouble("lng")};
            Thauma data = new Thauma(obj.getString("name"), obj.getString("summary"), obj.getInt("uid"), coords);
            city_topoi.add(data);
        }
        return city_topoi;
    }

    public Thauma getThauma(int uid) throws JSONException {
        JSONArray thaumata = json.getJSONArray("locations");
        for (int i = 0; i < thaumata.length(); i ++) {
            JSONObject obj = thaumata.getJSONObject(i);
            if (obj.getInt("uid") == uid) {
                JSONObject coordinates = obj.getJSONObject("coords");
                double[] coords = new double[]{coordinates.getDouble("lat"), coordinates.getDouble("lng")};
                return new Thauma(obj.getString("name"), obj.getString("summary"), obj.getInt("uid"), coords);
            }
        }
        return null;
    }

    private String loadText(InputStream in) {
        Log.i(LOGCAT_TAG, "starting to load text");
        StringBuilder out = new StringBuilder();
        BufferedReader buff = new BufferedReader( new InputStreamReader(in) );
        try {
            String new_line = buff.readLine();
            while (new_line != null) {
                out.append(new_line);
                new_line = buff.readLine();
            }
        }
        catch (IOException e) {
            Log.e("LocalJsonLoader", "error reading periegesis file from local storage", e);
        }
        finally {
            if (buff != null) { try { buff.close(); } catch (IOException e) { Log.e("LocalJsonLoader", "really goddamn weird error closing the buffered reader", e); } }
        }
        return out.toString();
    }

    private List<int[]> makeIntArray(JSONArray array) throws JSONException {
        List<int[]> result = new ArrayList<int[]>();
        for (int i = 0; i < array.length(); i++) {
            JSONArray period_array = array.getJSONArray(i);
            int[] times = new int[2];
            for (int j = 0; j < times.length; j++) {
                times[j] = array.getInt(j);
            }
            result.add(times);
        }
        return result;
    }
}

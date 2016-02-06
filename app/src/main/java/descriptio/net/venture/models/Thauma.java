package descriptio.net.venture.models;

import java.util.List;

/**
 * Created by rahar on 10/7/2015.
 */
public class Thauma {

    private String name;
    private String overview;
    private String description;
    private int uid;
    private Astu parent;
    private List<int[]> periods;
    private boolean active;
    private double[] coords;

    public Thauma(String name, String overview, String description, int uid, boolean active, double[] coords) {
        this.name = name;
        this.overview = overview;
        this.description = description;
        this.uid = uid;
        this.active = active;
        this.coords = coords;
    }

    public Thauma(String name, String overview, int uid, boolean active, double[] coords) {
        this(name, overview, "", uid, active, coords);
    }

    public Thauma(String name, String overview, int uid, String description, double[] coords) {
        this(name, overview, description, uid, false, coords);
    }

    public Thauma(String name, String overview, int uid, double[] coords) {
        this(name, overview, "", uid, false, coords);
    }

    public String getName() {
        return this.name;
    }

    public String getOverview() {
        return this.overview;
    }

    public String getDescription() {
        if (!(this.description == null) && this.description.length() > 0) {
            return this.description;
        } else {
            return this.overview;
        }
    }

    public int getUid() {
        return this.uid;
    }

    public Astu getParent() {
        return this.parent;
    }

    public double[] getCoords() { return this.coords; }
}

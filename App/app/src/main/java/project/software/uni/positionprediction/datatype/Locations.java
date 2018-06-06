package project.software.uni.positionprediction.datatype;

import java.util.ArrayList;

/**
 * Elementary class for holding a number of locations.
 * This will be implemented e.g. by a single trajectory type.
 *
 * TODO: merge stuff from GeoUtils into here.
 */
public abstract class Locations {

    public ArrayList<Location> locs;

    public Locations() { }

    public Locations(ArrayList<Location> locs) {
        this.locs = locs;
    }

    public Locations add(Location loc) {
        this.locs.add(loc);
        return this;
    }

    public Location get(int i) {
        return this.locs.get(i);
    }

    public Locations addAll(ArrayList<Location> locs) {
        this.locs.addAll(locs);
        return this;
    }
}

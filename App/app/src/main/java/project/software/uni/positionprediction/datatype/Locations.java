package project.software.uni.positionprediction.datatype;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Elementary class for holding a number of locations.
 * This will be implemented e.g. by a single trajectory type.
 *
 * TODO: merge stuff from GeoUtils into here.
 */
public abstract class Locations {

    public ArrayList<Location> locs = new ArrayList<>();

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

    public Locations addAll(Locations locs) {
        this.locs.addAll(locs.locs);
        return this;
    }

    public Iterator<Location> iterator() {
        return this.locs.iterator();
    }

    public int getLength() {
        return this.locs.size();
    }
}

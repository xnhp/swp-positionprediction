package project.software.uni.positionprediction.datatype;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Elementary class for holding an unordered set of clouds
 */
public abstract class Cloud {

    public HashSet<Location> locs;

    public Cloud(HashSet<Location> locs) {
        this.locs = locs;
    }

    public Cloud add(Location loc) {
        this.locs.add(loc);
        return this;
    }

    public Cloud remove(Location loc) {
        this.locs.remove(loc);
        return this;
    }

    public Iterator<Location> iterator() {
        return this.locs.iterator();
    }


}

package project.software.uni.positionprediction.datatype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import project.software.uni.positionprediction.util.Dimension;

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

    public Location getCenter(){

        Dimension dim = Dimension.LON;
        double lon_min = Collections.min(locs, new LocationsCompararator(dim)).getDimension(dim);
        double lon_max = Collections.max(locs, new LocationsCompararator(dim)).getDimension(dim);
        dim = Dimension.LAT;
        double lat_min = Collections.min(locs, new LocationsCompararator(dim)).getDimension(dim);
        double lat_max = Collections.max(locs, new LocationsCompararator(dim)).getDimension(dim);

        double lon = (lon_min + lon_max) / 2;
        double lat = (lat_min + lat_max) / 2;

        return new Location(lon, lat);
    }

    public double getSpread(){

        Dimension dim = Dimension.LON;
        double lon_min = Collections.min(locs, new LocationsCompararator(dim)).getDimension(dim);
        double lon_max = Collections.max(locs, new LocationsCompararator(dim)).getDimension(dim);
        dim = Dimension.LAT;
        double lat_min = Collections.min(locs, new LocationsCompararator(dim)).getDimension(dim);
        double lat_max = Collections.max(locs, new LocationsCompararator(dim)).getDimension(dim);

        double lonSpread = lon_max - lon_min;
        double latSpread = lat_max - lat_min;

        return Math.max(lonSpread, latSpread);
    }

    private class LocationsCompararator implements Comparator<Location> {
        Dimension dim;
        public LocationsCompararator(Dimension dim){
            this.dim = dim;
        };

        public final int compare(Location locA, Location locB){return 0;}

        public final int compare(Location locA, Location locB, Dimension dim){
            double a = locA.getDimension(dim);
            double b = locB.getDimension(dim);
            if(a < b){
                return -1;
            } else if(a == b){
                return 0;
            } else {
                return 1;
            }
        }
    }
}

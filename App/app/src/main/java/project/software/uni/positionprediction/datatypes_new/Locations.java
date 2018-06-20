package project.software.uni.positionprediction.datatypes_new;

import java.util.Collections;
import java.util.Map;

import project.software.uni.positionprediction.util.Dimension;


public class Locations extends Collection<Location> {

    public Locations() { super(); }
    public Locations(Locations locs) { super(locs); }
    public Locations(Location loc) { super(loc); }

    /**
     * Methods inherited from ArrayList<Location>:
     * - boolean add(Location)
     * - boolean addAll(Locations)
     * - Location get(int)
     * - iterator()
     */

    /*
    public Locations addAll(Locations locs){
        return this.addAll(locs);
    }
    */

    /* // mere renaming
    public int getLength() { return this.size(); }
    */


    public Map<String, Double> getBounds(){

        Map<String, Double> bounds = null;
        Dimension dim;

        dim = Dimension.LON;
        bounds.put("lon_min", Collections.min(this, new LocationsComparator(dim)).getDimension(dim));
        bounds.put("lon_max", Collections.max(this, new LocationsComparator(dim)).getDimension(dim));

        dim = Dimension.LAT;
        bounds.put("lat_min", Collections.min(this, new LocationsComparator(dim)).getDimension(dim));
        bounds.put("lat_max", Collections.max(this, new LocationsComparator(dim)).getDimension(dim));

        return bounds;
    }

    public Location getCenter(){

        Map<String, Double> bounds = getBounds();

        double lon = ( bounds.get("lon_min") + bounds.get("lon_max") ) / 2;
        double lat = ( bounds.get("lat_min") + bounds.get("lat_max") ) / 2;

        return new Location(lon, lat);
    }

    public double getSpread(){

        Map<String, Double> bounds = getBounds();

        double lonSpread = bounds.get("lon_max") - bounds.get("lon_min");
        double latSpread = bounds.get("lat_max") - bounds.get("lat_min");

        return Math.max(lonSpread, latSpread);
    }

}

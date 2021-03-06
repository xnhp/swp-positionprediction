package project.software.uni.positionprediction.datatypes;

import org.osmdroid.util.BoundingBox;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;


public class Locations extends Collection<Location> {

    public Locations() { super(); }
    public Locations(Locations locs) { super(locs); }
    public Locations(Collection<Location> locs) { super(locs); }
    public Locations(Location loc) { super(loc); }

    /**
     * Methods inherited from ArrayList<Location>:
     * - boolean add(Location)
     * - boolean addAll(Locations)
     * - Location get(int)
     * - locationsIterator()
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
        EDimension dim;

        dim = EDimension.LON;
        bounds.put("lon_min", Collections.min(this, new LocationsComparator(dim)).getDimension(dim));
        bounds.put("lon_max", Collections.max(this, new LocationsComparator(dim)).getDimension(dim));

        dim = EDimension.LAT;
        bounds.put("lat_min", Collections.min(this, new LocationsComparator(dim)).getDimension(dim));
        bounds.put("lat_max", Collections.max(this, new LocationsComparator(dim)).getDimension(dim));

        return bounds;
    }

    public Location getLast(){
        return this.get(this.size()-1);
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

    public BoundingBox getBoundingBox() {

        EDimension dim;
        double north, south, east, west;

        dim = EDimension.LON;
        west = Collections.min(this, new LocationsComparator(dim)).getDimension(dim);
        east = Collections.max(this, new LocationsComparator(dim)).getDimension(dim);

        dim = EDimension.LAT;
        south = Collections.min(this, new LocationsComparator(dim)).getDimension(dim);
        north = Collections.max(this, new LocationsComparator(dim)).getDimension(dim);

        return new BoundingBox(north, east, south, west);
    }

    public boolean haveValues() {
        return this.size() != 0 && this.get(0) instanceof LocationWithValue;
    }


    public Date getMostRecentDate() {
        int size = this.size();
        Location lastLocation = this.get(size -1);
        if(lastLocation instanceof LocationWithValue){
            LocationWithValue lastLocationWithValue = (LocationWithValue) lastLocation;
            if(lastLocationWithValue.getValue() instanceof  Date){
                return (Date) lastLocationWithValue.getValue();
            }
        }
        throw new InvalidParameterException();
    }


}

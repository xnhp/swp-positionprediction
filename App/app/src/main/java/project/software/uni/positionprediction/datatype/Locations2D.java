package project.software.uni.positionprediction.datatype;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class Locations2D {

    // Class variables
    ArrayList<Location2D> locations;

    // Constructor (empty trajectory)
    public Locations2D() {
        this.locations = new ArrayList<Location2D>();
    }

    // Constructor (from ArrayList of locations)
    public Locations2D(ArrayList<Location2D> locations) {
        this.locations = locations;
    }

    // Constructor (from String representation "long1,lat1; long2,lat2" of locations)
    public Locations2D(String coordsString) {
        ArrayList<Location2D> locations = new ArrayList<Location2D>();
        String[] coordStrings = coordsString.split(";");
        for (String coordString : coordStrings) {
            String[] coords = coordString.split(",");
            Location2D location = new Location2D(
                    Double.parseDouble(coords[0]),
                    Double.parseDouble(coords[1])
            );
            locations.add(location);;
        }
        this.locations = locations;
    }

    // Getters & Setters
    public List<GeoPoint> getGeoPoints(){
        List<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
        for (Location2D location : locations) {
            geoPoints.add(new GeoPoint(location.getLoc_long(), location.getLoc_lat()));
        }
        return geoPoints;
    }

    // Getters & Setters
    public List<IGeoPoint> getIGeoPoints(){
        List<IGeoPoint> iGeoPoints = new ArrayList<IGeoPoint>();
        for (Location2D location : locations) {
            iGeoPoints.add(new GeoPoint(location.getLoc_long(), location.getLoc_lat()));
        }
        return iGeoPoints;
    }

}

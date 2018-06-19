package project.software.uni.positionprediction.util;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import project.software.uni.positionprediction.datatypes_new.Location;

/**
 * Utility methods for manipulating geographical data (types).
 * TJ: Not needed at the moment.
 */
public abstract class GeoDataUtils_new {

    /**
     * Cast down each element to osmdroid's GeoPoint from IGeoPoint
     * @param points
     * @return an ArrayList of GeoPoints
     */
    public static List<GeoPoint> IGeoPointsToGeoPoints(List<IGeoPoint> points) {
        List<GeoPoint> newPoints = new ArrayList<>();
        for (IGeoPoint pt : points) {
            newPoints.add((GeoPoint) pt);
        }
        return newPoints;
    }

    // TODO: redundant. TJ: Why?
    public static List<IGeoPoint> GeoPointsToIGeoPoints(List<GeoPoint> points) {
        List<IGeoPoint> newPoints = new ArrayList<>();
        for (GeoPoint pt : points) {
            newPoints.add((IGeoPoint) pt);
        }
        return newPoints;
    }

    /**
     * convert a list of `Location3D`s to osmdroid's `GeoPoint`s.
     * @param points
     * @return
     */
    public static List<GeoPoint> ListLocationToGeoPoint(ArrayList<Location> points) {
        List<GeoPoint> newPoints = new ArrayList<>();
        for (Location loc : points) {
            newPoints.add(new GeoPoint(loc.getLat(), loc.getLon()));
        }
        return newPoints;
    }

    public static GeoPoint LocationToGeoPoint(Location loc) {
        return new GeoPoint(loc.getLat(), loc.getLon());
    }
}

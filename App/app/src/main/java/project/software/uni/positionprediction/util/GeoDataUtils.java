package project.software.uni.positionprediction.util;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import project.software.uni.positionprediction.datatypes.Location;

/**
 * Utility methods for manipulating geographical data (types).
 */
public abstract class GeoDataUtils {

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

    public static List<IGeoPoint> GeoPointsToIGeoPoints(List<GeoPoint> points) {
        List<IGeoPoint> newPoints = new ArrayList<>();
        newPoints.addAll(points);
        return newPoints;
    }


    public static GeoPoint LocationToGeoPoint(Location loc) {
        return new GeoPoint(loc.getLat(), loc.getLon());
    }
}

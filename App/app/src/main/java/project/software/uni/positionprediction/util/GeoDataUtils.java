package project.software.uni.positionprediction.util;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import project.software.uni.positionprediction.datatypes.Location;

/**
 * Utility methods for manipulating geographical data (types).
 * TJ: Not needed at the moment.
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

    // TODO: redundant. TJ: Why?
    public static List<IGeoPoint> GeoPointsToIGeoPoints(List<GeoPoint> points) {
        List<IGeoPoint> newPoints = new ArrayList<>();
        for (GeoPoint pt : points) {
            newPoints.add((IGeoPoint) pt);
        }
        return newPoints;
    }


    /* TJ 180622
    public static List<GeoPoint> ListLocationToGeoPoint(ArrayList<Location3D> points) {
        List<GeoPoint> newPoints = new ArrayList<>();
        for (Location3D loc : points) {
            newPoints.add(new GeoPoint(loc.getLoc_lat(), loc.getLoc_long()));
        }
        return newPoints;
    }

    public static GeoPoint Location3DToGeoPoint(Location3D loc) {
        return new GeoPoint(loc.getLoc_lat(), loc.getLoc_long());
    }
    */

    public static GeoPoint LocationToGeoPoint(Location loc) {
        return new GeoPoint(loc.getLat(), loc.getLon());
    }
}

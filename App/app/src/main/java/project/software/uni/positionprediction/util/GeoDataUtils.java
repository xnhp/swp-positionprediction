package project.software.uni.positionprediction.util;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import project.software.uni.positionprediction.datatype.Location3D;

/**
 * Utility methods for manipulating geographical data (types).
 */
public abstract class GeoDataUtils {

    /**
     * Cast down each element to osmdroid's GeoPoint from IGeoPoint
     * @param points
     * @return an ArrayList of GeoPoints
     */
    public static List<GeoPoint> castDownGeoPointList(List<IGeoPoint> points) {
        List<GeoPoint> newPoints = new ArrayList<>();
        for (IGeoPoint pt : points) {
            newPoints.add((GeoPoint) pt);
        }
        return newPoints;
    }

    // TODO: redundant
    public static List<IGeoPoint> ListGeoPointToIGeoPoint(List<GeoPoint> points) {
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
    public static List<GeoPoint> ListLocationToGeoPoint(ArrayList<Location3D> points) {
        List<GeoPoint> newPoints = new ArrayList<>();
        for (Location3D loc : points) {
            newPoints.add(new GeoPoint(loc.getLoc_lat(), loc.getLoc_long()));
        }
        return newPoints;
    }
}

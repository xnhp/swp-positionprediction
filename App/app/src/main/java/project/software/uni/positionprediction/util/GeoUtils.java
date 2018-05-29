package project.software.uni.positionprediction.util;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for manipulating geographical data.
 */
public abstract class GeoUtils {

    /**
     * Cast down each element to GeoPoint from IGeoPoint
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
}

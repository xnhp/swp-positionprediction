package project.software.uni.positionprediction.cesium;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import java.util.Iterator;

import project.software.uni.positionprediction.datatypes.Collection;
import project.software.uni.positionprediction.datatypes.Location;
import project.software.uni.positionprediction.datatypes.Locations;
import project.software.uni.positionprediction.visualisation.CloudVis;
import project.software.uni.positionprediction.visualisation.Polygon;
import project.software.uni.positionprediction.visualisation.Polyline;
import project.software.uni.positionprediction.visualisation.StyledLineSegment;
import project.software.uni.positionprediction.visualisation.StyledPoint;
import project.software.uni.positionprediction.visualisation.TrajectoryVis;
import project.software.uni.positionprediction.visualisation.Visualisation;

/**
 *
 */
public class JSONUtils {

    /**
     * Get the JSON string for a single traj vis.
     * This can also be called as a helper method for
     * assembling a bigger JSONObject for a big
     * `Visualisations` hashmap
     *
     * {
     *     "type": "single_trajectory"
     *     "polyline": [
     *         ...
     *     ],
     *     "funnel": [
     *         ...
     *     ]
     * }
     *
     * 'funnel' entry is only present if available.
     *
     * @return
     */
    public static JSONObject getSingleTrajJSON(TrajectoryVis trajVis) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("type", "single_trajectory");
        jo.put("polyline", getPolyLineJSON(trajVis.getLine()));
        if (trajVis.hasFunnel()) {
            jo.put("funnel", getFunnelJSON(trajVis.getFunnel()));
        }
        return jo;
    }


    private static JSONArray getFunnelJSON(Polygon funnel) throws JSONException {
        // skipping the step in the inheritance chain over `Polygon`.
        return getLocationsJSONArr(funnel.locations);
    }


    /**
     * [
     *  [[Location]],
     *  [[Location]],
     *  ...
     * ]
     * @param locations
     * @return
     * @throws JSONException
     */
    private static JSONArray getLocationsJSONArr(Locations locations) throws JSONException {
        JSONArray a = new JSONArray();
        Iterator<Location> lIt = locations.iterator();
        while (lIt.hasNext()) {
            Location l = lIt.next();
            a.put(getLocationJSON(l));
        }
        return a;
    }

    /**
     * {
     *     "location": [[cf getLocationJSON]]
     *     "point_color": [[String]]
     *     "point_radius": [[Int]]
     * }
     * @param pt
     * @return
     * @throws JSONException
     */
    public static JSONObject getStyledPointJSON(StyledPoint pt) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("location", getLocationJSON(pt.location));
        jo.put("point_color", pt.pointColor);
        jo.put("point_radius", pt.pointRadius);
        return jo;
    }


    /**
     * Returns a JSONObject holding the information of a Polyline. Example:
     *
     * {
     *     "type": "polyline",
     *     "styled_points": [
     *          [[cf getStyledPointJSON]]
     *          ...
     *     ],
     *     "styled_line_segments"; [
     *          [[cf getStyledLineSegmentJSON]]
     *     ]
     * }
     *
     *
     * @param pline
     * @return
     * @throws JSONException
     */
    public static JSONObject getPolyLineJSON(Polyline pline) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("type", "polyline");
        Iterator<StyledPoint> it = pline.styledPoints.iterator();
        JSONArray points = new JSONArray();
        while (it.hasNext()) {
            StyledPoint next = it.next();
            points.put(
              getStyledPointJSON(next)
            );
        }

        JSONArray linesegs = new JSONArray();
        Iterator<StyledLineSegment> linesegIt = pline.styledLineSegments.iterator();
        while (linesegIt.hasNext()) {
            StyledLineSegment next = linesegIt.next();
            linesegs.put(
                    getStyledLineSegJSON(next)
            );
        }

        // attach locations array to result object
        jo.put("styled_points", points);
        jo.put("styled_line_segments", linesegs);

        return jo;
    }


    /**
     * {
     *     "start": [[locationJSON]]
     *     "end": [[locationJSON]]
     *     "line_color": [[String]]
     * }
     *
     * @param seg
     * @return
     * @throws JSONException
     */
    private static JSONObject getStyledLineSegJSON(StyledLineSegment seg) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("start", getLocationJSON(seg.start));
        jo.put("end", getLocationJSON(seg.end));
        jo.put("line_color", seg.lineColor);
        return jo;
    }

    /**
     * {
     *     "lat": [[double]]
     *     "lon": [[double]]
     * }
     *
     * Note that altitude is only set if hasAltititude is true
     *
     * todo: converting to a JSONObject and then to a JSON string most likely
     * todo: casting or truncating the double. Have to see if that has a negative
     * todo: effect.
     *
     * @param l
     * @return
     * @throws JSONException
     */
    public static JSONObject getLocationJSON(Location l) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("lat", l.getLat());
        jo.put("lon", l.getLon());
        if (l.hasAltitude()) {
            jo.put("alt", l.getAlt());
        }
        return jo;
    }

    public static JSONObject getAndroidLocationJSON(android.location.Location l) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("lat", l.getLatitude());
        jo.put("lon", l.getLongitude());
        if (l.hasAltitude()) {
            jo.put("alt", l.getAltitude());
        }
        return jo;
    }

    public static JSONObject getGeoPointJSON(GeoPoint gp) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("lat", gp.getLatitude());
        jo.put("lon", gp.getLongitude());
        jo.put("alt", gp.getAltitude());
        return jo;
    }

    public static JSONObject getBoundingBoxJSON(BoundingBox bb) throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("north", bb.getLatNorth());
        jo.put("south", bb.getLatSouth());
        jo.put("west", bb.getLonWest());
        jo.put("east", bb.getLonEast());
        return jo;
    }

    /**
     * {
     *     "type": "multiple_trajectories",
     *     "trajectores": [
     *          ...
     *      ]
     * }
     * @param trajs
     * @return
     * @throws JSONException
     */
    // although the types do not enforce it, here we can assume we are
    // talking about multiple single-trajectories.
    public static JSONObject getMultTrajJSON(Collection<? extends Visualisation> trajs) throws JSONException {
        JSONObject jo = new JSONObject();
        JSONArray a = new JSONArray();
        for (Visualisation singleTraj : trajs) {
            a.put(
                    getSingleTrajJSON((TrajectoryVis) singleTraj)
            );
        }

        jo.put("type", "multiple_trajectories");
        jo.put("trajectories", a);
        return jo;
    }

    public static JSONObject getMultipleCloudsJSON(Collection<? extends Visualisation> clouds) throws JSONException {
        JSONObject jo = new JSONObject();
        JSONArray a = new JSONArray();
        for (Visualisation cloud : clouds) {
            a.put(
                    getSingleCloudJSON((CloudVis) cloud)
            );
        }
        jo.put("type", "clouds");
        jo.put("clouds", a);
        return jo;
    }

    public static JSONObject getSingleCloudJSON(CloudVis cloud) throws JSONException {
        JSONObject jo = new JSONObject();
        // for consistency with single trajectory
        jo.put("type", "single_cloud");
        jo.put("points", getLocationsJSONArr(cloud.points.locations));
        jo.put("hull", getLocationsJSONArr(cloud.hull.locations));
        return jo;
    }
}

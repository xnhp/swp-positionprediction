package project.software.uni.positionprediction.osm;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.util.List;

import project.software.uni.positionprediction.util.GeoDataUtils;
import project.software.uni.positionprediction.visualisation.IVisualisation;
import project.software.uni.positionprediction.visualisation.SingleTrajectoryVis;

/**
 * Takes care of calling the correct methods to draw the visualisation on the map.
 * Merely *draws* what is specified in the Visualisation obejct
 */
public class OSMDroidVisualisationAdapter implements IVisualisation {

    private OSMDroidMap map;

    public void linkMap (OSMDroidMap map) {
        this.map = map;
    }

    @Override
    public void visualiseSingleTraj(SingleTrajectoryVis vis) {
        //List<GeoPoint> points = GeoDataUtils.ListLocationToGeoPoint(vis.traj);
        //List<IGeoPoint> points2 = GeoDataUtils.ListGeoPointToIGeoPoint(points);
        map.drawTracks(vis.traj, vis.lineColor, vis.pointColor);
    }

}

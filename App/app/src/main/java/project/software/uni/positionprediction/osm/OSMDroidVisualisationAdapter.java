package project.software.uni.positionprediction.osm;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.util.List;

import project.software.uni.positionprediction.util.GeoDataUtils;
import project.software.uni.positionprediction.visualisation.IVisualisationAdapter;
import project.software.uni.positionprediction.visualisation.SingleTrajectoryVis;

/**
 * Takes care of calling the correct methods to draw the visualisation on the map.
 * Merely *draws* what is specified in the Visualisation obejct
 */
public class OSMDroidVisualisationAdapter implements IVisualisationAdapter {

    private OSMDroidMap map;

    public void linkMap (Object mapView) {
        if (mapView instanceof OSMDroidMap) {
            this.map = (OSMDroidMap) mapView;
        } else {
            InvalidMapViewException e = new InvalidMapViewException();
            e.printStackTrace();
        }
    }

    @Override
    public void visualiseSingleTraj(SingleTrajectoryVis vis) {
        List<GeoPoint> points = GeoDataUtils.ListLocationToGeoPoint(vis.locations);
        map.drawTracks(points, vis.lineColor, vis.pointColor);
    }

}

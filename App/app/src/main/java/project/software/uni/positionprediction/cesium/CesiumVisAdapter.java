package project.software.uni.positionprediction.cesium;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import project.software.uni.positionprediction.osm.MapInitException;
import project.software.uni.positionprediction.visualisation_new.IVisualisationAdapter;
import project.software.uni.positionprediction.visualisation_new.TrajectoryVis;

public class CesiumVisAdapter implements IVisualisationAdapter {
    @Override
    public void linkMap(Object mapView) throws MapInitException {

    }

    @Override
    public void visualiseSingleTraj(TrajectoryVis vis) {

    }

    @Override
    public void setCenter(GeoPoint centerWithDateLine) {

    }

    @Override
    public void setMapCenter(GeoPoint centerWithDateLine) {

    }

    @Override
    public void panToBoundingBox(BoundingBox boundingBox) {

    }
}

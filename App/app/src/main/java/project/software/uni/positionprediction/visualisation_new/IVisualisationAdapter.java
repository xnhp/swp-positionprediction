package project.software.uni.positionprediction.visualisation_new;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import project.software.uni.positionprediction.osm.MapInitException;

/**
 * Specifies the methods than need to be implemented in order to draw a visualisation.
 * This is implemented e.g. by OSMDroidVisualisationAdapter
 */
public interface IVisualisationAdapter {

    // there needs to be a reference to the map that should be drawn on.
    // as of now, the type is general because it is open what we would receive for cesium here.
    // TODO: more specific subtype possible?
    void linkMap(Object mapView) throws MapInitException;

    void visualiseSingleTraj(TrajectoryVis vis);

    void setCenter(GeoPoint centerWithDateLine);

    void panToBoundingBox(BoundingBox boundingBox);

    //void visualiseMultipleTraj(Geometry vis);
    //void visualiseCloud(Geometry vis);

}

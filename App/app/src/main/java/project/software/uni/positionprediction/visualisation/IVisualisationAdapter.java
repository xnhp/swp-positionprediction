package project.software.uni.positionprediction.visualisation;

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

    void visualiseSingleTraj(SingleTrajectoryVis vis);
    //void visualiseMultipleTraj(Visualisation vis);
    //void visualiseCloud(Visualisation vis);

}

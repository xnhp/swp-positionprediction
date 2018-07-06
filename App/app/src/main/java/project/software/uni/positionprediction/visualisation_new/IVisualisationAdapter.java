package project.software.uni.positionprediction.visualisation_new;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import project.software.uni.positionprediction.osm.MapInitException;

/**
 * Specifies the methods than need to be implemented in order to draw a visualisation.
 * This is implemented e.g. by OSMDroidVisualisationAdapter
 */
public abstract class IVisualisationAdapter {

    public Visualisations currentVisPred;
    public TrajectoryVis currentVisPast;

    /**
     * This is to enforce that the visualisation Adapter remembers
     * the current visualisation.
     * <p>
     * Upon resuming an activity, we need a way to tell whether
     * current prediction (vis_pred & vis_past in PredWfCtrl)
     * have changed so we can update the visualisation if we have to.
     * <p>
     * This is done in visualisationWorkflow.
     */
    public void setCurrentPastVis(TrajectoryVis vis_past) {
        this.currentVisPast = vis_past;
    }

    public void setCurrentPredVis(Visualisations vis_pred) {
        this.currentVisPred = vis_pred;
    }

    public boolean areVisCurrent(TrajectoryVis visPast1, Visualisations visPred1) {
        return (
                visPast1 == currentVisPast
                && visPred1 == currentVisPred
                );
    }

    // there needs to be a reference to the map that should be drawn on.
    // TODO: more specific subtype possible?
    public abstract void linkMap(Object mapView) throws MapInitException;

    public abstract void visualiseSingleTraj(TrajectoryVis vis);

    public abstract void setMapCenter(GeoPoint centerWithDateLine);

    public abstract void panToBoundingBox(BoundingBox boundingBox);

    //void visualiseMultipleTraj(Geometry vis);
    //void visualiseCloud(Geometry vis);

    /**
     * Clear any visualisations fom the map.
     */
    public abstract void clear();

}

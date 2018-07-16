package project.software.uni.positionprediction.visualisation;

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

    // minimal size of a bounding box in latitude/longitude difference
    // (in case it e.g. describes only a single point)
    // Note: this is risky since the actual distance from some
    // lat/lon to another is different depending on where on the globe
    // you are. However, I'll make the assumption that this is not
    // important here.
    public double boundingBoxMinSize = 0.1;

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

    // a visAdapter needs a reference to a map that it can draw on.
    // TODO: more specific subtype possible?
    public abstract void linkMap(Object mapView) throws MapInitException;

    public abstract void visualiseSingleTraj(TrajectoryVis vis);

    public abstract void visualiseSingleCloud(CloudVis vis);

    /*
        connect last point of past trajectory and
        and first point prediction trajectory by a straight line.

        this has to be done separately because this cannot be put
        in any of the other visualisation methods.
     */
    public abstract void drawTrajectoryConnection(Polyline pline);

    public abstract void setMapCenter(GeoPoint centerWithDateLine);


    /**
     * Clear any visualisations fom the map.
     */
    public abstract void clear();

    /**
     * zoom/pan to past and prediction visualisation
     * (done on activity start or refresh of activ)
     */
    public abstract void showVisualisation();

    /**
     * zoom/pan to visualisation of past data
     * (done on button press)
     */
    public abstract void showData();

    /**
     * zoom/pan to visualisation of prediction
     * (done on button press)
     */
    public abstract void showPrediction();

    /**
     * In case the prediction bounding box is overly small,
     * the map libraries do weird things (osmdroid doesnt move at all,
     * cesium glitches).
     *
     * Hence enforce a minimum size for the bounding box.
     *
     * this could also be put in GeoDataUtils
     * but then the question arises where to get the boundingBoxMinSize from
     * that should be stored here, so you'd have to pass the visAdap around
     * as parameter.
     * @param boundingBox
     * @return
     */
    public BoundingBox getSafeBoundingBox(final BoundingBox boundingBox) {
        double diff = Math.min(
                boundingBox.getLatNorth() - boundingBox.getLatSouth(),
                boundingBox.getLonEast() - boundingBox.getLonWest()
        );

        // in this case, we simply enlarge the bounding box by a bit.
        if (diff < 0.0001) {
            boundingBox.set(
                    boundingBox.getLatNorth() + this.boundingBoxMinSize/2,
                    boundingBox.getLonWest() + this.boundingBoxMinSize/2,
                    boundingBox.getLatSouth() - this.boundingBoxMinSize/2,
                    boundingBox.getLonEast() - this.boundingBoxMinSize/2
            );
        }
        return boundingBox;
    }
}

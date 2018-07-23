package project.software.uni.positionprediction.controllers;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import project.software.uni.positionprediction.datatypes.Collection;
import project.software.uni.positionprediction.datatypes.Location;
import project.software.uni.positionprediction.datatypes.Locations;
import project.software.uni.positionprediction.visualisation.CloudVis;
import project.software.uni.positionprediction.visualisation.IVisualisationAdapter;
import project.software.uni.positionprediction.visualisation.Polyline;
import project.software.uni.positionprediction.visualisation.PredTrajectoryStyle;
import project.software.uni.positionprediction.visualisation.TrajectoryVis;
import project.software.uni.positionprediction.visualisation.Visualisation;
import project.software.uni.positionprediction.visualisation.Visualisations;

/** This workflow calls the adapter's visualization methods which, in turn, draw the actual vis. */
public class VisualizationWorkflow extends Controller {

    private IVisualisationAdapter visAdapter = null;
    private TrajectoryVis past;
    private Visualisations pred;

    public VisualizationWorkflow(
            Context context,
            IVisualisationAdapter visAdapter,
            TrajectoryVis past,
            Visualisations pred
            ) {
        super(context);
        this.visAdapter = visAdapter;
        this.past = past;
        this.pred = pred;
    }

    public void trigger() {
        // No prediction, no visualization
        if (past == null) {
            visAdapter.setCurrentPastVis(null);
            visAdapter.setCurrentPredVis(null);
            return;
        }

        Log.i("osm adapter", "past BoundingBox: " + past.getBoundingBox().toString());
        Log.i("osm adapter", "past BoundingBox center: " + past.getBoundingBox().getCenterWithDateLine());
        Log.i("vis workflow", "traj line is null: " + (past.getLine() == null));


        // assemble past and prediction visualisation
        /*
            (a)
            In the case where the prediction result is a single or multiple
            trajectories, we additionally want a line from the last data point
            of the prediction to the first point of the resp. trajectory

            (b)
            In the case where the prediction result is a funnel,
            there is no extra work (funnel will be positioned with the pointy
            end at the end of the past trajectory)

            (c)
            in the case where the prediction result is a cloud,
            there is no extra work - the cloud is just drawn at its
            position
         */

        /*simply visualise the past tracking points*/
        visAdapter.visualiseSingleTraj(this.past);
        visAdapter.setCurrentPastVis(this.past);

        /*
        visualise prediction, in different ways based on its
        type
        */
        // Catch if no prediction could be made
        if (pred == null || pred.size() == 0) {
            return;
        }

        for (Collection<? extends Visualisation> type : pred.values()) {
            for (Visualisation vis : type) {
                if (vis instanceof TrajectoryVis) {
                    /* additionally connect the first point of the pred
                       to the last point of the past vis
                     */
                    visAdapter.drawTrajectoryConnection(getConnectingLine((TrajectoryVis) vis));

                    /*
                    draw prediction visualisation
                     */
                    visAdapter.visualiseSingleTraj((TrajectoryVis) vis);

                } else if (vis instanceof CloudVis) {
                    visAdapter.visualiseSingleCloud((CloudVis) vis);
                }

            }
        }
        visAdapter.setCurrentPredVis(this.pred);

        new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                visAdapter.showVisualisation();
            }
        });
    }


    private Polyline getConnectingLine(TrajectoryVis vis) {
        // first point of pred
        Location end = vis.getLine().locations.get(0);
        // last point of past
        Location start = this.past.getLine().locations.getLast();
        Locations locs = new Locations();
        locs.add(end);
        locs.add(start);
        return new Polyline(locs, PredTrajectoryStyle.pointCol, PredTrajectoryStyle.lineCol, PredTrajectoryStyle.pointRadius);
    }

}

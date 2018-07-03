package project.software.uni.positionprediction.controllers;

import android.content.Context;
import android.util.Log;

import project.software.uni.positionprediction.datatypes_new.Cloud;
import project.software.uni.positionprediction.datatypes_new.Collection;
import project.software.uni.positionprediction.osm.OSMDroidVisualisationAdapter_new;
import project.software.uni.positionprediction.visualisation_new.IVisualisationAdapter;
import project.software.uni.positionprediction.visualisation_new.TrajectoryVis;
import project.software.uni.positionprediction.visualisation_new.Visualisation;
import project.software.uni.positionprediction.visualisation_new.Visualisations;

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
            return;
        }

        Log.i("osm adapter", "past BoundingBox: " + past.getBoundingBox().toString());
        Log.i("osm adapter", "past BoundingBox center: " + past.getBoundingBox().getCenterWithDateLine());
        Log.i("vis workflow", "traj line is null: " + (past.getLine() == null));


        // todo: TJ 180623: There is a multithread error
        // visAdapter.panToBoundingBox(past.getBoundingBox());
        //visAdapter.setMapCenter(past.getBoundingBox().getCenterWithDateLine());


        // assemble past and prediction visualisation
        /*
            (a)
            In the case where the prediction result is a single or multiple
            trajectories, we additionally want a line from the last data point
            of the prediction to the first point of the resp. trajectory - todo

            (b)
            In the case where the prediction result is a funnel,
            there is no extra work (funnel will be positioned with the pointy
            end at the end of the past trajectory) - todo

            (c)
            in the case where the prediction result is a cloud,
            there is no extra work - the cloud is just drawn at its
            position - todo
         */

        // simply visualise the past tracking points
        visAdapter.visualiseSingleTraj(past);

        // visualise prediction, in different ways based on its
        // type
        for (Collection<? extends Visualisation> type : pred.values()) {
            //int counter = 1;
            for (Visualisation vis : type) {
                if (vis instanceof TrajectoryVis) {
                    // case (a)
                    visAdapter.visualiseSingleTraj((TrajectoryVis) vis);
                //} else if (locs instanceof Cloud) {
                    // visualizeCloud(locs, i);
                //}
                //counter++;
                }
            }

        }
    }
}

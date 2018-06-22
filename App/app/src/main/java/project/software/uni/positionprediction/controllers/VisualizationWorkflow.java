package project.software.uni.positionprediction.controllers;

import android.content.Context;

import project.software.uni.positionprediction.datatypes_new.Collection;
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

        /*
        Polyline past = PredictionWorkflow.vis_past;
        Visualisations pred = PredictionWorkflow.vis_pred;
        */

        visAdapter.visualiseSingleTraj(past);


        for (Collection<? extends Visualisation> type : pred.values()) {
            //int counter = 1;
            for (Visualisation vis : type) {
                if (vis instanceof TrajectoryVis) {
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

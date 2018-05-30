package project.software.uni.positionprediction.visualisation;



import org.osmdroid.api.IGeoPoint;

import java.util.ArrayList;

public class SingleTrajectoryVis extends Visualisation {
    /* a single trajectory merely consists of a series of points */
    public ArrayList<IGeoPoint> traj;

    public String pointColor;
    public String lineColor;

    public SingleTrajectoryVis() {
        // TODO: is this necessary?
        this.kind = VisualisationKinds.SINGLE_TRAJ;
    }
}

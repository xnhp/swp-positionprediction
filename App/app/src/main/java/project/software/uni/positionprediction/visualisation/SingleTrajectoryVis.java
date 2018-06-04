package project.software.uni.positionprediction.visualisation;



import org.osmdroid.api.IGeoPoint;

import java.util.ArrayList;
import java.util.List;

import project.software.uni.positionprediction.datatype.Location3D;

public class SingleTrajectoryVis extends Visualisation {
    /* a single trajectory merely consists of a series of points */
    public List<Location3D> traj;

    public String pointColor;
    public String lineColor;

    public SingleTrajectoryVis() {
        // TODO: is this necessary?
        // instanceof?
        // this.kind = VisualisationKinds.SINGLE_TRAJ;
    }
}

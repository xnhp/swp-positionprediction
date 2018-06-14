package project.software.uni.positionprediction.datatype;

import java.util.ArrayList;

/**
 * Represents a single trajectory (ordered sequence of points)
 */
public class SingleTrajectory extends Locations {

    public SingleTrajectory() {
        super();
    }

    public SingleTrajectory(ArrayList<Location> l) {
        super(l);
    }

    public SingleTrajectory(Locations l) {
        super(l);
    }
}

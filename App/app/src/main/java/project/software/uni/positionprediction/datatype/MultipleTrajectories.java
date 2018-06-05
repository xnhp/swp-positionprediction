package project.software.uni.positionprediction.datatype;

import java.util.ArrayList;

public class MultipleTrajectories {

    // mind the locationS
    // this is an ArrayList and not a set because trajectories could be ordered
    // (e.g.) by importance
    public ArrayList<SingleTrajectory> trajectories;

    public MultipleTrajectories() {

    }

    public MultipleTrajectories(ArrayList<SingleTrajectory> trajectories) {
        this.trajectories = trajectories;
    }

    public MultipleTrajectories add(SingleTrajectory traj) {
        this.trajectories.add(traj);
        return this;
    }

    public Locations get(int i) {
        return this.trajectories.get(i);
    }

    public MultipleTrajectories addAll(ArrayList<SingleTrajectory> trajs) {
        this.trajectories.addAll(trajs);
        return this;
    }
}

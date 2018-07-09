package project.software.uni.positionprediction.datatypes;

public class PredictionBaseData {

    private Trajectory trajectory = new Trajectory();

    public PredictionBaseData() {}

    public PredictionBaseData(Trajectory trajectory){
        this.trajectory = trajectory;
    }

    public Trajectory getTrajectory() {
        return trajectory;
    }

    public void setTrackedLocations(Trajectory trajectory) {
        this.trajectory = trajectory;
    }

}

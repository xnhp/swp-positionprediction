package project.software.uni.positionprediction.datatypes_new;

public class PredictionBaseData {

    private Trajectory trackedLocations = new Trajectory();

    public PredictionBaseData() {}

    public PredictionBaseData(Trajectory trackedLocations){
        this.trackedLocations = trackedLocations;
    }

    public Trajectory getTrackedLocations() {
        return trackedLocations;
    }

    public void setTrackedLocations(Trajectory trackedLocations) {
        this.trackedLocations = trackedLocations;
    }

}

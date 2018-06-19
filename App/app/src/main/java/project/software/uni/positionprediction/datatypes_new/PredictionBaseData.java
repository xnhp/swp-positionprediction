package project.software.uni.positionprediction.datatypes_new;

import java.util.ArrayList;
import java.util.Date;

import project.software.uni.positionprediction.datatype.TrackingPoint;

public class PredictionBaseData {
    private Trajectory trackedLocations = new Trajectory();

    public Trajectory getTrackedLocations() {
        return trackedLocations;
    }

    public PredictionBaseData() {
        /* syntax demo :
        TrackedLocation loc = new TrackedLocation(new Location(2.0, 3.0), new Date());
        trackedLocations.add(loc);
        */
    }

    public PredictionBaseData(Trajectory pastTracks){
        this.trackedLocations = pastTracks;
    }


}

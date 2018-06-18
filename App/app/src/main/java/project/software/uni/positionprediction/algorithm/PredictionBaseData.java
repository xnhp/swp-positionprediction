package project.software.uni.positionprediction.algorithm;

import java.util.ArrayList;
import java.util.List;

import project.software.uni.positionprediction.datatype.Location;
import project.software.uni.positionprediction.datatype.Location3D;
import project.software.uni.positionprediction.datatype.Locations;
import project.software.uni.positionprediction.datatype.TrackingPoints;

/**
 * Serves as a container for data that a prediction algorithm uses to make a prediction
 * i.e. past tracking points, time of day, weather, etc
 */
public class PredictionBaseData {
    public TrackingPoints pastTracks;
    // .. etc ...


    public TrackingPoints getPastTracks() {
        return pastTracks;
    }

    public void setPastTracks(TrackingPoints pastTracks) {
        this.pastTracks = pastTracks;
    }



}

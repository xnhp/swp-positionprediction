package project.software.uni.positionprediction.algorithm;

import java.util.List;

import project.software.uni.positionprediction.datatype.Location3D;

/**
 * Serves as a container for data that a prediction algorithm uses to make a prediction
 * i.e. past tracking points, time of day, weather, etc
 */
public class PredictionBaseData {
    public Location3D[] pastTracks;
    // .. etc ...
}

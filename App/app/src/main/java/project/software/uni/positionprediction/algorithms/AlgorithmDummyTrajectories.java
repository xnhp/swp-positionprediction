package project.software.uni.positionprediction.algorithms;

import android.content.Context;

import project.software.uni.positionprediction.datatypes.Location;
import project.software.uni.positionprediction.datatypes.LocationWithValue;
import project.software.uni.positionprediction.datatypes.Locations;
import project.software.uni.positionprediction.datatypes.PredictionBaseData;
import project.software.uni.positionprediction.datatypes.PredictionResultData;
import project.software.uni.positionprediction.datatypes.PredictionUserParameters;
import project.software.uni.positionprediction.datatypes.Trajectory;

public class AlgorithmDummyTrajectories extends PredictionAlgorithmReturnsTrajectories {

    // Constructor
    public AlgorithmDummyTrajectories(Context c) {
        super(c);
    }

    public PredictionResultData predict(PredictionUserParameters algParams, PredictionBaseData data){

        Locations locs1 = new Locations();
        LocationWithValue<Double> loc11 = new LocationWithValue(
            new Location(-89.73, -1.37),
            0.002
        );
        locs1.add(loc11);
        LocationWithValue<Double> loc12 = new LocationWithValue(
                new Location(-89.71, -1.36),
                0.004
        );
        locs1.add(loc12);
        LocationWithValue<Double> loc13 = new LocationWithValue(
                new Location(-89.68, -1.37),
                0.006
        );
        locs1.add(loc13);
        LocationWithValue<Double> loc14 = new LocationWithValue(
                new Location(-89.66, -1.39),
                0.008
        );
        locs1.add(loc14);
        LocationWithValue<Double> loc15 = new LocationWithValue(
                new Location(-89.64, -1.40),
                0.010
        );
        locs1.add(loc15);

        Trajectory traj1 = new Trajectory(locs1);

        return new PredictionResultData(traj1);
    }
}

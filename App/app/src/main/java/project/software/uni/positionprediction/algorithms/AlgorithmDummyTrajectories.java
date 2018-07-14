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
            new Location(47.691057, 9.186505),
            0.001
        );
        locs1.add(loc11);
        LocationWithValue<Double> loc12 = new LocationWithValue(
                new Location(47.693811, 9.189909),
                0.002
        );
        locs1.add(loc12);
        LocationWithValue<Double> loc13 = new LocationWithValue(
                new Location(47.698893, 9.189131),
                0.003
        );
        locs1.add(loc13);
        LocationWithValue<Double> loc14 = new LocationWithValue(
                new Location(47.701975, 9.190788),
                0.004
        );
        locs1.add(loc14);
        LocationWithValue<Double> loc15 = new LocationWithValue(
                new Location(47.704131, 9.189782),
                0.005
        );
        locs1.add(loc15);

        Trajectory traj1 = new Trajectory(locs1);

        return new PredictionResultData(traj1);
    }
}

package project.software.uni.positionprediction.algorithms;

import android.content.Context;

import project.software.uni.positionprediction.datatypes.Cloud;
import project.software.uni.positionprediction.datatypes.Location;
import project.software.uni.positionprediction.datatypes.LocationWithValue;
import project.software.uni.positionprediction.datatypes.Locations;
import project.software.uni.positionprediction.datatypes.PredictionBaseData;
import project.software.uni.positionprediction.datatypes.PredictionResultData;
import project.software.uni.positionprediction.datatypes.PredictionUserParameters;

public class AlgorithmDummyCloud extends PredictionAlgorithmReturnsCloud {
    public AlgorithmDummyCloud(Context c) {
        super(c);
    }

    @Override
    public PredictionResultData predict(PredictionUserParameters algParams, PredictionBaseData data) {
        Locations hull1 = new Locations();
        Location loc11 = new Location(
                new Location(-89.734471,-1.364126)
        );
        hull1.add(loc11);
        Location loc12 = new Location(
                new Location(-89.669240, -1.349024)
        );
        hull1.add(loc12);
        Location loc13 = new Location(
                new Location(-89.625981, -1.383347)
        );
        hull1.add(loc13);
        Location loc14 = new LocationWithValue(
                new Location(-89.635594, -1.409775),
                0.004
        );
        hull1.add(loc14);
        Location loc15 = new Location(
                new Location(-89.739621, -1.371034)
        );
        hull1.add(loc15);

        Locations locs1 = new Locations(hull1);

        // Add some more inner points
        Location loc16 = new Location(
                new Location(-89.714387, -1.365371)
        );
        locs1.add(loc16);

        Location loc17 = new Location(
                new Location(-89.683831, -1.363655)
        );
        locs1.add(loc17);

        Location loc18 = new Location(
                new Location(-89.665463, -1.368803)
        );
        locs1.add(loc18);

        Location loc19 = new Location(
                new Location(-89.646237, -1.381502)
        );
        locs1.add(loc19);

        Location loc20 = new Location(
                new Location(-89.639542, -1.395574)
        );
        locs1.add(loc20);

        Location loc21 = new Location(
                new Location(-89.657051, -1.384248)
        );
        locs1.add(loc21);

        Location loc22 = new Location(
                new Location(-89.674217, -1.385449)
        );
        locs1.add(loc22);

        Location loc23 = new Location(
                new Location(-89.694477, -1.373903)
        );
        locs1.add(loc23);

        Location loc24 = new Location(
                new Location(-89.719196, -1.373217)
        );
        locs1.add(loc24);


        Cloud cloud1 = new Cloud(locs1, hull1);

        return new PredictionResultData(cloud1);
    }
}
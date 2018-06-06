package project.software.uni.positionprediction.interfaces;

import java.util.Date;

import project.software.uni.positionprediction.algorithm.PredictionBaseData;
import project.software.uni.positionprediction.algorithm.PredictionUserParameters;
import project.software.uni.positionprediction.datatype.Cloud;
import project.software.uni.positionprediction.datatype.TrackingPoint;

public interface PredictionAlgorithmReturnsCloud {

    /**
     * Description: Interface for an algorithm, which returns a set of locations. Therefore it is
     * not necessary to pay attention to the order of the locations.
     *
     * Distinction to the other interfaces:
     *      - Trajectory:    One List of Locations. The order is necessary!
     *      - Trajectories:  Multiple Lists of Locatsions. The order of the elements are important!
     *                       The order of the trajectories is not important.
     *      - Cloud:         List of Locations. The order is not necessary
     *
     * @return
     */
    Cloud predict(PredictionUserParameters algParams, PredictionBaseData data);

}

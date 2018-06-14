package project.software.uni.positionprediction.interfaces;

import java.util.Date;

import project.software.uni.positionprediction.algorithm.PredictionBaseData;
import project.software.uni.positionprediction.algorithm.PredictionUserParameters;
import project.software.uni.positionprediction.datatype.Location;
import project.software.uni.positionprediction.datatype.Locations;
import project.software.uni.positionprediction.datatype.TrackingPoint;

public interface PredictionAlgorithmReturnsTrajectory extends PredictionAlgorithm {

    /**
     * Description: Interface for an algorithm, which returns one single trajectory.
     * This trajectory can be one single point and therefore can also be used to predict one
     * single point.
     *
     * Distinction to the other interfaces:
     *      - Trajectory:    One List of Locations. The order is necessary!
     *      - Trajectories:  Multiple Lists of Locatsions. The order of the elements are important!
     *                       The order of the trajectories is not important.
     *      - Cloud:         List of Locations. The order is not necessary
     *
     */
    Locations predict(PredictionUserParameters algParams, PredictionBaseData data);


}





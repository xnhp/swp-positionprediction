package project.software.uni.positionprediction.interfaces;

import java.util.LinkedList;

import project.software.uni.positionprediction.algorithm.PredictionBaseData;
import project.software.uni.positionprediction.algorithm.PredictionUserParameters;
import project.software.uni.positionprediction.datatype.Location3D;

/**
 * General Interface for using a prediction algorithm
 */
public interface SingleTrajPredictionAlgorithm {

    /**
     * Predict Algorithm uses:
     * <p>
     * Input can be specified by implementation of interface (Database scheme dependent)
     * <p>
     * Returns only one predicted point
     *
     * @return
     */
    // Todo: Output
    LinkedList<Location3D> predict(PredictionUserParameters params, PredictionBaseData data);


}





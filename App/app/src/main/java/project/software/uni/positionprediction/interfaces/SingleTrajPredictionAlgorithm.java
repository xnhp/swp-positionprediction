package project.software.uni.positionprediction.interfaces;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import project.software.uni.positionprediction.algorithm.PredictionBaseData;
import project.software.uni.positionprediction.algorithm.PredictionUserParameters;
import project.software.uni.positionprediction.datatype.Location;
import project.software.uni.positionprediction.datatype.Location3D;
import project.software.uni.positionprediction.datatype.Locations;

/**
 * General Interface for using a prediction algorithm
 */
public interface SingleTrajPredictionAlgorithm {

    /**
     * Predict Algorithm uses:
     * - bird_id and can get data by searching for date in the database
     * - date_past to use only data in a given time frame
     * - date_pred to predict the position in a given time frame in the future
     * <p>
     * Input can be specified by implementation of interface (Database scheme dependent)
     * <p>
     * Returns only one predicted point
     *
     */
    // Todo: Output

    Locations predict(PredictionUserParameters algParams, PredictionBaseData data);
}





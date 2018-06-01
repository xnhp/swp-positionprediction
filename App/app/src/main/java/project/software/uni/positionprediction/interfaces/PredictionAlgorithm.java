package project.software.uni.positionprediction.interfaces;

import java.util.Date;

import project.software.uni.positionprediction.datatype.Location3D;

/**
 * General Interface for using a prediction algorithm
 */
public interface PredictionAlgorithm {

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
     * @param date_past
     * @param date_pred
     * @param bird_id
     * @return
     */
    // Todo: Output
    Location3D predict_interpolation(Date date_past, Date date_pred, int bird_id);


}





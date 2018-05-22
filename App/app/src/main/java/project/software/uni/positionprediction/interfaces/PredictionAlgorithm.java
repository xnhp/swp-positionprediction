package project.software.uni.positionprediction.interfaces;

import android.location.Location;
import java.util.Date;
import java.util.LinkedList;

import project.software.uni.positionprediction.classes.LocProbTupel;

/**
 * General Interface for using a prediction algorithm
 *
 */
public interface PredictionAlgorithm {

    /**
     * @param date_past
     * @param date_pred
     * @param bird_id
     * @return Tupels with Locations and Probalities
     */
    LinkedList<LocProbTupel> predict(Date date_past, Date date_pred, int bird_id);

}

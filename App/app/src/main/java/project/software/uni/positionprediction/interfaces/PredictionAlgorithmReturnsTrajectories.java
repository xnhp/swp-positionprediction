package project.software.uni.positionprediction.interfaces;

import java.util.Date;
import project.software.uni.positionprediction.datatype.Location;
import project.software.uni.positionprediction.datatype.TrackingPoint;

public interface PredictionAlgorithmReturnsTrajectories {


    /**
     * Description: Interface for an algorithm, which returns multiple (one or more) trajectories.
     *
     * Distinction to the other interfaces:
     *      - Trajectory:    One List of Locations. The order is necessary!
     *      - Trajectories:  Multiple Lists of Locatsions. The order of the elements are important!
     *                       The order of the trajectories is not important.
     *      - Cloud:         List of Locations. The order is not necessary
     *
     * @param data
     * @param date_past
     * @param date_pred
     * @param study_id
     * @param bird_id
     * @return
     */
    Location predict(TrackingPoint[] data, Date date_past, Date date_pred, int study_id, int bird_id);

}

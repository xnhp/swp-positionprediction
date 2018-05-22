package project.software.uni.positionprediction.classes;

import java.util.Date;

import project.software.uni.positionprediction.interfaces.PredictionAlgorithm;

public class PredictionImplementation implements PredictionAlgorithm {

    /**
     * Hard-coded implementation of an prediction algorithm to use it for visualization
     */
    @Override
    public LocProbTupel[][] predict(Date date_past, Date date_pred, int bird_id) {



        return new LocProbTupel[0][];
    }
}

package project.software.uni.positionprediction.algorithm;

import java.util.Date;

import project.software.uni.positionprediction.datatype.Bird;

/**
 * *User-Given* parameters to an algorithm
 * Potentially, there could be more parameters as an input to an algorithm,
 * such as time of day, weather etc.
 * This class is there to encapsulate these values (potentially null if n/a)
 * to not have all this in the argument list of a method
 */
public class PredictionParameters {


    public PredictionParameters(Date date_past, Date date_pred, Bird bird) {
        this.date_past = date_past;
        this.date_pred = date_pred;
        this.bird = bird;
    }

    public PredictionParameters(Date date_past, Date date_pred, Bird bird, Date time_of_day) {
        this.date_past = date_past;
        this.date_pred = date_pred;
        this.bird = bird;
        this.time_of_day = time_of_day;
    }

    public Date date_past; // cf SingleTrajPredictionAlgorithm
    public Date date_pred; // cf SingleTrajPredictionAlgorithm
    public Bird bird;
    public Date time_of_day;
    // ...

}

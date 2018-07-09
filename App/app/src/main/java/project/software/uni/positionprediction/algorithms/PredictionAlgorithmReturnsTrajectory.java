package project.software.uni.positionprediction.algorithms;

import android.content.Context;

import project.software.uni.positionprediction.datatypes.PredictionResultData;
import project.software.uni.positionprediction.datatypes.Trajectory;

public abstract class PredictionAlgorithmReturnsTrajectory extends PredictionAlgorithm<Trajectory> {

    public PredictionAlgorithmReturnsTrajectory(Context c) {
        super(c);
    }

    /**
     * TJ: Subtypes of this class use this method to create proper result data.
     * In this case, the methods simply calls the appropriate constructor. However, more complex
     * algorithms could return e.g. a combination of trajectories and clouds or novel shapes.
     * Then, the method would coordinate the creation of a proper PredictionResultData object.
     */
    @Override
    protected PredictionResultData createResultData(Trajectory trajectory){
        return new PredictionResultData(trajectory);
    }

}





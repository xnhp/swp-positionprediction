package project.software.uni.positionprediction.algorithms;


import android.content.Context;

import project.software.uni.positionprediction.datatypes.Cloud;
import project.software.uni.positionprediction.datatypes.PredictionResultData;

public abstract class PredictionAlgorithmReturnsCloud extends PredictionAlgorithm<Cloud> {

    public PredictionAlgorithmReturnsCloud(Context c) {
        super(c);
    }



    /**
     * TJ: Subtypes of this class use this method to create proper result data.
     * In this case, the methods simply calls the appropriate constructor. However, more complex
     * algorithms could return e.g. a combination of trajectories and clouds or novel shapes.
     * Then, the method would coordinate the creation of a proper PredictionResultData object.
     */
    protected PredictionResultData createResultData(Cloud cloud) {
        return new PredictionResultData(cloud);
    }
}

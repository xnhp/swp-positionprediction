package project.software.uni.positionprediction.algorithms_new;


import project.software.uni.positionprediction.datatypes_new.Cloud;
import project.software.uni.positionprediction.datatypes_new.PredictionResultData;

public abstract class PredictionAlgorithmReturnsCloud extends PredictionAlgorithm<Cloud> {
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

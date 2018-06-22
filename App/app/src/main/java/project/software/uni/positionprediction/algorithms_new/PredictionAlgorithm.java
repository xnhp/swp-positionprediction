package project.software.uni.positionprediction.algorithms_new;

import project.software.uni.positionprediction.datatypes_new.PredictionUserParameters;
import project.software.uni.positionprediction.datatypes_new.PredictionBaseData;
import project.software.uni.positionprediction.datatypes_new.PredictionResultData;

/**
 * Abstract class for an algorithm, which returns a collection of locations.
 */
public abstract class PredictionAlgorithm<T> {

    public abstract PredictionResultData predict(PredictionUserParameters algParams, PredictionBaseData data);
    protected abstract PredictionResultData createResultData(T input);
}

package project.software.uni.positionprediction.interfaces;

import project.software.uni.positionprediction.algorithm.PredictionBaseData;
import project.software.uni.positionprediction.algorithm.PredictionUserParameters;
import project.software.uni.positionprediction.datatype.Locations;

public interface PredictionAlgorithm {

    // TODO: Replace "Object" by generic PredictionOutput class
    Object predict(PredictionUserParameters algParams, PredictionBaseData data);
}

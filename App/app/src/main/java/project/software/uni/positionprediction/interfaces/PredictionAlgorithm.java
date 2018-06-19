package project.software.uni.positionprediction.interfaces;

import project.software.uni.positionprediction.datatypes_new.PredictionBaseData;
import project.software.uni.positionprediction.algorithm.PredictionUserParameters;
import project.software.uni.positionprediction.datatype.Locations;
import project.software.uni.positionprediction.datatypes_new.PredictionResultData;

public interface PredictionAlgorithm {

    // TODO: Replace "Object" by generic PredictionOutput class
    PredictionResultData predict(PredictionUserParameters algParams, PredictionBaseData data);
}

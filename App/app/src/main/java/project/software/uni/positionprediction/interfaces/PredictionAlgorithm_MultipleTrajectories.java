package project.software.uni.positionprediction.interfaces;

import project.software.uni.positionprediction.algorithm.PredictionBaseData;
import project.software.uni.positionprediction.algorithm.PredictionUserParameters;
import project.software.uni.positionprediction.datatype.MultipleTrajectories;

public interface PredictionAlgorithm_MultipleTrajectories {

    MultipleTrajectories predict(PredictionUserParameters algParams, PredictionBaseData data);

}

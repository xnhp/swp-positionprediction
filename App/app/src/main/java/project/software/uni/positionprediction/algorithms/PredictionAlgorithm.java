package project.software.uni.positionprediction.algorithms;

import android.content.Context;

import project.software.uni.positionprediction.datatypes.PredictionUserParameters;
import project.software.uni.positionprediction.datatypes.PredictionBaseData;
import project.software.uni.positionprediction.datatypes.PredictionResultData;

/**
 * Abstract class for an algorithm, which returns a collection of locations.
 */
public abstract class PredictionAlgorithm<T> {

    private Context context;

    public PredictionAlgorithm(Context c){
        this.context = c;
    }

    public abstract PredictionResultData predict(PredictionUserParameters algParams, PredictionBaseData data);
    protected abstract PredictionResultData createResultData(T input);

    public void setContext(Context c){
        context = c;
    }

    public Context getContext() {
        return context;
    }
}

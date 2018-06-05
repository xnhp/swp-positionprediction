package project.software.uni.positionprediction.algorithm;

import java.util.ArrayList;
import java.util.LinkedList;

import project.software.uni.positionprediction.datatype.Location3D;
import project.software.uni.positionprediction.interfaces.SingleTrajPredictionAlgorithm;

public class AlgorithmExtrapolationExtended implements SingleTrajPredictionAlgorithm {

    private final int weight_max = 100;

    // Algorithm objects are singletons because you cannot declare static methods in an interface.
    // TODO: maybe abstract classes would be better after all?
    private static AlgorithmExtrapolationExtended instance;
    public static SingleTrajPredictionAlgorithm getInstance() {
        if (instance == null) instance = new AlgorithmExtrapolationExtended();
        return instance;
    }


    /**
     * Main idea:
     * Compute the difference between two successive data points and take the average of them.
     * By adding the average to the last data point we get a very simple prediction algorithm
     *
     * TODO javadoc
     */
    @Override
    public ArrayList<Location3D> predict(PredictionUserParameters params, PredictionBaseData data) {
        // TODO determine pastDataPoints from params.past_date
        int pastDataPoints = 10;
        // Compute prediction
        ArrayList<Location3D> prediction = next_Location(data.pastTracks, pastDataPoints);
        return prediction;
    }


    /**
     * Computes the average differences in long. and lat. and adds it to the last position
     * [Average (last 2 Locations, Average last 3 Locations,...)]
     *
     * @param data
     * @return
     */
    public ArrayList<Location3D> next_Location(Location3D data[], int date) {
        int n = data.length - 1;
        Location3D vector_collection[] = new Location3D[date - 1];
        // Fill collection
        for (int t = 1; t < date; t++) {
            // Compute difference of pair n and n-t
            // Get n-th point
            Location3D vec_n = data[n];

            // Get n-t point
            Location3D vec_old = data[n-t];

            // Compute vector between them
            Location3D vec_delta = vec_n.subtract(vec_old);

            // Compute average
            Location3D vec_avg = vec_delta.divide(t);

            // Add vector to collection
            vector_collection[t - 1] = vec_avg;
        }

        // Compute average of all computed vectors in collection
        Location3D avg = weighted_average(vector_collection);
        Location3D curr_loc = data[data.length - 1];

        // Add avg vector to current Location3D
        ArrayList<Location3D> result_list = new ArrayList<>();
        Location3D result_vector = curr_loc.add(avg);
        result_list.add(result_vector);
        return result_list;
    }


    /**
     * Computes (weighted) average of Location3D vectors
     *
     * @param collection
     * @return
     */
    public Location3D weighted_average(Location3D collection[]) {
        double sum_long = 0;
        double sum_lat = 0;
        double sum_height = 0;

        // Compute sum
        for (int i = 0; i < collection.length; i++) {
            sum_long += weight(i) * collection[i].getLoc_long();
            sum_lat += weight(i) * collection[i].getLoc_lat();
            sum_height += weight(i) * collection[i].getLoc_height();
        }

        // Compute average
        double length = (double) collection.length;
        double res_long = sum_long / length;
        double res_lat = sum_lat / length;
        double res_height = sum_height / length;

        return new Location3D(res_long, res_lat, res_height);
    }


    /**
     * Todo: possible weight function
     *
     * @param a
     * @return
     */
    private int weight(int a) {
        return 1;
    }

}

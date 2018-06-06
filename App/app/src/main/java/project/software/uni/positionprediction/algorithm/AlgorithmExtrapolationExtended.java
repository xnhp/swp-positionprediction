package project.software.uni.positionprediction.algorithm;

import android.content.Context;

import java.util.ArrayList;

import project.software.uni.positionprediction.datatype.Location;
import project.software.uni.positionprediction.datatype.Locations;
import project.software.uni.positionprediction.datatype.SingleTrajectory;
import project.software.uni.positionprediction.interfaces.SingleTrajPredictionAlgorithm;

public class AlgorithmExtrapolationExtended implements SingleTrajPredictionAlgorithm {

    private final int weight_max = 100;
    private Context context;

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

     * @return
     */
    @Override
    public Locations predict(PredictionUserParameters params, PredictionBaseData data) {
        // TODO determine pastDataPoints from params.past_date
        int pastDataPoints = 10;
        // Compute prediction
        Locations prediction = next_Location(data.pastTracks, pastDataPoints);
        return prediction;
    }


    /**
     * Computes the average differences in long. and lat. and adds it to the last position
     * [Average (last 2 Locations, Average last 3 Locations,...)]
     *
     * @param data
     * @return
     */
    public Locations next_Location(Locations data, int date) {
        int n = data.getLength() - 1;
        ArrayList<Location> vector_collection = new ArrayList<>();
        // Fill collection
        for (int t = 1; t < date; t++) { // todo: loop conditions correct?
            // Compute difference of pair n and n-t
            // Get n-th point
            Location vec_n = data.get(n);

            // Get n-t point
            Location vec_old = data.get(n-t);

            // Compute vector between them
            Location vec_delta = vec_n.subtract(vec_old);

            // Compute average
            Location vec_avg = vec_delta.divide(t);

            // Add vector to collection
            //vector_collection.set(t - 1, vec_avg);
            vector_collection.add(vec_avg);
        }

        // Compute average of all computed vectors in collection
        Location avg = weighted_average(vector_collection);
        Location curr_loc = data.get(data.getLength() - 1);

        // Add avg vector to current Location
        Locations result_list = new SingleTrajectory();
        Location result_vector = curr_loc.to3D().add(avg);
        result_list.add(result_vector);
        return result_list;
    }


    /**
     * Computes (weighted) average of Location3D vectors
     *
     * @param collection
     * @return
     */
    public Location weighted_average(ArrayList<Location> collection) {
        double sum_long = 0;
        double sum_lat = 0;
        double sum_height = 0;

        // Compute sum
        for (int i = 0; i < collection.size(); i++) {
            sum_long += collection.get(i).getLon();
            sum_lat += collection.get(i).getLat();
            sum_height += collection.get(i).getAlt();
        }

        // Compute average
        double length = (double) collection.size();
        double res_long = sum_long / length;
        double res_lat = sum_lat / length;
        double res_height = sum_height / length;

        return new Location(res_long, res_lat, res_height);
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

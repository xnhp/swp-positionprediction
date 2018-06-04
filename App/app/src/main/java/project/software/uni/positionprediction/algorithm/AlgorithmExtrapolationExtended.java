package project.software.uni.positionprediction.algorithm;

import android.content.Context;
import android.util.Log;

import java.util.Date;
import java.util.LinkedList;

import project.software.uni.positionprediction.datatype.BirdData;
import project.software.uni.positionprediction.datatype.Location3D;
import project.software.uni.positionprediction.datatype.TrackingPoint;
import project.software.uni.positionprediction.interfaces.SingleTrajPredictionAlgorithm;
import project.software.uni.positionprediction.movebank.SQLDatabase;

public class AlgorithmExtrapolationExtended implements SingleTrajPredictionAlgorithm {

    private final int weight_max = 100;
    private Context context;

    public AlgorithmExtrapolationExtended ( Context context ) {
        this.context = context;
    }


    /**
     * Main idea:
     * Compute the difference between two successive data points and take the average of them.
     * By adding the average to the last data point we get a very simple prediction algorithm
     *
     * @param date_past
     * @param date_pred
     * @param study_id
     * @param bird_id
     * @return
     */
    @Override
    public LinkedList<Location3D> predict(Date date_past, Date date_pred, int study_id, int bird_id) {

        // Still hardcoded
        int pastDataPoints = 10; // Use last 10 data points




        // Fetch data from database
        SQLDatabase db = SQLDatabase.getInstance(context);
        BirdData birddata = db.getBirdData(study_id, bird_id);
        TrackingPoint data[] = birddata.getTrackingPoints();

        // Check data
        if (data.length == 0) {
            Log.e("Error", "Size of data is 0");
            return null;
        }

        // Use only needed data
        Location3D loc_data[] = new Location3D[pastDataPoints];
        int size = data.length;
        for (int i = 0; i < pastDataPoints; i++) {
            loc_data[i] = data[size - 1 - pastDataPoints + i].getLocation().to3D();
        }

        // Compute prediction
        LinkedList<Location3D> prediction = next_Location(loc_data, pastDataPoints);
        return prediction;
    }


    /**
     * Computes the average differences in long. and lat. and adds it to the last position
     * [Average (last 2 Locations, Average last 3 Locations,...)]
     *
     * @param data
     * @return
     */
    public LinkedList<Location3D> next_Location(Location3D data[], int date) {
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
        LinkedList<Location3D> result_list = new LinkedList<>();
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
            sum_long += collection[i].getLoc_long();
            sum_lat += collection[i].getLoc_lat();
            sum_height += collection[i].getLoc_height();
        }

        // Compute average
        double length = (double) collection.length;
        double res_long = sum_long / length;
        double res_lat = sum_lat / length;
        double res_height = sum_height / length;

        return new Location3D(res_long, res_lat, res_height);
    }
}

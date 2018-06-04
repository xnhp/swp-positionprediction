package project.software.uni.positionprediction.algorithm;

import android.content.Context;
import android.location.Location;

import java.util.Date;

import project.software.uni.positionprediction.datatype.BirdData;
import project.software.uni.positionprediction.datatype.Location3D;
import project.software.uni.positionprediction.datatype.TrackingPoint;
import project.software.uni.positionprediction.interfaces.PredictionAlgorithm;
import project.software.uni.positionprediction.movebank.SQLDatabase;

public class AlgorithmExtrapolationExtended implements PredictionAlgorithm {

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
     * @param bird_id
     * @return
     */
    @Override
    public Location3D predict(Date date_past, Date date_pred, int study_id, int bird_id) {

        // Still hardcoded
        int constant = 10; // Use last 10 data points


        // Algorithm

        // Fetch data from database
        SQLDatabase db = SQLDatabase.getInstance(context);
        BirdData birddata = db.getBirdData(study_id, bird_id);
        TrackingPoint data[] = birddata.getTrackingPoints();

        // Check data
        if (data.length == 0) {

        }




        // Use only needed data
        Location3D geo[] = new Location3D[constant];
        for (int i = 0; i < constant; i++) {
            geo[i] = new Location3D(loc_long    [size - 1 - constant + i],
                                    loc_lat     [size - 1 - constant + i],
                                    loc_height  [size - 1 - constant + i]);
        }

        // Compute prediction
        Location3D prediction = next_Location(geo, constant);
        return prediction;
    }


    /**
     * Computes the average differences in long. and lat. and adds it to the last position
     * [Average (last 2 Locations, Average last 3 Locations,...)]
     *
     * @param data
     * @return
     */
    public Location3D next_Location(Location3D data[], int date) {
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
        return curr_loc.add(avg);
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

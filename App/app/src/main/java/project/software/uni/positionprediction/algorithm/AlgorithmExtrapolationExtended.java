package project.software.uni.positionprediction.algorithm;

import android.content.Context;
import android.util.Log;

import java.util.Date;

import project.software.uni.positionprediction.datatype.BirdData;
import project.software.uni.positionprediction.datatype.Location;
import project.software.uni.positionprediction.datatype.TrackingPoint;
import project.software.uni.positionprediction.interfaces.PredictionAlgorithmReturnsTrajectory;
import project.software.uni.positionprediction.movebank.SQLDatabase;

public class AlgorithmExtrapolationExtended implements PredictionAlgorithmReturnsTrajectory {

    private Context context;

    public AlgorithmExtrapolationExtended ( Context context ) {
        this.context = context;
    }


    /**
     * Main idea:
     *
     * ... ---> ---> ---> ---> X - - - >
     * vn   v3   v2   v1   v0      p1
     *
     * Name v1,...,vn the known vectors, where v1 is the last known vector. Compute the average
     * of the vectors (v1), (v1+v2), ..., (v1+..+vn). This gives a weighted average vector.
     * Add this vector to X and get p1.
     *
     * @param d
     * @param date_past
     * @param date_pred
     * @param study_id
     * @param bird_id
     * @return
     */
    @Override
    public Location predict(TrackingPoint d[], Date date_past, Date date_pred, int study_id, int bird_id) {

        // Size error
        if (d.length == 0) {
            Log.e("Error", "Data has length 0");
        }

        // Date error
        Date now = new Date();
        if (now.before(date_past)) {
            Log.e("Error", "Past date is in the future");
        }

        // ID error
        if (study_id <= 0) {
            Log.e("Error", "ID not valid");
        }
        if (bird_id <= 0) {
            Log.e("Error", "ID not valid");
        }




        // Still hardcoded
        int constant = 10; // Use last 10 data points




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
        Location loc_data[] = new Location[constant];
        int size = data.length;
        for (int i = 0; i < constant; i++) {
            loc_data[i] = data[size - 1 - constant + i].getLocation().to3D();
        }

        // Compute prediction
        return next_Location(loc_data, constant);
    }


    /**
     * Computes the average differences in long. and lat. and adds it to the last position
     * [Average (last 2 Locations, Average last 3 Locations,...)]
     *
     * @param data
     * @return
     */
    public Location next_Location(Location data[], int date) {
        int n = data.length - 1;
        Location vector_collection[] = new Location[date - 1];
        // Fill collection
        for (int t = 1; t < date; t++) {
            // Compute difference of pair n and n-t
            // Get n-th point
            Location vec_n = data[n];

            // Get n-t point
            Location vec_old = data[n-t];

            // Compute vector between them
            Location vec_delta = vec_n.subtract(vec_old);

            // Compute average
            Location vec_avg = vec_delta.divide(t);

            // Add vector to collection
            vector_collection[t - 1] = vec_avg;
        }

        // Compute average of all computed vectors in collection
        Location avg = weighted_average(vector_collection);
        Location curr_loc = data[data.length - 1];

        // Add avg vector to current Location
        return curr_loc.add(avg);
    }


    /**
     * Computes (weighted) average of Location vectors
     *
     * @param collection
     * @return
     */
    public Location weighted_average(Location collection[]) {
        double sum_long = 0;
        double sum_lat = 0;
        double sum_height = 0;

        // Compute sum
        for (int i = 0; i < collection.length; i++) {
            sum_long += collection[i].getLon();
            sum_lat += collection[i].getLat();
            sum_height += collection[i].getAlt();
        }

        // Compute average
        double length = (double) collection.length;
        double res_long = sum_long / length;
        double res_lat = sum_lat / length;
        double res_height = sum_height / length;

        return new Location(res_long, res_lat, res_height);
    }
}

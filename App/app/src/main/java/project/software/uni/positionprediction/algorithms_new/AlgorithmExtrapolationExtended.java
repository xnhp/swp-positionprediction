package project.software.uni.positionprediction.algorithms_new;

import android.content.Context;
import android.util.Log;

import org.apache.commons.math3.exception.InsufficientDataException;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import project.software.uni.positionprediction.datatypes_new.Location;
import project.software.uni.positionprediction.datatypes_new.LocationWithValue;
import project.software.uni.positionprediction.datatypes_new.Locations;
import project.software.uni.positionprediction.datatypes_new.PredictionUserParameters;
import project.software.uni.positionprediction.datatypes_new.PredictionBaseData;
import project.software.uni.positionprediction.datatypes_new.PredictionResultData;
import project.software.uni.positionprediction.datatypes_new.Trajectory;
import project.software.uni.positionprediction.util.Debug;
import project.software.uni.positionprediction.util.Message;

public class AlgorithmExtrapolationExtended extends PredictionAlgorithmReturnsTrajectory {

    private Context c;
    private Debug d = new Debug();

    public AlgorithmExtrapolationExtended(Context c) {
        this.c = c;
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
     */
    @Override
    public PredictionResultData predict(PredictionUserParameters params, PredictionBaseData data) {

        // Compute prediction
        return next_Location(data.getTrajectory(), params.date_past, params.date_pred);

    }


    /**
     * Computes the average differences in long. and lat. and adds it to the last position
     * [Average (last 2 Locations, Average last 3 Locations,...)]
     *
     * @param data
     * @return
     */
    public PredictionResultData next_Location(Trajectory data, Date date_past, Date date_pred) {
        Log.e("Date", "1"+((LocationWithValue) data.getLocation(0)).getValue().toString() );
        Log.e("Date", "2"+((LocationWithValue) data.getLocation(1)).getValue().toString() );
        Log.e("Date", "3"+((LocationWithValue) data.getLocation(2)).getValue().toString() );
        Log.e("Date", "4"+((LocationWithValue) data.getLocation(3)).getValue().toString() );
        Log.e("Date", "5"+((LocationWithValue) data.getLocation(4)).getValue().toString() );
        d.printDates(data);

        Log.e("Size of Data input", ""+ data.size());

        // Check for datatype correctness
        boolean has_timestamps = false;
        if (data.getLocation(0) instanceof LocationWithValue) {
            has_timestamps = true;
            Log.e("Type-checking", "Locations have timestamps!");
        } else if (data.getLocation(0) instanceof Location) {
            Log.e("Type-checking", "Locations don't have timestamps!");
        } else {
            Log.e("Type-checking", "Type couldn't be resolved!");
        }


        int n = data.size() - 1;
        ArrayList<Location> vector_collection = new ArrayList<>();
        int c = 0;

        // Fill collection
        for (int t = 1; t < n; t++) {
            LocationWithValue loc_t = (LocationWithValue) data.getLocation(n-t);
            Date date_t = (Date) loc_t.getValue();

            // Break if date until we want the data is reached
            if (date_t.before(date_past)) {

                Log.e("Break", "" + c + " data points were used for prediction");
                Log.e("Use Data from", date_past.toString());
                Log.e("Date which breaks loop", date_t.toString());
                break;
            }
            c++; // Count for Log.e

            // Compute difference of pair n and n-t
            // Get n-th point
            Location vec_n = data.getLocation(n);

            // Get n-t point
            Location vec_old = data.getLocation(n-t);

            // Compute vector between them
            Location vec_delta = vec_n.subtract(vec_old);

            // Compute average
            Location vec_avg = vec_delta.divide(t);

            // Add vector to collection
            //vector_collection.set(t - 1, vec_avg);
            vector_collection.add(vec_avg);
        }

        // if the collection is empty at this point, it means that there was no
        // data available within the requested lower bound.
        if (vector_collection.size() == 0) {
            Log.e("algorithm", "no data within given lower bound for timestamps");
            // todo: handle this, show notice to user.
        }

        // Compute prediction factor
        double pred_factor;
        if (has_timestamps) {
            pred_factor = (date_pred == null)? 1 : compute_pred_length(data.getLocations(), date_pred, vector_collection.size());
        } else {
            pred_factor = 1;
        }



        // Compute average of all computed vectors in collection
        // avg has altitude or not based on the data that is passed in
        Location avg = weighted_average(vector_collection);
        // curr_loc has altitude or not based on the data that is passed in
        Location curr_loc = data.getLocation(data.size() - 1);

        // Add Vector to current one
        Trajectory traj = new Trajectory();
        traj.addLocation( curr_loc.add( avg.multiply( pred_factor) ) );

        return new PredictionResultData(traj);

    }


    /**
     * Computes (weighted) average of Location vectors
     * todo: move this to class Locations
     *
     * @param collection
     * @return
     */
    public Location weighted_average(ArrayList<Location> collection) {

        if (collection.size() == 0) {
            Log.e("algorithm", "cannot compute average of empty collection");
        }

        double sum_long = 0;
        double sum_lat = 0;
        double sum_height = 0;

        // we only consider the weighted average to have an altitude
        // if all locations in the collection have an altitude set.
        boolean have_alt = collection.get(0).hasAltitude();

        // Compute sum
        for (int i = 0; i < collection.size(); i++) {

            sum_long += collection.get(i).getLon();
            sum_lat += collection.get(i).getLat();

            if (have_alt) {
                sum_height += collection.get(i).getAlt();
            } else {
                // if any location in the given collection does not have
                // alt set, do not consider altitude at all and return
                // a weighted average location without altitude
                have_alt = false;
            }
        }

        // Compute average
        double length = (double) collection.size();
        double res_long = sum_long / length;
        double res_lat = sum_lat / length;
        double res_height = sum_height / length;

        if (have_alt) {
            // return a location with altitude
            return new Location(res_long, res_lat, res_height);
        } else {
            // return a location *without* altitude
            return new Location(res_long, res_lat);
        }

    }



    private double compute_pred_length(Locations data, Date date_pred, int nr_of_pts) {
        if (data.size() < nr_of_pts ){
            Message m = new Message();
            m.disp_error(c, "Data size", "There is to less data to compute a good result",true);
            return 1;
        }

        // Get the used tracking points
        LinkedList<Number> delta_ms = new LinkedList<Number>();
        int n = data.size();
        for (int i = 0; i<nr_of_pts; i++){
            LocationWithValue p_curr = (LocationWithValue) data.get(n - nr_of_pts + i);
            LocationWithValue p_before = (LocationWithValue) data.get(n - nr_of_pts + i - 1);
            Date t1 = (Date) p_curr.getValue();
            Date t2 = (Date) p_before.getValue();
            long delta_t = Math.abs(t1.getTime() - t2.getTime());
            delta_ms.add(delta_t);
        }

        // Get average time between Trackingpoints
        long sum = 0;
        int m = delta_ms.size();
        for (int j = 0; j < m; j++) {
            sum = sum + (long) delta_ms.get(j);
        }
        long avg = (m == 0)? 0 : sum / m;
        Log.e("Note", "Average of last few data points (in millis) = " + avg );

        // Get relative frequency of avg time in whole in prediction
        long duration_pred = date_pred.getTime();
        double freq = avg / duration_pred;

        return freq;

    }

}

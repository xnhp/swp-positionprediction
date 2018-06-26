package project.software.uni.positionprediction.algorithms_new;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import project.software.uni.positionprediction.datatypes_new.Collection;
import project.software.uni.positionprediction.datatypes_new.EShape;
import project.software.uni.positionprediction.datatypes_new.Location;
import project.software.uni.positionprediction.datatypes_new.LocationWithValue;
import project.software.uni.positionprediction.datatypes_new.Locations;
import project.software.uni.positionprediction.datatypes_new.PredictionBaseData;
import project.software.uni.positionprediction.datatypes_new.PredictionResultData;
import project.software.uni.positionprediction.datatypes_new.PredictionUserParameters;
import project.software.uni.positionprediction.datatypes_new.Trajectory;
import project.software.uni.positionprediction.util.Message;

public class AlgorithmExtrapolationExtendedFunnel extends PredictionAlgorithmReturnsTrajectory {

    Context c;
    GeneralComputations comp = new GeneralComputations();

    public AlgorithmExtrapolationExtendedFunnel(Context c) {
        this.c = c;
    }


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
                Log.e("Break", "" + c + " data points where used for prediction");
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

        // Compute prediction factor
        double pred_factor;
        if (has_timestamps) {
            pred_factor = (date_pred == null)? 1 : compute_pred_length(data.getLocations(), date_pred, vector_collection.size());
        } else {
            pred_factor = 1;
        }



        // Compute average of all computed vectors in collection
        Location avg = weighted_average(vector_collection);
        Location curr_loc = data.getLocation(data.size() - 1);

        // Add Vector to current one
        Trajectory traj = new Trajectory();

        double alpha = comp.getAngleVariance(data);
        traj.addLocation( new LocationWithValue<>(curr_loc.add( avg.multiply( pred_factor)), alpha));

        return new PredictionResultData(traj);

    }


    /**
     * Computes (weighted) average of Location vectors
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

            if (collection.get(i).hasAltitude()) {
                Log.i("algorithm", "a location has altitude set!");
            } else {
                Log.i("algorithm", "a location doesn't have alt set!");
            }

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

        // Get average time between Tracking points
        long sum = 0;
        int m = delta_ms.size();
        for (int j = 0; j < n; j++) {
            sum = sum + (long) delta_ms.get(j);
        }
        long avg = sum / m;
        Log.e("Note", "Average of last few data points (in millis) = " + avg );

        // Get relative frequency of avg time in whole in prediction
        long duration_pred = date_pred.getTime();
        double freq = avg / duration_pred;

        return freq;

    }


}

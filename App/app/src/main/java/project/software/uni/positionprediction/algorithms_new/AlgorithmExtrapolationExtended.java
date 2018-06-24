package project.software.uni.positionprediction.algorithms_new;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import project.software.uni.positionprediction.datatype.SingleTrajectory;
import project.software.uni.positionprediction.datatype.TrackingPoint;
import project.software.uni.positionprediction.datatype.TrackingPoints;
import project.software.uni.positionprediction.datatypes_new.Location;
import project.software.uni.positionprediction.datatypes_new.Locations;
import project.software.uni.positionprediction.datatypes_new.PredictionUserParameters;
import project.software.uni.positionprediction.datatypes_new.PredictionBaseData;
import project.software.uni.positionprediction.datatypes_new.PredictionResultData;
import project.software.uni.positionprediction.datatypes_new.Trajectory;
import project.software.uni.positionprediction.util.Message;

public class AlgorithmExtrapolationExtended extends PredictionAlgorithmReturnsTrajectory {

    private Context c;

    public AlgorithmExtrapolationExtended() {
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
    public Location next_Location(Trajectory data, Date date_past, Date date_pred) {
        int n = data.size() - 1;
        ArrayList<project.software.uni.positionprediction.datatype.Location> vector_collection = new ArrayList<>();
        int c = 0;

        // Fill collection
        for (int t = 1; t < n; t++) {
            Date date_t = data.getLocation(n-t).ge();

            // Break if date until we want the data is reached
            if (date_t.before(date_past)) {
                Log.e("Break", "" + c + " data points where used for prediction");
                break;
            }
            c++; // Count for Log.e

            // Compute difference of pair n and n-t
            // Get n-th point
            project.software.uni.positionprediction.datatype.Location vec_n = data.get(n).getLocation();

            // Get n-t point
            project.software.uni.positionprediction.datatype.Location vec_old = data.get(n-t).getLocation();

            // Compute vector between them
            project.software.uni.positionprediction.datatype.Location vec_delta = vec_n.subtract(vec_old);

            // Compute average
            project.software.uni.positionprediction.datatype.Location vec_avg = vec_delta.divide(t);

            // Add vector to collection
            //vector_collection.set(t - 1, vec_avg);
            vector_collection.add(vec_avg);
        }

        // Compute prediction factor
        double pred_factor = (date_pred == null)? 1 : compute_pred_length(data, date_pred, vector_collection.size());


        // Compute average of all computed vectors in collection
        project.software.uni.positionprediction.datatype.Location avg = weighted_average(vector_collection);
        project.software.uni.positionprediction.datatype.Location curr_loc = data.get(data.getLength() - 1).getLocation();

        // Add avg vector to current Location
        return curr_loc.add( avg.multiply(pred_factor) );

    }


    /**
     * Computes (weighted) average of Location vectors
     *
     * @param collection
     * @return
     */
    public project.software.uni.positionprediction.datatype.Location weighted_average(ArrayList<project.software.uni.positionprediction.datatype.Location> collection) {
        double sum_long = 0;
        double sum_lat = 0;
        double sum_height = 0;

        // Compute sum
        for (int i = 0; i < collection.size(); i++) {

            if (collection.get(i).has_altitude) {
                Log.i("algorithm", "a location has altitude set!");
            } else {
                Log.i("algorithm", "a location doesnt have alt set!");
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

        return new project.software.uni.positionprediction.datatype.Location(res_long, res_lat, res_height);
    }



    private double compute_pred_length(TrackingPoints data, Date date_pred, int nr_of_pts) {
        if (data.getLength() < nr_of_pts ){
            Message m = new Message();
            m.disp_error(c, "Data size", "There is to less data to compute a good result",true);
            return 1;
        }

        // Get the used tracking points
        LinkedList<Number> delta_ms = new LinkedList<Number>();
        int n = data.getLength();
        for (int i = 0; i<nr_of_pts; i++){
            TrackingPoint p_curr = data.get(n - nr_of_pts + i);
            TrackingPoint p_before = data.get(n - nr_of_pts + i - 1);
            long t1 = p_curr.getDate().getTime();
            long t2 = p_before.getDate().getTime();
            long delta_t = Math.abs(t1-t2);
            delta_ms.add(delta_t);
        }

        // Get average time between Trackingpoints
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

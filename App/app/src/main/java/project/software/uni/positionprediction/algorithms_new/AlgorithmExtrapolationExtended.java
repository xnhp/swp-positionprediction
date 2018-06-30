package project.software.uni.positionprediction.algorithms_new;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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

public class AlgorithmExtrapolationExtended extends PredictionAlgorithmReturnsTrajectory {

    // General variables
    private Context c;
    private GeneralComputations gc = new GeneralComputations();
    private Message msg = new Message();


    // Variables for robustness
    private boolean use_all_data = false;
    private boolean no_date_pred_available = false;
    private boolean has_timestamps = false;


    // Constructor
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

        // Check input
        // ===========

        // Check trajectory size
        if (data == null || data.getTrajectory().size() == 0) {
            msg.disp_error(this.c, "Data size", "The algorithm doesn't get enough data");
            return null;
        }
        // Check dates
        if (params.date_past == null) {
            msg.disp_error(this.c, "Date Error", "No date avaiable. Trying to use all data in given Trajectory!");
            use_all_data = true;
        }
        if (params.date_past.after(new Date())) {
            msg.disp_error(this.c, "Date Error", "Date is in the future. Trying to use all data in given Trajectory!");
            use_all_data = true;
        }
        if (params.date_pred == null) {
            msg.disp_error(this.c, "Date Error", "No date avaiable. Prediction will be made, but the future time is not correct!");
            no_date_pred_available = true;
        }
        if (params.date_pred.before(new Date())) {
            msg.disp_error(this.c, "Date Error", "No correct date avaiable. Prediction will be made, but the future time is not correct!");
            no_date_pred_available = true;
        }
        // Check data
        if (data.getTrajectory().getLocation(0) instanceof LocationWithValue) {
            has_timestamps = true;
            Log.e("Type-checking", "Locations have timestamps!");
        } else if (data.getTrajectory().getLocation(0) instanceof Location) {
            Log.e("Type-checking", "Locations don't have timestamps!");
        } else {
            Log.e("Type-checking", "Type couldn't be resolved!");
        }



        // Compute prediction
        // ==================
        return make_prediction(data.getTrajectory(), params.date_past, params.date_pred);

    }









    /**
     * Computes the average differences in long. and lat. and adds it to the last position
     * [Average (last 2 Locations, Average last 3 Locations,...)]
     *
     * @param data
     * @return
     */
    private PredictionResultData make_prediction(Trajectory data, Date date_past, Date date_pred) {

        // 1. Get old vectors
        ArrayList<Location> vector_collection = get_past_vectors(data, date_past);

        // 2. Compute prediction factor
        double pred_factor = compute_pred_factor(date_pred, data, vector_collection.size());

        // 3. Compute prediction
        Location predicted_Location = predict_next_Location(data, vector_collection, pred_factor);

        // 4. Make Prediction result date
        return create_prediction_result_data(predicted_Location);

    }


    /**
     * @param predicted_Location
     * @return
     */
    private PredictionResultData create_prediction_result_data(Location predicted_Location) {
        Trajectory traj = new Trajectory();
        traj.addLocation( predicted_Location );
        return new PredictionResultData(traj);
    }



    /**
     *
     * @param data
     * @param vector_collection
     * @param pred_factor
     * @return
     */
    private Location predict_next_Location(Trajectory data, ArrayList<Location> vector_collection, double pred_factor){
        // avg has altitude or not based on the data that is passed in
        Location avg = weighted_average(vector_collection).to3D();
        // curr_loc has altitude or not based on the data that is passed in
        Location curr_loc = data.getLocation(data.size() - 1).to3D();

        return curr_loc.add( avg.multiply( pred_factor));
    }



    /**
     *
     * @param date_pred
     * @param data
     * @param size
     * @return
     */
    private double compute_pred_factor(Date date_pred, Trajectory data, int size){
        double pred_factor;
        if (has_timestamps) {
            pred_factor = (date_pred == null || no_date_pred_available )? 1 : compute_pred_length(data.getLocations(), date_pred, size);
        } else {
            pred_factor = 1;
        }
        return pred_factor;
    }


    /**
     *
     * @param data
     * @param date_past
     * @return
     */
    private ArrayList<Location> get_past_vectors(Trajectory data, Date date_past) {

        ArrayList<Location> vector_collection = new ArrayList<Location>();
        int n = data.size() - 1;
        int c = 0;

        // Fill collection
        for (int t = 1; t < n; t++) {
            LocationWithValue loc_t = (LocationWithValue) data.getLocation(n - t);
            Date date_t = (Date) loc_t.getValue();

            // Break if date until we want the data is reached or use all data when something went wrong with the date
            if (date_t.before(date_past) && !use_all_data) {
                Log.e("Break", "" + c + " data points were used for prediction");
                break;
            }
            c++; // Count for Log.e

            // Compute difference of pair n and n-t
            // Get n-th point
            Location vec_n = data.getLocation(n);

            // Get n-t point
            Location vec_old = data.getLocation(n - t);

            // Compute vector between them
            Location vec_delta = vec_n.subtract(vec_old);

            // Compute average
            Location vec_avg = vec_delta.divide(t);

            // Add vector to collection
            //vector_collection.set(t - 1, vec_avg);
            vector_collection.add(vec_avg);
        }

        return vector_collection;
    }



    /**
     * Computes (weighted) average of Location vectors
     *
     * @param collection
     * @return
     */
    private Location weighted_average(ArrayList<Location> collection) {
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



    /**
     *
     * @param data
     * @param date_pred
     * @param nr_of_pts
     * @return
     */
    private double compute_pred_length(Locations data, Date date_pred, int nr_of_pts) {
        if (data.size() < nr_of_pts ){
            Message m = new Message();
            m.disp_error(c, "Data size", "There is to less data to compute a good result");
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

        if (delta_ms.size() == 0) {
            msg.disp_error(c, "d", "d");
            return 1;
        }


        // Get average time between Tracking points
        long sum = 0;
        int m = delta_ms.size();
        for (int j = 0; j < nr_of_pts; j++) {
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

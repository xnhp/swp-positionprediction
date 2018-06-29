package project.software.uni.positionprediction.algorithms_new;


import android.content.Context;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import project.software.uni.positionprediction.datatypes_new.Collection;
import project.software.uni.positionprediction.datatypes_new.Location;
import project.software.uni.positionprediction.datatypes_new.LocationWithValue;
import project.software.uni.positionprediction.datatypes_new.PredictionBaseData;
import project.software.uni.positionprediction.datatypes_new.PredictionResultData;
import project.software.uni.positionprediction.datatypes_new.PredictionUserParameters;
import project.software.uni.positionprediction.datatypes_new.Trajectory;
import project.software.uni.positionprediction.util.Message;
import project.software.uni.positionprediction.util.XML;


public class AlgorithmSimilarTrajectoryFunnel extends PredictionAlgorithmReturnsTrajectories {

    Message msg = new Message();
    Context c;
    GeneralComputations gc = new GeneralComputations();

    // Variables for robustness
    private boolean use_all_data = false;
    private boolean no_date_pred_available = false;
    private boolean has_timestamps = false;

    // Class variables
    PredictionUserParameters params;
    PredictionBaseData data;

    // Other variables
    private double eps = 0.1;
    private int traj_length = 5;


    // Constructor
    public AlgorithmSimilarTrajectoryFunnel(Context c) {
        this.c = c;
    }


    /**
     * Main idea:
     * <p>
     * Get a list of angles of the last few vectors relative to the last known vector. Then iterate
     * through the whole data and compute the list of angles for every data point. Mostly the
     * computation of the list in every point can be stopped after a few (mostly 0 or 1) computations
     * and comparisons, because it is very unlikely that three successive vectors have exactly the
     * same angles as our last known trajectory.
     * If we found the indexes, where similar trajectories end, we have to compute the relative angle
     * of the next vectors (which helps us to predict the behaviour of the bird). Then add the
     * last known vector of the whole data and turn it by the relative angle of the given trajectory.
     *
     * @return
     */
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


        // Save variables
        this.params = params;
        this.data = data;


        // Compute prediction
        // ==================
        return make_prediction();


    }








    private PredictionResultData make_prediction() {

        // 1. Get List of angles of current trajectory
        LinkedList<Number> angles = get_angle_list();

        // 2. Compute possible indices where old trajectories are similar
        LinkedList<Number> possible_indices = get_possible_trajectory_indices(angles);

        // 3. Compute uncertainty of all data
        double uncertainty = gc.getAngleVariance(data.getTrajectory());

        // 4. Compute how many points we need in the future
        int pred_traj_length = get_number_of_future_points();

        // 5. Get trajectories of all found indices
        return createResultData( get_trajectories_from_indices(possible_indices, uncertainty, pred_traj_length) );

    }





    private Collection<Trajectory> get_trajectories_from_indices(LinkedList<Number> possible_indices, double uncertainty, double pred_traj_length) {

        if (possible_indices.size() == 0) {
            Log.d("Warning", "No similar trajectories found");
            return null;
        }



        Collection<Trajectory> trajectories = new Collection<Trajectory>();

        // Compute main angle. All other angles (delta_angles) are computed relative to this one.
        Location n0 = data.getTrajectory().getLocation(data.getTrajectory().size()-1).to3D();
        Location n1 = data.getTrajectory().getLocation(data.getTrajectory().size()-2).to3D();
        Location nth_vector = n0.subtract(n1);


        for (int l = 0; l < possible_indices.size(); l++) {

            Trajectory trajectory = new Trajectory();
            LocationWithValue new_loc = new LocationWithValue(n0, uncertainty);
            Location new_vector = nth_vector;

            for (int m = 0; m < pred_traj_length; m++) {
                // Last locations of similar trajectory
                Location pos1 = data.getTrajectory().getLocation((int) possible_indices.get(l) + m - 1);
                Location pos2 = data.getTrajectory().getLocation((int) possible_indices.get(l) + m);
                // Last vector of known trajectory
                Location vector = pos2.subtract(pos1);

                // First locations after similar trajectory
                Location next1 = data.getTrajectory().getLocation((int) possible_indices.get(l) + m + 1);
                Location next2 = data.getTrajectory().getLocation((int) possible_indices.get(l) + m + 2);
                // First vector after similiar trajectory (want to get this angle)
                Location iter_vector = next2.subtract(next1);

                // Relative angle to vector from similiar trajectoy
                double gamma = iter_vector.dotProduct(vector);
                // Rotate old vector with relative angle as known from similar trajectories
                new_vector = new_vector.rotate(gamma);
                // Add vector to current location
                new_loc.setLocation( new_loc.add(new_vector) );
                new_loc.setValue( uncertainty * uncertainty_factor(m+1) );

                // Add new locations to current trajectory
                trajectory.addLocation(new_loc);

            }
            // Add trajectory to List
            Log.i("algorithm", "size of trajectory: " + trajectory.size());
            trajectories.add(trajectory);
        }

        return trajectories;
    }


    private int get_number_of_future_points(){

        int n = data.getTrajectory().size()-1;
        LocationWithValue location = (LocationWithValue) data.getTrajectory().getLocation(n);
        Date date_now_minus_hours_pred = get_past_date();

        if (date_now_minus_hours_pred == null) {
            Log.e("Error","No hours between now and pred");
            return 0;
        }


        int count = 0;
        Date date_of_location = (Date) location.getValue();
        while (date_of_location.after(date_now_minus_hours_pred)) {
            count++;
            location = (LocationWithValue) data.getTrajectory().getLocation(n-count);
            date_of_location = (Date) location.getValue();
        }
        Log.e("Number of taken points", ""+count);
        return count;
    }



    private Date get_past_date(){
        long now = new Date().getTime();
        long date_pred = params.date_pred.getTime();
        long duration = date_pred - now;
        long hours = duration / 1000 / 60 / 60 ;
        Log.e("hours computation", ""+hours);
        Log.e("Date pred", params.date_pred.toString());
        int hours1 = Log.e("Hours", ""+new XML().getHours_fut());
        long past = now - hours;
        return new Date(past);
    }





    private LinkedList<Number> get_possible_trajectory_indices(LinkedList<Number> angles){

        if (angles.size() == 0) {
            Log.d("Warning", "No angles are in the list.");
            return null;
        }


        LinkedList<Number> possible_indices = new LinkedList<Number>();
        Trajectory traj = data.getTrajectory();

        int size = data.getTrajectory().size();
        Date last_known_date = (Date) ((LocationWithValue) data.getTrajectory().getLocation(size-1) ).getValue();
        long pred_duration = params.date_pred.getTime() - last_known_date.getTime();
        long date_last_as_long = last_known_date.getTime() - pred_duration;
        Date date_last = new Date(date_last_as_long);

        // Run through all locations (or endpoint of all trajectories)
        for (int i = traj_length + 1; i < size; i++) {

            LocationWithValue loc_t = (LocationWithValue) traj.getLocation(i);
            Date date_t = (Date) loc_t.getValue();

            if (date_t.after(params.date_past) && date_t.before(date_last)) {

                System.out.println("LOCATION: " + i);

                // Check if angle is approx. the same
                boolean is_similar = false;

                Location m0 = data.getTrajectory().getLocation(i - 1).to3D();
                // this works correctly
                Log.i("algorithm", "m0: " + m0.toString());

                Location m1 = data.getTrajectory().getLocation(i - 2).to3D();
                Location mth_vector = m0.subtract(m1);

                // Run backwards through every trajectory
                for (int k = 2; k < traj_length; k++) {
                    is_similar = false;
                    System.out.println("traj: " + k);
                    // Get angle of two locations
                    Location loc1 = data.getTrajectory().getLocation(i - k).to3D();
                    Location loc2 = data.getTrajectory().getLocation(i - k - 1).to3D();
                    Location vector = loc1.subtract(loc2);
                    double beta = vector.getAngle(mth_vector);
                    System.out.println("beta = " + beta + ", other = " + angles.get(k - 1));

                    if (Math.abs(beta - (double) angles.get(k - 1)) < eps) {
                        is_similar = true;
                        System.out.println("Similar");
                    }
                    if (!is_similar) {
                        System.out.println("Break");
                        break; // The trajectory is probably not similar, so we don't check the other locations of the trajectory
                    }
                }

                // If all locations are similar, we can add the last point of the trajectory to our list
                if (is_similar) {
                    possible_indices.add(i);
                }
            }
        }

        // Feedback
        Log.i("algorithm", "possible_indices size: " + possible_indices.size());
        if (possible_indices.size() == 0) {
            Log.e("No trajectory found", "There are no similar trajectories!");
            msg.disp_error(c, "No trajectory found", "There are no similar trajectories!");
            return null;
        } else {
            Log.i("Number of trajectories", ""+possible_indices.size());
        }

        return possible_indices;

    }


    private LinkedList<Number> get_angle_list(){
        // Length of trajectory
        int traj_length = 5;
        int pred_traj_length = 5;

        Collection<Trajectory> trajectories = new Collection<>();

        // Algorithm
        int size = data.getTrajectory().size();

        double eps = 0.1; //1E-5;

        // Get the trajectory (list of angles) you want to compare other trajectories
        LinkedList<Number> delta_angles = new LinkedList<Number>();

        // Compute main angle. All other angles (delta_angles) are computed relative to this one.
        Location n0 = data.getTrajectory().getLocation(size - 1).to3D();
        Location n1 = data.getTrajectory().getLocation(size - 2).to3D();
        Location nth_vector = n0.subtract(n1);

        // Compare all other angles with main vector to get relative angles (rotation of n_th vector doesn't matter)
        for (int j = 2; j <= traj_length; j++) {
            Location loc1 = data.getTrajectory().getLocation(size - j).to3D();
            Location loc2 = data.getTrajectory().getLocation(size - j - 1).to3D();
            Location vector = loc1.subtract(loc2);
            double alpha = vector.getAngle(nth_vector);
            delta_angles.add(alpha);
        }

        return delta_angles;
    }








    /**
     * todo: possible other function to change the factor which increases factor of uncertainty
     * @param m
     * @return
     */
    private double uncertainty_factor(int m){
            return m;
        }
    }



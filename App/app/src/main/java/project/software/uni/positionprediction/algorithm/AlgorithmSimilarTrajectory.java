package project.software.uni.positionprediction.algorithm;

import android.content.Context;
import android.renderscript.Matrix3f;

import org.apache.commons.math3.linear.MatrixUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import project.software.uni.positionprediction.datatype.AngleSteplength;
import project.software.uni.positionprediction.datatype.BirdData;
import project.software.uni.positionprediction.datatype.Location;
import project.software.uni.positionprediction.datatype.Location3D;
import project.software.uni.positionprediction.datatype.MultipleTrajectories;
import project.software.uni.positionprediction.datatype.SingleTrajectory;
import project.software.uni.positionprediction.datatype.TrackingPoint;
import project.software.uni.positionprediction.interfaces.PredictionAlgorithm;
import project.software.uni.positionprediction.movebank.SQLDatabase;



public class AlgorithmSimilarTrajectory implements PredictionAlgorithm_MultipleTrajectories {

    private Context context;

    public AlgorithmSimilarTrajectory ( Context context ) {
        this.context = context;
    }


    @Override
    public MultipleTrajectories predict(Date date_past, Date date_pred, int study_id, int bird_id) {



        // Fetch data from database
        SQLDatabase db = SQLDatabase.getInstance(context);
        BirdData birddata = db.getBirdData(study_id, bird_id);
        TrackingPoint data[] = birddata.getTrackingPoints();


        // Length of trajectory
        int traj_length = 5;
        int pred_traj_length = 5;
        double treshhold_direction = 0.5;





        // Algorithm
        int size = data.length;

        double eps = 1E-5;

        // Get the trajectory (list of angles) you want to compare other trajectories
        List<Number> delta_angles = new LinkedList<Number>();

        // Compute main angle. All other angles (delta_angles) are computed relative to this one.
        Location n0 = data[size-1].getLocation().to3D();
        Location n1 = data[size-2].getLocation().to3D();
        Location nth_vector = n0.subtract(n1);

        // Compare all other angles with main vector to get relative angles (rotation of n_th vector doesn't matter)
        for (int j = 2; j <= traj_length; j++) {
            Location loc1 = data[size-j].getLocation().to3D();
            Location loc2  = data[size-j-1].getLocation().to3D();
            Location vector = loc1.subtract(loc2);
            double alpha = vector.getAngle( nth_vector );
            delta_angles.add(alpha);
        }

        // Find possible other trajectories and add the index of the last point to list
        List<Number> possible_indices = new LinkedList<Number>();

        // Run through all locations (or endpoint of all trajectories)
        for (int i = traj_length + 1;  i < size; i++) {
            System.out.println("LOCATION: " + i);

            // Check if angle is approx. the same
            boolean is_similar = false;

            Location m0 = data[i-1].getLocation().to3D();
            Location m1 = data[i-2].getLocation().to3D();
            Location mth_vector = m0.subtract(m1);

            // Run backwards through every trajectory
            for (int k = 2; k < traj_length; k++) {
                is_similar = false;
                System.out.println("traj: " + k);
                // Get angle of two locations
                Location loc1 = data[i-k].getLocation().to3D();
                Location loc2  = data[i-k-1].getLocation().to3D();
                Location vector = loc1.subtract(loc2);
                double beta = vector.getAngle( mth_vector );

                if ( Math.abs( beta - (double) delta_angles.get(k-1) ) < eps ) {
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


        MultipleTrajectories trajectories = new MultipleTrajectories();

        for (int l = 0; l < possible_indices.size(); l++) {

            SingleTrajectory trajectory = new SingleTrajectory();
            Location new_loc = n0;
            Location new_vector = nth_vector;

            for (int m = 0; m < pred_traj_length; m++) {
                // Last locations of similar trajectory
                Location pos1 = data[(int) possible_indices.get(l) + m - 1 ].getLocation();
                Location pos2 = data[(int) possible_indices.get(l) + m     ].getLocation();
                // Last vector of known trajectory
                Location vector = pos2.subtract(pos1);

                // First locations after similar trajectory
                Location next1 = data[(int) possible_indices.get(l) + m + 1].getLocation();
                Location next2 = data[(int) possible_indices.get(l) + m + 2].getLocation();
                // First vector after similiar trajectory (want to get this angle)
                Location iter_vector = next2.subtract(next1);

                // Relative angle to vector from similiar trajectoy
                double gamma = iter_vector.scalarproduct(vector);
                // Rotate old vector with relative angle as known from similar trajectories
                new_vector = new_vector.rotate(gamma);
                // Add vector to current location
                new_loc = new_loc.add(new_vector);

                // Add new locations to current trajectory
                trajectory.add(new_loc);
            }
            // Add trajectory to List
            trajectories.add(trajectory);
        }

        return trajectories;
    }
}

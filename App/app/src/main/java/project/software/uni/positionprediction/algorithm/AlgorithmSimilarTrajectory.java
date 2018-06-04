package project.software.uni.positionprediction.algorithm;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import project.software.uni.positionprediction.datatype.Debug;
import project.software.uni.positionprediction.datatype.Location3D;
import project.software.uni.positionprediction.interfaces.SingleTrajPredictionAlgorithm;

/**
 * This algorithm has a weakness. See Sebastian's presentation.
 */
public class AlgorithmSimilarTrajectory implements SingleTrajPredictionAlgorithm {

    @Override
    public LinkedList<Location3D> predict(PredictionUserParameters params, PredictionBaseData data) {
        // Length of trajectory
        int traj_length = 5;

        int number_of_comp = 5; // Use last 5 data points

        // Algorithm
        int size = data.pastTracks.length;

        double eps = 1E-5;

        // Get the trajectory (list of angles) you want to compare other trajectories
        List<Number> angles = new LinkedList<Number>();
        for (int j = 1; j <= traj_length; j++) {
            Location3D curr_loc = data.pastTracks[size-j].to3D();
            Location3D pre_loc  = data.pastTracks[size-j-1].to3D();
            double alpha = curr_loc.getAngle(pre_loc);
            angles.add(alpha);
        }

        // Find possible other trajectories and add the index of the last point to list
        List<Number> possible_indices = new LinkedList<Number>();

        // Run through all locations (or endpoint of all trajectories)
        for (int i = traj_length + 1;  i < size; i++) {
            System.out.println("LOCATION: " + i);

            // Check if angle is approx. the same
            boolean is_similar = false;

            // Run backwards through every trajectory
            for (int k = 1; k < traj_length; k++) {
                is_similar = false;
                System.out.println("traj: " + k);
                // Get angle of two locations
                Location3D curr_loc = data.pastTracks[i-k].to3D();
                Location3D pre_loc  = data.pastTracks[i-k-1].to3D();
                double alpha = curr_loc.getAngle(pre_loc);

                if ( Math.abs( alpha - (double) angles.get(k-1) ) < eps ) {
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

        Debug d = new Debug();
        d.print(possible_indices, "Indices");


        // TODO What happens with the vectors

        return null;
    }
}

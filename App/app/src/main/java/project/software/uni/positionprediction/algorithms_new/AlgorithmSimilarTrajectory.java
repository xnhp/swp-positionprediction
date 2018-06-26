package project.software.uni.positionprediction.algorithms_new;


import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import project.software.uni.positionprediction.datatypes_new.PredictionUserParameters;
import project.software.uni.positionprediction.datatypes_new.Collection;
import project.software.uni.positionprediction.datatypes_new.Location;
import project.software.uni.positionprediction.datatypes_new.PredictionBaseData;
import project.software.uni.positionprediction.datatypes_new.PredictionResultData;
import project.software.uni.positionprediction.datatypes_new.Trajectory;
import project.software.uni.positionprediction.algorithms_new.PredictionAlgorithmReturnsTrajectories;


public class AlgorithmSimilarTrajectory extends PredictionAlgorithmReturnsTrajectories {

    public AlgorithmSimilarTrajectory() {
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
    public PredictionResultData predict(PredictionUserParameters algParams, PredictionBaseData data) {


        // Length of trajectory
        int traj_length = 5;
        int pred_traj_length = 5;



        // Algorithm
        int size = data.getTrajectory().size();

        double eps = 1E-5;

        // Get the trajectory (list of angles) you want to compare other trajectories
        List<Number> delta_angles = new LinkedList<Number>();

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

        // Find possible other trajectories and add the index of the last point to list
        List<Number> possible_indices = new LinkedList<Number>();

        // Run through all locations (or endpoint of all trajectories)
        for (int i = traj_length + 1; i < size; i++) {
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
                // Get angle of two locations
                Location loc1 = data.getTrajectory().getLocation(i - k).to3D();
                Location loc2 = data.getTrajectory().getLocation(i - k - 1).to3D();
                Location vector = loc1.subtract(loc2);
                double beta = vector.getAngle(mth_vector);
                System.out.println("beta = " + beta);

                if (Math.abs(beta - (double) delta_angles.get(k - 1)) < eps) {
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


        Collection<Trajectory> trajectories = new Collection<>();

        Log.i("algorithm", "possible_indices size: " + possible_indices.size());
        for (int l = 0; l < possible_indices.size(); l++) {

            Trajectory trajectory = new Trajectory();
            Location new_loc = n0;
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
                new_loc = new_loc.add(new_vector);

                // Add new locations to current trajectory
                trajectory.addLocation(new_loc);

            }
            // Add trajectory to List
            Log.i("algorithm", "size of trajectory: " + trajectory.size());
            trajectories.add(trajectory);
        }

        Log.i("algorithm", "size of trajectories: " + trajectories.size());
        return createResultData(trajectories);
        }
    }



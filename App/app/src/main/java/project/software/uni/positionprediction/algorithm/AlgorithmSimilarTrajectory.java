package project.software.uni.positionprediction.algorithm;

import android.content.Context;
import android.location.Location;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import project.software.uni.positionprediction.datatype.BirdData;
import project.software.uni.positionprediction.datatype.Debug;
import project.software.uni.positionprediction.datatype.Location3D;
import project.software.uni.positionprediction.datatype.Location2D;
import project.software.uni.positionprediction.datatype.TrackingPoint;
import project.software.uni.positionprediction.interfaces.PredictionAlgorithm;
import project.software.uni.positionprediction.movebank.SQLDatabase;

public class AlgorithmSimilarTrajectory implements PredictionAlgorithm {

    private Context context;

    public AlgorithmSimilarTrajectory ( Context context ) {
        this.context = context;
    }


    @Override
    public LinkedList<Location3D> predict(Date date_past, Date date_pred, int study_id, int bird_id) {



        // Fetch data from database
        SQLDatabase db = SQLDatabase.getInstance(context);
        BirdData birddata = db.getBirdData(study_id, bird_id);
        TrackingPoint data[] = birddata.getTrackingPoints();

        // Length of trajectory
        int traj_length = 5;





        // Algorithm
        int size = data.length;

        double eps = 1E-5;

        // Get the trajectory (list of angles) you want to compare other trajectories
        List<Number> angles = new LinkedList<Number>();
        for (int j = 1; j <= traj_length; j++) {
            Location3D curr_loc = data[size-j].getLocation().to3D();
            Location3D pre_loc  = data[size-j-1].getLocation().to3D();
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
                Location3D curr_loc = data[i-k].getLocation().to3D();
                Location3D pre_loc  = data[i-k-1].getLocation().to3D();
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

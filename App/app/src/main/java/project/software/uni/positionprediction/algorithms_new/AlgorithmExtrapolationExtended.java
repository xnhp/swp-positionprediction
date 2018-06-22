package project.software.uni.positionprediction.algorithms_new;

import android.util.Log;

import project.software.uni.positionprediction.datatypes_new.EShape;
import project.software.uni.positionprediction.datatypes_new.PredictionUserParameters;
import project.software.uni.positionprediction.datatypes_new.Collection;
import project.software.uni.positionprediction.datatypes_new.Location;
import project.software.uni.positionprediction.datatypes_new.Locations;
import project.software.uni.positionprediction.datatypes_new.PredictionBaseData;
import project.software.uni.positionprediction.datatypes_new.PredictionResultData;
import project.software.uni.positionprediction.datatypes_new.Trajectory;
import project.software.uni.positionprediction.algorithms_new.PredictionAlgorithmReturnsTrajectory;

public class AlgorithmExtrapolationExtended extends PredictionAlgorithmReturnsTrajectory {


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

        // TODO: Get UserParamSettings
        int pastDataPoints = 50;

        // Compute prediction
        Locations prediction = next_Location(data.getTrajectory().getLocations(), pastDataPoints);

        Trajectory trajectory = new Trajectory(prediction);

        Log.i("algorithm", "trajecory locations: " + (trajectory.getLocations().size()));

        // Create correct result type
        PredictionResultData result = createResultData(trajectory);

        Log.i("algorithm", "result size: " + (result.getShapes().get(EShape.TRAJECTORY).size()/*get(0).getLocations().get(0).toString()*/));

        return result;
    }


    /**
     * Computes the average differences in long. and lat. and adds it to the last position
     * [Average (last 2 Locations, Average last 3 Locations,...)]
     *
     * @param data
     * @return
     */
    private Locations next_Location(Locations data, int date) {
        int n = data.size() - 1;
        Collection<Location> vector_collection = new Collection<>();
        // Fill collection
        for (int t = 1; t < date; t++) { // todo: loop conditions correct?
            // Compute difference of pair n and n-t
            // Get n-th point
            Location vec_n = data.get(n);

            // Get n-t point
            Location vec_old = data.get(n-t);

            // Compute vector between them
            Location vec_delta = vec_n.subtract(vec_old);

            // Compute average
            Location vec_avg = vec_delta.divide(t);

            // Add vector to collection
            //vector_collection.set(t - 1, vec_avg);
            vector_collection.add(vec_avg);
        }

        // Compute average of all computed vectors in collection
        Location avg = weighted_average(vector_collection);
        Location curr_loc = data.get(data.size() - 1);

        // Add avg vector to current Location
        Locations result_list = new Locations();
        // todo: returns locations with hasAltitude = true. not good.
        Location result_vector = curr_loc.to3D().add(avg);
        result_list.add(result_vector);
        return result_list;
    }


    /**
     * Computes (weighted) average of Location vectors
     *
     * @param collection
     * @return
     */
    private Location weighted_average(Collection<Location> collection) {
        double sum_long = 0;
        double sum_lat = 0;
        double sum_height = 0;

        // Compute sum
        for (int i = 0; i < collection.size(); i++) {

            if (collection.get(i).hasAltitude()) {
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

        return new Location(res_long, res_lat, res_height);
    }


    /**
     * Todo: possible weight function
     *
     * @param a
     * @return
     */
    private int weight(int a) {
        return 1;
    }

}

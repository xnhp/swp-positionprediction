package project.software.uni.positionprediction.controllers;

import java.util.ArrayList;
import java.util.List;

import project.software.uni.positionprediction.algorithm.PredictionParameters;
import project.software.uni.positionprediction.datatype.Bird;
import project.software.uni.positionprediction.datatype.Location3D;
import project.software.uni.positionprediction.interfaces.SingleTrajPredictionAlgorithm;
import project.software.uni.positionprediction.osm.OSMDroidVisualisationAdapter;
import project.software.uni.positionprediction.visualisation.IVisualisationAdapter;
import project.software.uni.positionprediction.visualisation.SingleTrajectoryVis;
import project.software.uni.positionprediction.visualisation.Visualisation;

/**
 * This class coordinates between
 *
 * data fetching -> prediction calculation -> result visualisation -> visualisation drawing
 *
 * The PredictionWorkflowController itself doesnt have state. All methods should be static.
 */
public class PredictionWorkflowController {

    /**
     * TODO: we might already have downloaded bird data at this point?
     *
     * We receive the visualisation adapter from outside because it needs to be linked to a map.
     * That linking should be done close to where that map is handled/created (e.g. in the Activity).
     */
    public static void doSingleTrajPrediction(
            IVisualisationAdapter visAdapter,
            SingleTrajPredictionAlgorithm algorithm,
            PredictionParameters algParams
    ) {

       /*
        1.) fetch data    (what? need params)
                or make sure that data is there?
        2.) run pred alg
        3.) build vis
        4.) draw vis      (where? need params)
         */

        ArrayList<Location3D> testPosLoc = new ArrayList<>();
        testPosLoc.add(new Location3D(47.680503, 9.177198, 10));
        testPosLoc.add(new Location3D(47.679463, 9.179558, 10));
        testPosLoc.add(new Location3D(47.678871, 9.181532, 10));

        // run prediction
        List<Location3D> prediction = algorithm.predict(algParams.date_past, algParams.date_pred, algParams.bird.getId());

        // think about how to visualise it
        SingleTrajectoryVis myVis = new SingleTrajectoryVis();
        myVis.traj = testPosLoc;
        // myVis.traj = prediction
        myVis.pointColor = "#ff0077"; // pink
        myVis.lineColor = "#00ff88";  // bright green

        // do visualise it
        visAdapter.visualiseSingleTraj(myVis);
        // adapter is obtained outside of here. (see class comments)
        // thanks to the interface, we can write it this generally here, no matter if osm or cesium
    }


}

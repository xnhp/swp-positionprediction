package project.software.uni.positionprediction.controllers;

import project.software.uni.positionprediction.visualisation.IVisualisation;
import project.software.uni.positionprediction.visualisation.Visualisation;

/**
 * This class coordinates between
 *
 * data fetching -> prediction calculation -> result visualisation -> visualisation drawing
 */
public class PredictionWorkflowController {


    /**
     *
     * @param studyId Bird identification
     * @param indivId Bird identification
     * @param visAdapter means to draw visualisation somewhere (this already has information about the map)
     */
    public void displaySingleTrajPrediction(int studyId, int indivId, IVisualisation visAdapter) {



    /*
    1.) fetch data    (what? need params)
    2.) run pred alg
    3.) build vis
    4.) draw vis      (where? need params)
     */

        /*
        ArrayList<Location> testPosLoc = new ArrayList<>();
        testPosLoc.add(new Location(47.680503, 9.177198));
        testPosLoc.add(new Location(47.679463, 9.179558));
        testPosLoc.add(new Location(47.678871, 9.181532));
        // we would receive a visualisation object as output from a prediction algorithm
        SingleTrajectoryVis myVis = new SingleTrajectoryVis();
        myVis.traj = testPosGp;
        myVis.pointColor = "#ff0077"; // pink
        myVis.lineColor = "#00ff88";  // bright green


        // obtain an adapter
        OSMDroidVisualisationAdapter myVisAdap = new OSMDroidVisualisationAdapter();
        // set the map for the adapter
        myVisAdap.linkMap(mymap);
        // have it draw the visualisation
        myVisAdap.visualiseSingleTraj(myVis);
        */
    }

}

package project.software.uni.positionprediction.controllers;

import android.content.Context;
import android.util.Log;

import project.software.uni.positionprediction.datatypes_new.PredictionUserParameters;
import project.software.uni.positionprediction.datatypes_new.BirdData;
import project.software.uni.positionprediction.datatypes_new.Cloud;
import project.software.uni.positionprediction.datatypes_new.Collection;
import project.software.uni.positionprediction.datatypes_new.Collections;
import project.software.uni.positionprediction.datatypes_new.Locations;
import project.software.uni.positionprediction.datatypes_new.PredictionBaseData;
import project.software.uni.positionprediction.datatypes_new.PredictionResultData;
import project.software.uni.positionprediction.datatypes_new.Shape;
import project.software.uni.positionprediction.datatypes_new.Trajectory;
import project.software.uni.positionprediction.movebank.SQLDatabase;
import project.software.uni.positionprediction.datatypes_new.EShape;
import project.software.uni.positionprediction.visualisation_new.CloudVis;
import project.software.uni.positionprediction.visualisation_new.Funnel;
import project.software.uni.positionprediction.visualisation_new.IVisualisationAdapter;
import project.software.uni.positionprediction.visualisation_new.PastTrajectoryStyle;
import project.software.uni.positionprediction.visualisation_new.Polyline;
import project.software.uni.positionprediction.visualisation_new.PredTrajectoryStyle;
import project.software.uni.positionprediction.visualisation_new.TrajectoryVis;
import project.software.uni.positionprediction.visualisation_new.Visualisations;

/** Workflow:
*
  * 1.) On Bird select, `trigger` is called.
*   2.) Data is fetched from the internal DB
*   3.) Prediction is made
*   4.) Visualisation objects (past and prediction) are built
*   5.) Vis. objects are saved in static fields.
*
* Then, the map activities (OSM, Cesium) will fetch the
* Visualisation objects from these static fields and hand them
* (and the respective VisualisationAdapter) to the
* `VisualisationWorkflow` controller.
*
* This is because in order to be able to display a visualisation,
* the activity has to already be up and running (map view instanced
* etc). An alternative approach would have been to hand the
* visualisation data to the activity, however that would have been
* more complicated since we will be switching back and forth between
* OSM/Cesium too.
 */
public class PredictionWorkflow extends Controller {

    // ---------------
    // CLASS VARIABLES
    // ---------------

    public static PredictionBaseData data_past;
    private PredictionUserParameters userParams;
    private IVisualisationAdapter visAdapter;
    public static Visualisations vis_pred;
    public static TrajectoryVis vis_past;



    public PredictionWorkflow(
            Context context,
            PredictionUserParameters userParams,
            IVisualisationAdapter visAdapter
    ) {
        super(context);
        this.userParams = userParams;
        this.visAdapter = visAdapter;
        //this.algorithm = predictionUserParameters.algorithm;
        // this will be taken from the Settings instead
    }


    public void trigger() {

        // work in sepearate thread to not block UI
        new Thread(new Runnable() {
            @Override
            public void run() {

                // get tracking points from the movebank api
                // TODO: indicate activity / progress to user
                // TODO: try/catch RequestFailedException
                requestData();

                try {
                    // fetch data (tracking points) from the local database
                    data_past = fetchData();
                    Log.i("prediction workflow", "data_past locs size: " + data_past.getTrajectory().getLocations().size());
                } catch (InsufficientTrackingDataException e) {
                    // TODO: throwing exceptions in threads is a bit tricky
                    // cant catch this "from outside"
                }


                // TODO: throw (algorithm classes)
                // TODO: try/catch
                try {
                    final PredictionResultData data_pred = userParams.algorithm.predict(userParams, data_past);
                    Log.i("prediction workflow", "data_pred shapes size: " + (data_pred.getShapes().size()));
                    Log.i("prediction workflow", "data_pred keys: " + (data_pred.getShapes().keySet().toString()));

                    // simply build a single traj vis
                    // for the past tracking data
                    vis_past = buildSingleTrajectoryVis(
                            data_past.getTrajectory(),
                            PastTrajectoryStyle.pointCol,
                            PastTrajectoryStyle.lineCol,
                            PastTrajectoryStyle.pointRadius
                    );
                    // `buildVisualizations` builds a visualisation
                    // possibly composed of smaller visualisations
                    // e.g. multiple trajectories
                    // `shapes` is a collection of "smaller visualisations"
                    vis_pred = buildVisualizations(data_pred.getShapes());

                    // these two visualisations are saved in static fields and
                    // then accessed by the map activities


                    // TODO: this will be located in the activity
                    // accessing the prediction results via static fields
                    VisualizationWorkflow visWorkflow = new VisualizationWorkflow(
                            context,
                            visAdapter,
                            vis_past,
                            vis_pred);
                    visWorkflow.trigger();


                } catch (NullPointerException e) {
                    e.printStackTrace();
                }





            }
        }).start();
    }


    // Request data update from Movebank
    // TODO: throw RequestFailedException
    private void requestData() /*throws RequestFailedException*/ {
        // Make an async network request for new data
        Log.i("OSM_new", "userParams.bird is null: " + (userParams.bird == null));
        SQLDatabase.getInstance(context)
                .updateBirdDataSync(userParams.bird.getStudyId(), userParams.bird.getId());
    }


   /**
   * Prepare any data that will be used for the prediction.
   * The returned object will be handed to the predicition algorithm
   * as the sole ressource.
   *
   * in the future, might access different sources of information here (e.g. current weather)
     and save that in PredictionBaseDate to be used for a position prediction
    */
    private PredictionBaseData fetchData() throws InsufficientTrackingDataException {
        // TODO: do this based on date
        // todo: link this to hardcoded limit in algorithm
        int pastDataPoints = 50; // Use last 10 data points

        SQLDatabase db = SQLDatabase.getInstance(context);
        BirdData birddata = db.getBirdData(
                userParams.bird.getStudyId(),
                userParams.bird.getId());
        Locations tracks = birddata.getTrackingPoints();

        // Check data
        if (tracks.size() == 0) {
            Log.e("Error", "Size of data is 0");
            throw new InsufficientTrackingDataException("Size of data is 0");
        }

        // Use only needed data
        // todo: do this via SQL request?

        Trajectory pastTracks = new Trajectory();
        int size = tracks.size();
        for (int i = 0; i < pastDataPoints; i++) {
            // get the i last points
            pastTracks.addLocation(tracks.get(tracks.size() - 1 - pastDataPoints + i));
        }

        PredictionBaseData data = new PredictionBaseData(pastTracks);

        return data;
    }


    private Visualisations buildVisualizations(Collections<EShape, ? extends Shape> pred) {
        Visualisations result = new Visualisations();
        for (Collection<? extends Shape> type : pred.values()) {
            int counter = 1;
            for (Shape locs : type) {
                if (locs instanceof Trajectory) {
                    Log.i("prediction workflow", "Is instance of Trajectory.");
                    result.add(
                            buildSingleTrajectoryVis(
                                    (Trajectory) locs,
                                    PredTrajectoryStyle.lineCol,
                                    PredTrajectoryStyle.pointCol,
                                    PredTrajectoryStyle.pointRadius)
                    );
                } else if (locs instanceof Cloud) {
                    result.add(buildSingleCloudVis((Cloud) locs, counter));
                }
                counter++;
            }
        }
        return result;
    }

    private CloudVis buildSingleCloudVis(Cloud locs, int counter) {
        return null;
    }


    /**
     * Build Geometry object based on pre-defined visual properties and draw it
     * We can write it this generically here, no matter if osm or cesium
     */
    private TrajectoryVis buildSingleTrajectoryVis(
            Trajectory traj,
            String pointColor,
            String lineColor,
            int pointRadius
    ) {

        // Correct would be: build TrajectoryVis which consists of a polyline (among others)
        // Build Visualizations
        Polyline line = new Polyline(
                traj.getLocations(),
                pointColor,
                lineColor,
                pointRadius
        );

        if(traj.hasFunnel()){
            Locations coords = traj.calculateFunnelCoords();
            Funnel funnel = new Funnel(coords);
        }

        return new TrajectoryVis(line);

    }


}

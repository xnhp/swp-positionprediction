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
import project.software.uni.positionprediction.visualisation_new.Polyline;
import project.software.uni.positionprediction.visualisation_new.StyleTrajectory;
import project.software.uni.positionprediction.visualisation_new.TrajectoryVis;
import project.software.uni.positionprediction.visualisation_new.Visualisations;

/** Workflow:
 * 1.) request data from Movebank and update internal DB
 * 2.) fetch data from internal DB
 * 3.) run prediction
 * 4.) build visualization objects
 * 5.) depending on indoor/outdoor-mode: open activity OSM or Cesium
 *     which triggers visualization workflow with suitable visualizationAdapter
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
    }


    public void trigger() {
        // TODO: avoid making too many requests.
        // check whether a request was made in the last n seconds?
        new Thread(new Runnable() {
            @Override
            public void run() {

                // TODO: indicate activity / progress to user
                // TODO: try/catch RequestFailedException
                requestData();

                try {
                    data_past = fetchData();
                    Log.i("prediction workflow", "data_past locs size: " + data_past.getTrajectory().getLocations().size());
                } catch (InsufficientTrackingDataException e) {
                    // TODO: throwing exceptions in threads is a bit tricky
                    // cant catch this "from outside"
                }


                // TODO: throw (algorithm classes)
                // TODO: try/catch
                final PredictionResultData data_pred = userParams.algorithm.predict(userParams, data_past);
                Log.i("prediction workflow", "data_pred shapes size: " + (data_pred.getShapes().size()));
                Log.i("prediction workflow", "data_pred keys: " + (data_pred.getShapes().keySet().toString()));
                //Log.i("prediction workflow", "data_pred trajectories size: " + (data_pred.getShapes().get(EShape.TRAJECTORY).size()));

                //new Handler(Looper.getMainLooper()).post(new Runnable() {

                    //public void run() {
                        vis_past = buildSingleTrajectoryVis(data_past.getTrajectory(), 0);
                        vis_pred = buildVisualizations(data_pred.getShapes());
                    //}
                //});

                Log.i("prediction workflow", "vis_pred size: " + (vis_pred.size()));

                VisualizationWorkflow visWorkflow = new VisualizationWorkflow(
                        context,
                        visAdapter,
                        vis_past,
                        vis_pred);
                visWorkflow.trigger();

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


    // Fetch data from internal database
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
            //loc_data[i] = tracks[size - 1 - pastDataPoints + i].getLocation().to3D();
            // todo: What is going on here?
            pastTracks.addLocation(tracks.get(tracks.size() - 1 - pastDataPoints + i));
        }

        PredictionBaseData data = new PredictionBaseData(pastTracks);

        return data;
        // in the future, might access different sources of information here (e.g. current weather)
        // and save that in PredictionBaseDate to be used for a position prediction
    }


    private Visualisations buildVisualizations(Collections<EShape, ? extends Shape> pred) {
        Visualisations result = new Visualisations();
        for (Collection<? extends Shape> type : pred.values()) {
            int counter = 1;
            for (Shape locs : type) {
                if (locs instanceof Trajectory) {
                    Log.i("prediction worlflow", "Is instance of Trajectory.");
                    result.add(buildSingleTrajectoryVis((Trajectory) locs, counter));
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
    private TrajectoryVis buildSingleTrajectoryVis(Trajectory traj, int counter) {

        // Correct would be: build TrajectoryVis which consists of a polyline (among others)
        // Build Visualizations
        Polyline line = new Polyline(
                traj.getLocations(),
                StyleTrajectory.pastPointCol.asString(),
                StyleTrajectory.pastLineCol.asString(),
                StyleTrajectory.pastPointRadius.asInt()
        );

        if(traj.hasFunnel()){
            Locations coords = traj.calculateFunnelCoords();
            Funnel funnel = new Funnel(coords);
        }

        return new TrajectoryVis(line);

    }


}

package project.software.uni.positionprediction.controllers;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.Log;


import java.util.Calendar;
import org.osmdroid.util.BoundingBox;
import java.util.Date;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.activities.BirdSelect;
import project.software.uni.positionprediction.algorithms_new.PredictionAlgorithm;
import project.software.uni.positionprediction.datatypes_new.Bird;
import project.software.uni.positionprediction.datatypes_new.Location;
import project.software.uni.positionprediction.datatypes_new.LocationWithValue;
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
import project.software.uni.positionprediction.osm.OSMCacheControl;
import project.software.uni.positionprediction.util.BearingProvider;
import project.software.uni.positionprediction.util.LoadingIndicator;
import project.software.uni.positionprediction.util.Message;
import project.software.uni.positionprediction.util.XML;
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
    public static Bird bird;
    private static XML xml = new XML();

    private boolean refreshNeeded;

    private static PredictionWorkflow predictionWorkflowSingleton;



    private PredictionWorkflow(
            Context context
    ) {
        super(context);
    }

    public static PredictionWorkflow getInstance(Context c){
        if (predictionWorkflowSingleton == null) {
            predictionWorkflowSingleton = new PredictionWorkflow(c);
            return predictionWorkflowSingleton;
        }
        PredictionWorkflow.predictionWorkflowSingleton.context = c;
        return predictionWorkflowSingleton;
    }


    public void updateUserParams(){
        this.userParams = getPredictionUserParameters();
    }


    public static PredictionUserParameters getPredictionUserParameters() {

        int hoursInPast = xml.getHours_past();

        // If used all data is clicked
        Date date_past;

        if (hoursInPast == -1){
            Log.d("Controller", "All data will be used by algorithm");
            date_past = new Date(0);
            Log.e("date_past all data", "" + date_past.toString());


            // If an hour is set in settings
        } else {
            Calendar clp = Calendar.getInstance();
            clp.setTime(new Date());
            clp.add(Calendar.HOUR, -hoursInPast);
            date_past = clp.getTime();
        }


        // for what point in the future we want the prediction
        // hardcoded: 5 hours from current datetime
        int hoursInFuture = xml.getHours_fut();
        Calendar clf = Calendar.getInstance();
        clf.setTime(new Date());
        clf.add(Calendar.HOUR, hoursInFuture);
        Date date_pred = clf.getTime();


        // for debug purposes

        // Catch prediction algorithm which is null
        PredictionAlgorithm alg = xml.getPredictionAlgorithm(BirdSelect.getCtx());
        if (alg == null || date_past == null || date_pred == null || bird == null) {
            return null;

        } else {
            return new PredictionUserParameters(
                    alg,
                    date_past,
                    date_pred,
                    bird
            );
        }
    }

    public void make_prediction(Context c){

        if(predictionWorkflowSingleton != null) {
            Log.d("Prediction" ,"makes Prediction");
            predictionWorkflowSingleton.trigger();
        } else {
            Log.e("Error", "predWorkFlow == null! No Prediction is made!");
        }
    }


    /*
     * Because making a prediction requires a valid context and
     * is asynchroneous, triggering the recalculation with
     * the context of Settings Activity on click
     * of "Save" button in settings and immediately finishing
     * the activity afterwards invalidates the context.
     *
     * Possible solutions:
     *
     * - we remember that a recalculation is needed
     *   and trigger that when coming back to an activity
     *   (currently implemented)
     *
     * - we implement a callback function that PredWfCtrl
     *   calls when it is done - *then*, the Settings
     *   activity is finished.
     *
     */
    public boolean isRefreshNeeded(){
        return refreshNeeded;
    }

    public void requestRefresh(){
        refreshNeeded = true;
    }

    /**
     * This Method refreshes the Prediction
     */
    public void refreshPrediction(Context c){

        refreshNeeded = false;

        make_prediction(c);

        Log.d("Prediction" ,"gets refreshed");
        if(predictionWorkflowSingleton != null) {
            PredictionUserParameters userParams = getPredictionUserParameters();
            if (userParams == null) {
                Log.e("Error", "User Params are null");
            } else {
                predictionWorkflowSingleton.setUserParams(userParams);
                predictionWorkflowSingleton.trigger();
            }

        } else {
            Log.e("Error", "predWorkFlow == null! No Prediction is made with refreshing!");
        }
    }


    public void trigger() {

        // work in sepearate thread to not block UI
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {

                /*
                    get tracking points from the movebank api
                    and save in local db
                 */
                // show LoadingIndicator
                LoadingIndicator.getInstance().show(context);

                // TODO: try/catch RequestFailedException
                requestData();


                /*
                    fetch data from local database
                 */
                try {
                    // fetch data (tracking points) from the local database
                    data_past = fetchData();
                    Log.i("prediction workflow", "data_past locs size: " + data_past.getTrajectory().getLocations().size());
                } catch (InsufficientTrackingDataException e) {
                    // TODO: throwing exceptions in threads is a bit tricky
                    // cant catch this "from outside"
                }


                /*
                    Run Prediction Algo, build Visualisations,
                    trigger download of maps.
                 */
                // TODO: throw (algorithm classes)
                // TODO: try/catch
                try {
                    final PredictionResultData data_pred = userParams.algorithm.predict(userParams, data_past);

                    Log.i("prediction workflow", "data_pred shapes size: " + (data_pred.getShapes().size()));
                    Log.i("prediction workflow", "data_pred keys: " + (data_pred.getShapes().keySet().toString()));


                    if ( data_pred == null || data_pred.getShapes().size() == 0){
                        Log.e("Warning", "No data to visualize");
                    } else {
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
                    }


                    /*
                        Trigger download of maps
                     */
                    BoundingBox visBBPast = PredictionWorkflow.vis_past.getBoundingBox();
                    BoundingBox visBBPred = PredictionWorkflow.vis_pred.getBoundingBox();
                    BoundingBox visBB = visBBPast.concat(visBBPred);
                    // this might not be working due to chaotic handling and saving of
                    // context references
                    // OSMCacheControl.getInstance(context).saveAreaToCache(visBB);

                     /*
                        Set target location for "Compass
                     */
                    android.location.Location targetLocation = new android.location.Location("PredWf");
                    targetLocation.setLatitude(visBBPred.getCenterLatitude());
                    targetLocation.setLongitude(visBBPred.getCenterLongitude());
                    BearingProvider.getInstance().setTargetLocation(targetLocation);


                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                // hide LoadingIndicator
                LoadingIndicator.getInstance().hide();


            }
        }).start();
    }


    // Request data update from Movebank
    // TODO: throw RequestFailedException
    private void requestData() /*throws RequestFailedException*/ {
        // Make an async network request for new data
        Log.i("OSM_new", "userParams.bird is null: " + (userParams.bird == null));
        final int percentag_bad_data = (int) (SQLDatabase.getInstance(context)
                .updateBirdDataSync(userParams.bird.getStudyId(), userParams.bird.getId()) * 100.0f);

        // TODO: fix error message

        new Handler(Looper.getMainLooper()).post(
                new Runnable() {
                    @Override
                    public void run() {
                        if(percentag_bad_data >=
                                context.getResources().getInteger(R.integer.percentage_bad_data_warning)) {
                            Message.disp_error(context,
                                    context.getResources().getString(R.string.percentage_bad_date_warning),
                                    context.getResources().getString(R.string.percentage_bad_data_warning_dialog,
                                            percentag_bad_data));
                        }
                    }
                });
    }


    /**
    * Prepare any data that will be used for the prediction.
    * The returned object will be handed to the predicition algorithm
    * as the sole ressource.
    *
    * in the future, might access different sources of information here (e.g. current weather)
    * and save that in PredictionBaseDate to be used for a position prediction
    */
    private PredictionBaseData fetchData() throws InsufficientTrackingDataException {

        // TODO: link this to hardcoded limit in algorithm

        SQLDatabase db = SQLDatabase.getInstance(context);
        BirdData birddata = db.getBirdData(
                userParams.bird.getStudyId(),
                userParams.bird.getId(),
                userParams.date_past);
        Locations tracks = birddata.getTrackingPoints();

        // Check data
        if (tracks.size() == 0) {
            Log.e("Error", "Size of data is 0");
            throw new InsufficientTrackingDataException("Size of data is 0");
        }


        Trajectory pastTracks = new Trajectory();
        int size = tracks.size();
        for (int i = 0; i < size; i++) {
            // get the i last points
            pastTracks.addLocation(tracks.get(size - 1 - + i));
        }

        Location lastLocaation = tracks.get(size -1);
        if(lastLocaation instanceof LocationWithValue){
            LocationWithValue lastLocationWithValue = (LocationWithValue) lastLocaation;
            if(lastLocationWithValue.getValue() instanceof  Date){

                final Date date = (Date) lastLocationWithValue.getValue();

                Date now = new Date();
                // check if latest Location is older than a week
                if(now.getTime() - date.getTime() >
                        context.getResources().getInteger(R.integer.old_data_warning_time_difference)){

                    new Handler(Looper.getMainLooper()).post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    Message.disp_error(context,
                                            context.getResources().getString(R.string.old_data_warning),
                                            context.getResources().getString(
                                                    R.string.old_data_warning_dialog,
                                                    date.toString()));
                                }
                            });
                }
            }
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

    public void setUserParams(PredictionUserParameters userParams){
        this.userParams = userParams;
    }

    public static Bird getBird() {
        return bird;
    }

    public static void setBird(Bird bird) {
        PredictionWorkflow.bird = bird;
    }
}

package project.software.uni.positionprediction.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.osmdroid.util.BoundingBox;

import java.util.Calendar;
import java.util.Date;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.activities.BirdSelect;
import project.software.uni.positionprediction.algorithms.PredictionAlgorithm;
import project.software.uni.positionprediction.datatypes.Bird;
import project.software.uni.positionprediction.datatypes.BirdData;
import project.software.uni.positionprediction.datatypes.Cloud;
import project.software.uni.positionprediction.datatypes.Collection;
import project.software.uni.positionprediction.datatypes.Collections;
import project.software.uni.positionprediction.datatypes.EShape;
import project.software.uni.positionprediction.datatypes.Locations;
import project.software.uni.positionprediction.datatypes.PredictionBaseData;
import project.software.uni.positionprediction.datatypes.PredictionResultData;
import project.software.uni.positionprediction.datatypes.PredictionUserParameters;
import project.software.uni.positionprediction.datatypes.Shape;
import project.software.uni.positionprediction.datatypes.Trajectory;
import project.software.uni.positionprediction.movebank.SQLDatabase;
import project.software.uni.positionprediction.osm.OSMCacheControl;
import project.software.uni.positionprediction.util.AsyncTaskCallback;
import project.software.uni.positionprediction.util.BearingProvider;
import project.software.uni.positionprediction.util.LoadingIndicator;
import project.software.uni.positionprediction.util.Message;
import project.software.uni.positionprediction.util.XML;
import project.software.uni.positionprediction.visualisation.CloudVis;
import project.software.uni.positionprediction.visualisation.Funnel;
import project.software.uni.positionprediction.visualisation.PastTrajectoryStyle;
import project.software.uni.positionprediction.visualisation.Points;
import project.software.uni.positionprediction.visualisation.Polyline;
import project.software.uni.positionprediction.visualisation.PredCloudStyle;
import project.software.uni.positionprediction.visualisation.PredTrajectoryStyle;
import project.software.uni.positionprediction.visualisation.TrajectoryVis;
import project.software.uni.positionprediction.visualisation.Visualisations;

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
            Log.d("date_past all data", "" + date_past.toString());


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
    public void refreshPrediction(Context c, AsyncTaskCallback callback){
        context = c;
        refreshNeeded = false;

        Log.d("Prediction" ,"gets refreshed");
        if(predictionWorkflowSingleton != null) {
            PredictionUserParameters userParams = getPredictionUserParameters();
            if (userParams == null) {
                Log.e("Error", "User Params are null");
            } else {
                predictionWorkflowSingleton.setUserParams(userParams);
                predictionWorkflowSingleton.trigger(c, callback);
            }

        } else {
            Log.e("Error", "predWorkFlow == null! No Prediction is made with refreshing!");
        }
    }

    public void refreshPrediction(Context c) {
        refreshPrediction(c, null);
    }


    public void trigger(Context c, AsyncTaskCallback callback) {
        LoadingIndicator.getInstance().show(c);
        new makePredictionTask().execute(callback);

    }

    /**
     * We run the calculation of a prediction
     * (fetch data from network into db, read from db, run alg)
     * in a seperate thread in order to avoid blocking the main thread.
     *
     * cf https://developer.android.com/reference/android/os/AsyncTask
     */
    private class makePredictionTask extends AsyncTask<AsyncTaskCallback, Void, Void> {

        AsyncTaskCallback callback;

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(AsyncTaskCallback... asyncTaskCallbacks) {

            // save callback for reference in onPostExecute
            if (asyncTaskCallbacks.length > 0) callback = asyncTaskCallbacks[0];


            /*
                get tracking points from the movebank api
                and save in local db
             */
            try {
                requestData();
            } catch (Exception e) {
                // InsufficientDataException, RequestFailedException...
                // call callback with exception so we can handle them
                // outside of here
                this.callback.onException(e);
            }


            /*
                fetch data from local database
             */
            try {
                data_past = fetchData();
                Log.i("prediction workflow", "data_past locs size: " + data_past.getTrajectory().getLocations().size());
            } catch (InsufficientTrackingDataException e) {
                // no past data, hence no result
                data_past = null;
                vis_past = null;
                vis_pred = null;
                return null;
            }

            /*
                check if data is recent enough
             */
            Date mostRecent = data_past.getTrajectory().getLocations().getMostRecentDate();
            if ( isDataOld( mostRecent )) {
                callback.onWarning(
                        context.getResources().getString(R.string.old_data_warning),
                        context.getResources().getString(
                                R.string.old_data_warning_dialog,
                                mostRecent.toString())
                );
            }


            /*
                Run Prediction Algo
             */
            PredictionAlgorithm algorithm = userParams.algorithm;
            algorithm.setContext(context);
            final PredictionResultData data_pred = algorithm.predict(userParams, data_past);




            // Visualize old data, but not the prediction if null (*)

            /*
                Assemble Visualisations
             */
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


            // This have to be after the visualization of the old data
            if (data_pred == null || data_pred.getShapes().size() == 0) {
                PredictionWorkflow.vis_pred = null;
                Log.e("Warning", "No prediction to visualize");
                Message.disp_error_asynch(context, "Warnung", "" +
                        "Eine Vorhersage konnte NICHT gemacht werden!");
                return null;

            }


            vis_pred = buildVisualizations(data_pred.getShapes());

            // these two visualisations are saved in static fields and
            // then accessed by the map activities



            /*
                Trigger download of maps
             */
            BoundingBox visBBPast = PredictionWorkflow.vis_past.getBoundingBox();
            BoundingBox visBBPred = PredictionWorkflow.vis_pred.getBoundingBox();
            BoundingBox visBB = visBBPast.concat(visBBPred);
            // this might not be working due to chaotic handling and saving of
            // context references
            OSMCacheControl.getInstance(context).saveAreaToCache(visBB);

            /*
                    Set target location for "Compass"
                */
            // this will at some point affect the ui
            // (the rotation of the compass arrow is updated)
            // so we post this back to the ui thread
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                 @Override
                 public void run() {
                     android.location.Location targetLocation = new android.location.Location("");
                     BoundingBox visBBPred = PredictionWorkflow.vis_pred.getBoundingBox();
                     targetLocation.setLatitude(visBBPred.getCenterLatitude());
                     targetLocation.setLongitude(visBBPred.getCenterLongitude());
                     BearingProvider.getInstance().setTargetLocation(targetLocation);
                 }
             });


            return null; // AsyncTask implementation detail
        }

        /**
         * Called when calculation was executed without exception.
         * @param aVoid
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // we dont pass the resulting visualisation as a parameter to the callback
            // but save it in a static field instead because it will be needed more than there
            // (in OSM as well as in Cesium)
            if (callback != null) callback.onFinish();

            // hide LoadingIndicator
            LoadingIndicator.getInstance().hide();

        }

        /**
         * Called when calculation thread was cancelled.
         * @param aVoid
         */
        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            if (callback != null) callback.onCancel();
        }
    }


    // Request data update from Movebank

    /**
     * Request data from network (movebank)
     * @throws BadDataException if the amount of usable data is too low
     */
    private void requestData() throws BadDataException, RequestFailedException {
        // Make an async network request for new data
        Log.i("OSM", "userParams.bird is null: " + (userParams.bird == null));
        final int percentage_bad_data = (int) (SQLDatabase.getInstance(context)
                .updateBirdDataSync(userParams.bird.getStudyId(), userParams.bird.getId()) * 100.0f);

        if (percentage_bad_data >= context.getResources().getInteger(R.integer.percentage_bad_data_warning)) {
            throw new BadDataException(percentage_bad_data);
        }
    }


    /**
    * Prepare any data that will be used for the prediction.
    * The returned object will be handed to the prediction algorithm
    * as the sole resource.
    *
    * in the future, might access different sources of information here (e.g. current weather)
    * and save that in PredictionBaseData to be used for a position prediction
    */
    private PredictionBaseData fetchData() throws InsufficientTrackingDataException {

        SQLDatabase db = SQLDatabase.getInstance(context);
        BirdData birddata = db.getBirdData(
                userParams.bird.getStudyId(),
                userParams.bird.getId(),
                userParams.date_past);
        Locations tracks = birddata.getTrackingPoints();

        // Check data
        if (tracks.size() == 0) {
            Log.e("Error", "Size of data is 0");
            throw new InsufficientTrackingDataException();
        }

        Trajectory pastTracks = new Trajectory();
        int size = tracks.size();
        for (int i = 0; i < size; i++) {
            // get the i last points
            pastTracks.addLocation(tracks.get(size - 1 - + i));
        }

        return new PredictionBaseData(pastTracks);
    }



    /**
     * Checks if given date is older than a value specified
     * in R.integer.old_data_warning_time_difference
     * @param mostRecent
     * @return
     */
    private boolean isDataOld(Date mostRecent) {
        // check if latest Location is older than a week
        return (
             new Date().getTime() - mostRecent.getTime()
           > context.getResources().getInteger(R.integer.old_data_warning_time_difference)
        );


    }

    /**
     * Assembles a visualisation object out of prediction results
     * @param pred
     * @return
     */
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
                    result.add(
                            buildSingleCloudVis(
                                    (Cloud) locs,
                                    PredCloudStyle.pointCol,
                                    PredCloudStyle.pointRadius)
                    );
                }
                counter++;
            }
        }
        return result;
    }

    private CloudVis buildSingleCloudVis(
            Cloud cloud,
            String pointColor,
            int pointRadius
    ) {
        Points points = new Points(
                cloud.getLocations(),
                pointColor,
                pointRadius
        );

        CloudVis cloudVis = new CloudVis(points);

        if(cloud.hasHull()) {
            cloudVis.setHull(cloud.getHull(),
                    PredCloudStyle.hullLineCol,
                    PredCloudStyle.hullFillCol,
                    PredCloudStyle.hullOpacity);

        }

        return cloudVis;
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


        TrajectoryVis trajVis = new TrajectoryVis(line);

        if(traj.hasFunnel()) {
            Funnel funnel = new Funnel(
                    traj.calculateFunnelCoords(data_past.getLastLocation()),
                    PredTrajectoryStyle.funnelFillCol,
                    PredTrajectoryStyle.funnelLineCol,
                    PredTrajectoryStyle.funnelOpacity
            );
            trajVis.setFunnel(funnel);

        }

        return trajVis;

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

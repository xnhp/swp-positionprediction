package project.software.uni.positionprediction.controllers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import project.software.uni.positionprediction.algorithm.PredictionBaseData;
import project.software.uni.positionprediction.algorithm.PredictionUserParameters;
import project.software.uni.positionprediction.datatype.BirdData;
import project.software.uni.positionprediction.datatype.Locations;
import project.software.uni.positionprediction.datatype.SingleTrajectory;
import project.software.uni.positionprediction.datatype.TrackingPoint;
import project.software.uni.positionprediction.interfaces.PredictionAlgorithm;
import project.software.uni.positionprediction.movebank.SQLDatabase;
import project.software.uni.positionprediction.osm.OSMDroidVisualisationAdapter;
import project.software.uni.positionprediction.visualisation.IVisualisationAdapter;
import project.software.uni.positionprediction.visualisation.SingleTrajectoryVis;
import project.software.uni.positionprediction.visualisation.StyleTrajectory;

/**
 * Call of method "trigger" triggers execution of the following procedures:
 * 1.) Request data update from Movebank (method "request")
 * 2.) Fetch data from internal database (method "fetch")
 * 3.) Run prediction
 * 4.) Visualize (Build Visualisation object based on pre-defined visual properties and draw it)
  * @author Timo, based on PredictionWorkflowContrroller by Benny
 */
public class PredictVisController {


    private final Context ctx;
    private final IVisualisationAdapter visAdapter;
    private final PredictionUserParameters predictionUserParameters;
    private PredictionAlgorithm algorithm;
    private PredictionBaseData data;


    public PredictVisController(
            Context ctx,
            IVisualisationAdapter visAdapter,
            PredictionUserParameters predictionUserParameters
    ){
        this.ctx = ctx;
        this.visAdapter = visAdapter;
        this.predictionUserParameters = predictionUserParameters;
        this.algorithm = predictionUserParameters.algorithm;
    }


    public void trigger(){
        // TODO: avoid making too many requests.
        // check whether a request was made in the last n seconds?
        new Thread(new Runnable() {
            @Override
            public void run() {

                // TODO: indicate activity / progress to user
                // TODO: try/catch RequestFailedException
                requestData();

                try {
                    data = fetchData();
                } catch (InsufficientTrackingDataException e) {
                    // TODO: throwing exceptions in threads is a bit tricky
                    // cant catch this "from outside"
                }

                final Locations locs_past = data.pastTracks;

                // TODO: throw (algorithm classes)
                // TODO: try/catch
                // TODO: Use new type PredictionResult insetead of Locations
                // TODO: (cont'd) because MultipleTrajectories will be a

                // TODO: We CANNOT assume that the prediction algorithm returns an object
                // TODO: (cont'd) of type "Locations" or one of its subtypes!
                // TODO: (cont'd) E.g.: "MultipleTrajectories" which is a collection of
                // TODO: (cont'd) trajectories and hereby of "Locations"s

                // TODO: Replace "Object" by generic PredictionOutput class
                // TODO: Obtaining locations from prediction result will be more compliceted
                // TODO: (cont'd) esp. in the case that a loop is needed

                final Locations locs_pred = (Locations) algorithm.predict(predictionUserParameters, data);

                // The entirety of all locations to be displayed
                final Locations locs_all = locs_past.addAll(locs_pred);

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    public void run() {

                        visualize(locs_past, locs_pred);
                        if (visAdapter instanceof OSMDroidVisualisationAdapter) {
                            //TJ: Center the map so that all locations are visible
                            ((OSMDroidVisualisationAdapter) visAdapter).setMapCenter(locs_all);
                            ((OSMDroidVisualisationAdapter) visAdapter).setMapZoom(locs_all);
                        }
                    }
                });


            }
        }).start();
    }




    // Request data update from Movebank
    // TODO: throw RequestFailedException
    private void requestData() /*throws RequestFailedException*/ {
        // Make an async network request for new data
        SQLDatabase.getInstance(ctx)
                .updateBirdDataSync(predictionUserParameters.bird.getStudyId(), predictionUserParameters.bird.getId());
    }



    // Fetch data from internal database
    private PredictionBaseData fetchData() throws InsufficientTrackingDataException{
        // TODO: do this based on date
        // todo: link this to hardcoded limit in algorithm
        int pastDataPoints = 50; // Use last 10 data points

        SQLDatabase db = SQLDatabase.getInstance(ctx);
        BirdData birddata = db.getBirdData(predictionUserParameters.bird.getStudyId(), predictionUserParameters.bird.getId());
        TrackingPoint tracks[] = birddata.getTrackingPoints();

        // Check data
        if (tracks.length == 0) {
            Log.e("Error", "Size of data is 0");
            throw new InsufficientTrackingDataException("Size of data is 0");
        }

        // Use only needed data
        // todo: do this via SQL request?
        //Location loc_data[] = new Location[pastDataPoints];
        Locations pastTracks = new SingleTrajectory();
        int size = tracks.length;
        for (int i = 0; i < pastDataPoints; i++) {
            //loc_data[i] = tracks[size - 1 - pastDataPoints + i].getLocation().to3D();
            pastTracks.add( tracks[tracks.length-1 - pastDataPoints + i].getLocation() );
        }

        PredictionBaseData data = new PredictionBaseData();
        data.pastTracks = pastTracks;

        return data;
        // in the future, might access different sources of information here (e.g. current weather)
        // and save that in PredictionBaseDate to be used for a position prediction
    }


    private void visualize(Locations locs_past, Locations locs_pred) {
        if (locs_pred instanceof SingleTrajectory) {
            visualizeTrajectory(locs_past, locs_pred);
        } else {
            // TODO: Other Visualization types
        }
    }

    /** Build Visualisation object based on pre-defined visual properties and draw it
      * We can write it this generically here, no matter if osm or cesium
      */
    private void visualizeTrajectory(Locations locs_past, Locations locs_pred) {

        if (locs_pred instanceof SingleTrajectory) {

            // Build Visualizations
            SingleTrajectoryVis pastVis = new SingleTrajectoryVis(
                    locs_past,
                    StyleTrajectory.pastPointCol.asString(),
                    StyleTrajectory.pastLineCol.asString(),
                    StyleTrajectory.pastPointRadius.asInt()
            );
            SingleTrajectoryVis predVis = new SingleTrajectoryVis(
                    locs_pred,
                    StyleTrajectory.predPointCol.asString(),
                    StyleTrajectory.predLineCol.asString(),
                    StyleTrajectory.predPointRadius.asInt()
            );
            SingleTrajectoryVis combinedVis = SingleTrajectoryVis.concat(
                    pastVis,
                    predVis,
                    StyleTrajectory.connectingLineColor.asString()
            );

            // Draw
            visAdapter.visualiseSingleTraj(combinedVis);

        } else {
            // TODO: Exception
        }

    }




}

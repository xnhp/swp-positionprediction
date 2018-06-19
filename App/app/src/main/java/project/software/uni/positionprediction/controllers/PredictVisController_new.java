package project.software.uni.positionprediction.controllers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

//import project.software.uni.positionprediction.datatype.BirdData;
import project.software.uni.positionprediction.datatype.BirdData;
import project.software.uni.positionprediction.datatype.TrackingPoint;
import project.software.uni.positionprediction.datatypes_new.PredictionBaseData;
import project.software.uni.positionprediction.algorithm.PredictionUserParameters;
import project.software.uni.positionprediction.datatypes_new.Cloud;
import project.software.uni.positionprediction.datatypes_new.Locations;
import project.software.uni.positionprediction.datatypes_new.PredictionResultData;
import project.software.uni.positionprediction.datatypes_new.TrackedLocation;
//import project.software.uni.positionprediction.datatypes_new.TrackingPoint;
import project.software.uni.positionprediction.datatypes_new.Trajectory;
import project.software.uni.positionprediction.interfaces.PredictionAlgorithm;
import project.software.uni.positionprediction.movebank.SQLDatabase;
import project.software.uni.positionprediction.osm.OSMDroidVisualisationAdapter;
import project.software.uni.positionprediction.osm.OSMDroidVisualisationAdapter_new;
import project.software.uni.positionprediction.util.Shape;
import project.software.uni.positionprediction.visualisation.IVisualisationAdapter_new;
import project.software.uni.positionprediction.visualisation.SingleTrajectoryVis;
import project.software.uni.positionprediction.visualisation.SingleTrajectoryVis_new;
import project.software.uni.positionprediction.visualisation.StyleTrajectory;

/**
 * Call of method "trigger" triggers execution of the following procedures:
 * 1.) Request data update from Movebank (method "request")
 * 2.) Fetch data from internal database (method "fetch")
 * 3.) Run prediction
 * 4.) Visualize (Build Visualisation object based on pre-defined visual properties and draw it)
  * @author Timo, based on PredictionWorkflowContrroller by Benny
 */
public class PredictVisController_new {


    private final Context ctx;
    private final IVisualisationAdapter_new visAdapter;
    private final PredictionUserParameters predictionUserParameters;
    private PredictionAlgorithm algorithm;
    private PredictionBaseData data;


    public PredictVisController_new(
            Context ctx,
            IVisualisationAdapter_new visAdapter,
            PredictionUserParameters predictionUserParameters
    ) {
        this.ctx = ctx;
        this.visAdapter = visAdapter;
        this.predictionUserParameters = predictionUserParameters;
        this.algorithm = predictionUserParameters.algorithm;
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
                    data = fetchData();
                } catch (InsufficientTrackingDataException e) {
                    // TODO: throwing exceptions in threads is a bit tricky
                    // cant catch this "from outside"
                }

                final Locations locs_past = data.getTrackedLocations();

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

                final PredictionResultData data_pred = algorithm.predict(predictionUserParameters, data);

                // The entirety of all locations to be displayed

                // todo: loop through map iot. get the enterity of the locations (needed for panning)
                //final Locations locs_all = locs_past.addAll(locs_pred);

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    public void run() {
                       //if (locs_all.getLength() > 0) {
                            //panToLocations(locs_all);
                            visualize(data.getTrackedLocations(), data_pred.getData());
                        //}
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
    private PredictionBaseData fetchData() throws InsufficientTrackingDataException {
        // TODO: do this based on date
        // todo: link this to hardcoded limit in algorithm
        int pastDataPoints = 50; // Use last 10 data points

        SQLDatabase db = SQLDatabase.getInstance(ctx);
        BirdData birddata = db.getBirdData(
                predictionUserParameters.bird.getStudyId(),
                predictionUserParameters.bird.getId());
        Locations tracks = birddata.getTrackingPoints();

        // Check data
        if (tracks.size() == 0) {
            Log.e("Error", "Size of data is 0");
            throw new InsufficientTrackingDataException("Size of data is 0");
        }

        // Use only needed data
        // todo: do this via SQL request?
        //Location loc_data[] = new Location[pastDataPoints];
        Trajectory pastTracks = new Trajectory();
        int size = tracks.size();
        for (int i = 0; i < pastDataPoints; i++) {
            //loc_data[i] = tracks[size - 1 - pastDataPoints + i].getLocation().to3D();
            // todo: What is going on here?
            pastTracks.add(tracks.get(tracks.size() - 1 - pastDataPoints + i));
        }

        PredictionBaseData data = new PredictionBaseData(pastTracks);

        return data;
        // in the future, might access different sources of information here (e.g. current weather)
        // and save that in PredictionBaseDate to be used for a position prediction
    }


    private void visualize(Trajectory past, Map<Shape, ArrayList<Locations>> pred) {
        visualizeTrajectory(past, 0);
        for (ArrayList<Locations> type : pred.values()) {
            int counter = 1;
            for (Locations locs : type) {
                if (locs instanceof Trajectory) {
                    visualizeTrajectory(locs, counter);
                } else if (locs instanceof Cloud) {
                    // visualizeCloud(locs, i);
                }
                counter++;
            }
        }

    }


    /**
     * Build Visualisation object based on pre-defined visual properties and draw it
     * We can write it this generically here, no matter if osm or cesium
     */
    private void visualizeTrajectory(Locations locs, int counter) {

        if (locs instanceof Trajectory) {

            // Build Visualizations
            SingleTrajectoryVis_new vis = new SingleTrajectoryVis_new(
                    locs,
                    StyleTrajectory.pastPointCol.asString(),
                    StyleTrajectory.pastLineCol.asString(),
                    StyleTrajectory.pastPointRadius.asInt()
            );

            // Draw
            visAdapter.visualiseSingleTraj(vis);

        } else {
            // TODO: Exception
        }

    }


    private void panToMyLocation() {
        if (visAdapter instanceof OSMDroidVisualisationAdapter) {
            //TJ: Center the map so that all locations are visible
            ((OSMDroidVisualisationAdapter) visAdapter).panToMyLocation();
        } // else if (visAdapter instanceof CesiumVisualisationAdapter){ }

    }

    private void panToLocations(Locations locations) {
        if (visAdapter instanceof OSMDroidVisualisationAdapter) {
            //TJ: Center the map so that all locations are visible
            ((OSMDroidVisualisationAdapter_new) visAdapter).setMapCenter(locations);
            ((OSMDroidVisualisationAdapter_new) visAdapter).setMapZoom(locations);
        } // else if (visAdapter instanceof CesiumVisualisationAdapter){ }
    }




}

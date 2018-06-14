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
import project.software.uni.positionprediction.interfaces.PredictionAlgorithmReturnsTrajectory;
import project.software.uni.positionprediction.movebank.SQLDatabase;
import project.software.uni.positionprediction.visualisation.IVisualisationAdapter;
import project.software.uni.positionprediction.visualisation.SingleTrajectoryVis;

/**
 * This class coordinates between
 *
 * data fetching -> prediction calculation -> result visualisation -> visualisation drawing
 *
 * This is not static or a singleton because it requires a context for the SQL connection.
 */
public class PredictionWorkflowController {

    private Context ctx;

    public PredictionWorkflowController(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * TODO: we might already have downloaded bird data at this point?
     *
     * We receive the visualisation adapter from outside because it needs to be linked to a map.
     * That linking should be done close to where that map is handled/created (e.g. in the Activity).
     */
    public void doSingleTrajPrediction(
            final IVisualisationAdapter visAdapter,
            final PredictionAlgorithmReturnsTrajectory algorithm,
            final PredictionUserParameters algParams
    )
        // todo: have this throw exceptions that are handled on ui level
    {

       /*
        1.) fetch data
        2.) run pred alg
        3.) build vis
        4.) draw vis
         */

       // TODO: avoid making too many requests.
        // check whether a request was made in the last n seconds?
        new Thread(new Runnable() {
            @Override
            public void run() {

                // TODO: indicate activity / progress to user

                // will make a network request for new data
                SQLDatabase.getInstance(ctx).updateBirdDataSync(algParams.bird.getStudyId(), algParams.bird.getId());

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onDataUpdate(visAdapter, algorithm, algParams);
                        } catch (InsufficientTrackingDataException e) {
                            // TODO: throwing exceptions in threads is a bit tricky
                            // cant catch this "from outside"
                        }
                    }
                });
           }
        }).start();


    }

    private void onDataUpdate(final IVisualisationAdapter visAdapter,
                              PredictionAlgorithmReturnsTrajectory algorithm,
                              final PredictionUserParameters algParams)
            throws InsufficientTrackingDataException {

        // TODO: do this based on date
        // todo: link this to hardcoded limit in algorithm
        int pastDataPoints = 50; // Use last 10 data points

        // Fetch data from database
        SQLDatabase db = SQLDatabase.getInstance(ctx);
        BirdData birddata = db.getBirdData(algParams.bird.getStudyId(), algParams.bird.getId());
        TrackingPoint tracks[] = birddata.getTrackingPoints();

        // Check data
        if (tracks.length == 0) {
            Log.e("Error", "Size of data is 0");
            throw new InsufficientTrackingDataException("Size of data is 0");
        }

        /*
        // Date error
        Date now = new Date();
        if (now.before(date_past)) {
            Log.e("Error", "Past date is in the future");
        }

        // ID error
        if (study_id <= 0) {
            Log.e("Error", "ID not valid");
        }
        if (bird_id <= 0) {
            Log.e("Error", "ID not valid");
        }
         */

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
        // in the future, might access different sources of information here (e.g. current weather)
        // and save that in PredictionBaseDate to be used for a position prediction

        // run prediction
        Locations prediction = algorithm.predict(algParams, data);

        // think about how to visualise prediction
        // todo: data type for colors
        String predPointCol = "#ff0077"; // pink
        String predLineCol = "#e28a16";  // orange
        int predPointRadius = 20;
        SingleTrajectoryVis predVis = new SingleTrajectoryVis(prediction, predPointCol, predLineCol, predPointRadius);

        // do visualise it
        //visAdapter.visualiseSingleTraj(predVis);

        // visualise past data
        String pastPointCol = "#9116e2"; // purple
        String pastLineCol = "#1668e2"; // blue
        int pastPointRadius = 15;
        SingleTrajectoryVis pastVis = new SingleTrajectoryVis(pastTracks, pastPointCol, pastLineCol, pastPointRadius);

        //visAdapter.visualiseSingleTraj(pastVis);

        // combine the two visualisations
        String connectingLineColor = "#f7f300"; // yellow
        SingleTrajectoryVis combinedVis = SingleTrajectoryVis.concat(pastVis, predVis, connectingLineColor);

        visAdapter.visualiseSingleTraj(combinedVis);

        // adapter is obtained outside of here. (see class comments)
        // thanks to the interface, we can write it this generally here, no matter if osm or cesium
    }


}

package project.software.uni.positionprediction.algorithm;

import android.content.Context;
import android.util.Log;

import java.util.Date;

import project.software.uni.positionprediction.datatype.BirdData;
import project.software.uni.positionprediction.datatype.Debug;
import project.software.uni.positionprediction.datatype.Location3D;
import project.software.uni.positionprediction.datatype.TrackingPoint;
import project.software.uni.positionprediction.interfaces.PredictionAlgorithm;
import project.software.uni.positionprediction.movebank.SQLDatabase;


public class TestConnectionSQLAlgo implements PredictionAlgorithm{

    private Context context;

    public TestConnectionSQLAlgo ( Context context ) {
        this.context = context;
    }


    @Override
    public Location3D predict(Date date_past, Date date_pred, int study_id, int bird_id) {


        SQLDatabase db = SQLDatabase.getInstance(context);

        BirdData birddata = db.getBirdData(study_id, bird_id);
        TrackingPoint data[] = birddata.getTrackingPoints();

        Debug d = new Debug();


        d.print(data);



        // algo




        return null;
    }
}

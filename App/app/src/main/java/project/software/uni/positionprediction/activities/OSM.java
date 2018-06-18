package project.software.uni.positionprediction.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.Calendar;
import java.util.Date;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.algorithm.AlgorithmExtrapolationExtended;
import project.software.uni.positionprediction.algorithm.PredictionUserParameters;
import project.software.uni.positionprediction.controllers.PredictionWorkflowController;
import project.software.uni.positionprediction.datatype.Bird;
import project.software.uni.positionprediction.interfaces.PredictionAlgorithmReturnsTrajectory;
import project.software.uni.positionprediction.osm.MapInitException;
import project.software.uni.positionprediction.osm.OSMDroidMap;
import project.software.uni.positionprediction.osm.OSMDroidVisualisationAdapter;
import project.software.uni.positionprediction.util.Message;

public class OSM extends AppCompatActivity {

    OSMDroidMap osmDroidMap;

    private Button buttonSettings = null;
    private Button buttonDownload = null;
    private Button buttonBack     = null;
    private Button buttonOnline   = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // savedInstanceState is a Bundle object containing the activity's previously saved state.
        super.onCreate(savedInstanceState);
        final OSM osm = this;

        // TODO: put this in own method
        // note: the actions in the OSMDroidMap constructor have to happen *before*
        // setContentView is called.
        osmDroidMap = new OSMDroidMap(this);


        setContentView(R.layout.activity_osm);

        MapView mapView = (MapView) findViewById(R.id.map);
        GeoPoint center = new GeoPoint(48.856359, 2.290849);

        try {
            osmDroidMap.initMap(mapView, center, 6);
        } catch (MapInitException e) {
            // todo: this will (?) be moved to a controller anyway, handle errors there
            Message msg = new Message();
            msg.disp_error(this, "Error saving maps", "Could not save maps for offline use", true);
        }


        //Bird myBird = new Bird(2911040, 2911059, "Zwitschi");

        Intent i = getIntent();
        Bird selectedBird = (Bird) i.getSerializableExtra("selectedBird");
        if (selectedBird != null) {
            // todo download maps and show note to user when done
            Log.i("VisBird OSM", "Trying to show prediction for " + selectedBird.toString());
            showPrediction(selectedBird); // TODO tmp
        } else {
            Log.e("VisBird OSM", "No bird for prediction was passed into this activity");
        }

        // TODO: this is for testing
        //Bird myTestBird = new Bird(2911059,2911040, "albatros");
        //showPrediction(myTestBird);

        // Set color in offline mode


        buttonSettings = findViewById(R.id.navbar_button_settings);
        buttonBack = findViewById(R.id.navbar_button_back);
        buttonDownload = findViewById(R.id.map_download_button);
        buttonOnline = findViewById(R.id.online_btn);
        //buttonPanTo    = findViewById(R.id.map_panto_button);
        registerEventHandlers(osm);


    }



    private void showPrediction(Bird bird) {
        // obtain an adapter
        OSMDroidVisualisationAdapter myVisAdap = new OSMDroidVisualisationAdapter();
        // set the map for the adapter
        myVisAdap.linkMap(osmDroidMap);

        // have it draw the visualisation
        // (I am making the assumption that an algorithm only has one specific fitting Visualisation)
        PredictionAlgorithmReturnsTrajectory algorithm = new AlgorithmExtrapolationExtended(this);
        PredictionWorkflowController controller = new PredictionWorkflowController(this);

        // todo: get these from user / from settings
        Date date_past = new Date(2017, 5, 1, 0, 0);
        // for what point in the future we want the prediction
        int hoursInFuture = 5;
        Calendar cl = Calendar.getInstance();
        cl.setTime(new Date());
        cl.add(Calendar.HOUR, hoursInFuture);
        Date date_pred = cl.getTime();


        controller.doSingleTrajPrediction(
                myVisAdap,
                algorithm,
                new PredictionUserParameters(
                        date_past,
                        date_pred,
                        bird
                )
        );

        // TODO from timo
        // Depending on the semantics of the prediction output, the suitable Visualization subclass
        // has to be chosen

        // PolygonVis mainauVis = new PolygonVis(mainau);
        // SingleTrajectoryVis uniMainauVis = new SingleTrajectoryVis(uniMainau);
    }




    private void registerEventHandlers(final OSM osm) {
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buttonIntent =  new Intent(osm, Settings.class);
                startActivity(buttonIntent);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BoundingBox subafrica = new BoundingBox(19.635663, 12.921289,7.006371,-4.305273);
                osmDroidMap.saveAreaToCache(subafrica, 5,7);
            }
        });


        buttonOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent buttonIntent = new Intent(osm, Cesium.class);
                startActivity(buttonIntent);
            }
        });

    }


    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        if (osmDroidMap != null) osmDroidMap.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        if (osmDroidMap != null) osmDroidMap.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
}

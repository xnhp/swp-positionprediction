package project.software.uni.positionprediction.activities;

import android.content.Context;
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
import project.software.uni.positionprediction.algorithms_new.AlgorithmExtrapolationExtended;
import project.software.uni.positionprediction.datatypes_new.PredictionUserParameters;
import project.software.uni.positionprediction.controllers.PredictionWorkflow;
import project.software.uni.positionprediction.datatypes_new.Bird;
import project.software.uni.positionprediction.fragments.FloatingMapButtons;
import project.software.uni.positionprediction.osm.MapInitException;
import project.software.uni.positionprediction.osm.OSMDroidMap;
import project.software.uni.positionprediction.osm.OSMDroidVisualisationAdapter_new;


public class OSM_new extends AppCompatActivity implements FloatingMapButtons.floatingMapButtonsClickListener{


    // CONTENTS
    // ========


    OSMDroidMap mymap;

    private Button buttonSettings = null;
    private Button buttonDownload = null;
    private Button buttonPanTo    = null;
    private Button buttonBack     = null;
    OSMDroidMap osmDroidMap;



    // BEHAVIOR
    // ========


    /** Execute the following procedures:
     * 1.) Create OSMdroid map
     * 2.) Trigger controller workflow
     * @param savedInstanceState
     * @author Benny, Timo
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);


        // 0.) Set Buttons
        buttonSettings = findViewById(R.id.navbar_button_settings);
        buttonBack = findViewById(R.id.navbar_button_back);
        buttonDownload = findViewById(R.id.map_download_button);
        //buttonPanTo    = findViewById(R.id.map_panto_button);
        //registerEventHandlers(this);


        // 1.) Create OSMdroid map.
        // ------------------------

        // This is the blank/default map, before sth. is drawn on it
        // TJ: todo: There should be an appropriate constructor in class "OSMdroidMap" to do this
        osmDroidMap = createMap(this, 2.290849, 48.856359, 6);



        // 2.) Create OSMDroidVisualizationAdapter
        // ---------------------------------------

        // TJ: todo: Alternately, provide Constructor which takes map as argument
        OSMDroidVisualisationAdapter_new myVisAdap = new OSMDroidVisualisationAdapter_new();
        myVisAdap.linkMap(osmDroidMap);



        // 3.) Trigger prediction workflow
        // -------------------------------

        // todo: this should be done in activity BirdSlelect or Settings

        /*
        PredictVisController_new pvContr = new PredictVisController_new(
                this,
                myVisAdap,
                getPredictionUserParameters()
        );
        pvContr.trigger();
*/

        PredictionWorkflow predWorkflow = new PredictionWorkflow(
                this,
                getPredictionUserParameters(),
                myVisAdap
        );
        predWorkflow.trigger();

    }







    // PROVISIONAL METHODS (TJ: todo: All of them should be implemented elsewhere!!!)
    // ==============================================================================




    // TJ: todo: Implement this Method correctly in class "Settings"
    public PredictionUserParameters getPredictionUserParameters() {

        Date date_past = new Date(2017, 5, 1, 0, 0);
        // for what point in the future we want the prediction
        int hoursInFuture = 5;
        Calendar cl = Calendar.getInstance();
        cl.setTime(new Date());
        cl.add(Calendar.HOUR, hoursInFuture);
        Date date_pred = cl.getTime();

        Intent i = getIntent();
        Bird bird = (Bird) i.getSerializableExtra("selectedBird");

        if(bird == null) Log.i("OSM_new", "no bird");

        return new PredictionUserParameters(
                new AlgorithmExtrapolationExtended(),
                date_past,
                date_pred,
                bird
        );
    }



    // TJ: todo: This should be implemented in the OSMDroidMap constructor, not here!
    private OSMDroidMap createMap(Context ctx, double centerLon, double centerLat, int zoom){
        // note: the actions in the OSMDroidMap constructor have to happen *before*
        // setContentView is called.
        mymap = new OSMDroidMap(ctx);

        setContentView(R.layout.activity_osm);

        MapView mapView = (MapView) findViewById(R.id.map);
        GeoPoint center = new GeoPoint(centerLat, centerLon);
        try {
            mymap.initMap(mapView, center, zoom);
        } catch(MapInitException e) {
            Log.i("OSM_new", e.getMessage());
        }
        return mymap;
    }

    // TJ 180612: Copied from Benny

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        if (mymap != null) mymap.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        //fo
        super.onPause();
        if (mymap != null) mymap.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    private void registerEventHandlers(final OSM_new osm) {
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
                mymap.saveAreaToCache(subafrica, 5,7);
            }
        });
    }


/**
 * Click handler triggered by the according button in
 * fragments.FloatingMapButtons
 */
    @Override
    public void onSwitchModeClick() {
        // switch to Cesium Activity
        // todo: toggle
        finish();
        Intent buttonIntent = new Intent(this, Cesium.class);
        startActivity(buttonIntent);
    }

    /**
     * Click handler triggered by the according button in
     * fragments.FloatingMapButtons
     */
    @Override
    public void onShowDataClick() {
        Log.i("osm activity", "on my fab click");
        // todo, cf changes timo
    }

    /**
     * Click handler triggered by the according button in
     * fragments.FloatingMapButtons
     */
    @Override
    public void onShowPredClick() {
        // todo, cf changes timo
    }

    /**
     * Click handler triggered by the according button in
     * fragments.FloatingMapButtons
     */
    @Override
    public void onShowLocClick() {
        Log.i("FollowLocation", "FollowLocation was " + osmDroidMap.isFollowingLocation());
        Log.i("osm activ", "on show loc click");
        // toggle following users current position
        if (!osmDroidMap.isFollowingLocation()) {
            // toggle button
            toggleShowLocBtn(true);
            // enable functionality
            osmDroidMap.enableFollowLocation();
        } else {
            toggleShowLocBtn(false);
            osmDroidMap.disableFollowLocation();
        }
    }

    /**
     * Toggle the icon of the "show location" button
     * @param state true if currently following the users location
     */
    private void toggleShowLocBtn(boolean state) {
        // obtain reference to floating buttons fragment
        FloatingMapButtons fragment = (FloatingMapButtons) getSupportFragmentManager().findFragmentById(R.id.floating_map_buttons);
        if (fragment != null) {
            fragment.toggleShowLocBtn(state);
        } else {
            Log.e("FloatingMapButtons", "No fragment button to toggle! This probably means that no fragment is attached.");
        }
    }

}//class

package project.software.uni.positionprediction.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import org.osmdroid.config.Configuration;
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
import project.software.uni.positionprediction.util.XML;


public class OSM_new extends AppCompatActivity implements FloatingMapButtons.floatingMapButtonsClickListener{


    // CONTENTS
    // ========


    private Button buttonSettings = null;
    private Button buttonDownload = null;
    private Button buttonBack     = null;
    OSMDroidMap osmDroidMap;

    // padding to edges of map when zooming/panning
    // to view something on the map.
    // e.g. for zoomToBoundingBox
    private int zoomPadding = 15;


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

        // todo: this should be done in activity BirdSelect or Settings
        // instead in order to get something to draw: access static fields from PredictionWorkflow,
        // hand it and adapter to visualisatiion controller
        PredictionWorkflow predWorkflow = new PredictionWorkflow(
                this,
                getPredictionUserParameters(),
                myVisAdap
        );
        predWorkflow.trigger();



        // 4.) Set Buttons & register event handlers
        // -----------------------------------------
        buttonSettings = findViewById(R.id.navbar_button_settings);
        buttonBack = findViewById(R.id.navbar_button_back);
        buttonDownload = findViewById(R.id.map_download_button);
        registerEventHandlers(this);

    }







    // PROVISIONAL METHODS (TJ: todo: All of them should be implemented elsewhere!!!)
    // ==============================================================================


    // TJ: todo: Implement this Method correctly in class "Settings"
    // bm: Would rather put this somewhere else b/c "Settings" is an
    // Activity and should only do UI-related stuff.
    // But thats not important
    public PredictionUserParameters getPredictionUserParameters() {

        // hardcoded date marking the lower bound for tracking data
        // which should be included in the prediction
        // note that if hour, second, ... is not specified the
        // *current* hour, second, ... will be used
        Calendar cal = Calendar.getInstance();
        cal.set(2007, Calendar.MAY, 9);
        Date date_past = cal.getTime();

        // for what point in the future we want the prediction
        // hardcoded: 5 hours from current datetime
        int hoursInFuture = 5;
        Calendar cl = Calendar.getInstance();
        cl.setTime(new Date());
        cl.add(Calendar.HOUR, hoursInFuture);
        Date date_pred = cl.getTime();

        // todo: fetch from PredictionWorkflow controller instead
        Intent i = getIntent();
        Bird bird = (Bird) i.getSerializableExtra("selectedBird");

        // for debug purposes
        if(bird == null) {
            Log.i("OSM_new", "no bird");
            bird = new Bird(2911059, 2911040, "Galapagos");
        }

        return new PredictionUserParameters(
                new AlgorithmExtrapolationExtended(this),
                date_past,
                date_pred,
                bird
        );
    }



    // TJ: todo: This should be implemented in the OSMDroidMap constructor, not here!
    private OSMDroidMap createMap(Context ctx, double centerLon, double centerLat, int zoom){
        // note: the actions in the OSMDroidMap constructor have to happen *before*
        // setContentView is called.
        osmDroidMap = new OSMDroidMap(ctx);

        setContentView(R.layout.activity_osm);

        MapView mapView = (MapView) findViewById(R.id.map);
        GeoPoint center = new GeoPoint(centerLat, centerLon);
        try {
            osmDroidMap.initMap(mapView, center, zoom);
        } catch(MapInitException e) {
            Log.i("OSM_new", e.getMessage());
        }
        return osmDroidMap;
    }


    /*
     * On activity resume
     */
    public void onResume(){
        super.onResume();
        // this will refresh the osmdroid configuration on resuming.
        // if you make changes to the configuration, use
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        if (osmDroidMap != null) osmDroidMap.onResume(); // needed for overlays (location, ...)
    }

    /*
     * On activity pause
     */
    public void onPause(){
        super.onPause();
        if (osmDroidMap != null) osmDroidMap.onPause();  // needed for overlays (location, ...)
    }

    /*
     * Register event handlers for user interaction (buttons etc)
     */
    private void registerEventHandlers(final Context ctx) {
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buttonIntent =  new Intent(ctx, Settings.class);
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

        /* manual interaction with the map will always disable osmdroid's
           followLocation. This is used to update the button accordingly.
        */
        osmDroidMap.mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                toggleShowLocBtn(false);
                return false; // indicates that the touch event was not
                // successfully handled (it will propagated
                // further, such as to the mapView to actually
                // pan/zoom/rotate it).
            }
        });
    }


    /**
     * Click handler triggered by the according button in
     * fragments.FloatingMapButtons
     */
    @Override
    public void onSwitchModeClick() {
        toggleShowLocBtn(false);
        // switch to Cesium Activity
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
        toggleShowLocBtn(false);

        if (PredictionWorkflow.vis_past == null) {
            Log.e("FloatingMapButtons", "no past vis available");
            return;
        }
        osmDroidMap.mapView.invalidate();
        osmDroidMap.safeZoomToBoundingBox(
                PredictionWorkflow.vis_past.getBoundingBox(),
                false,
                this.zoomPadding
        );
    }

    /**
     * Click handler triggered by the according button in
     * fragments.FloatingMapButtons
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onShowPredClick() {
        toggleShowLocBtn(false);

        /*
        Theoretically, zoomToBoundingBox should have been fixed as per
        https://github.com/osmdroid/osmdroid/pull/702
         */
        if (PredictionWorkflow.vis_pred== null) {
            Log.e("FloatingMapButtons", "no prediction visualisation available (yet?)");
            return;
        }
        BoundingBox mybb = PredictionWorkflow.vis_pred.getBoundingBox();
        // BoundingBox testbb = new BoundingBox(10, 10, 20, 20);
        // final BoundingBox testbb = new BoundingBox(44899816, 5020385,44899001,5019482);


        MapView mMapView = osmDroidMap.mapView;
        mMapView.invalidate();
        osmDroidMap.safeZoomToBoundingBox(mybb, false, this.zoomPadding);


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

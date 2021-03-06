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
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.controllers.PredictionWorkflow;
import project.software.uni.positionprediction.controllers.VisualizationWorkflow;
import project.software.uni.positionprediction.fragments.FloatingMapButtons;
import project.software.uni.positionprediction.osm.OSMDroidMap;
import project.software.uni.positionprediction.osm.OSMDroidVisualisationAdapter;
import project.software.uni.positionprediction.util.AsyncTaskCallback;
import project.software.uni.positionprediction.util.Message;


public class OSM extends AppCompatActivity implements FloatingMapButtons.floatingMapButtonsClickListener{


    private Button buttonSettings = null;
    private Button buttonBack     = null;
    private Context ctx;
    OSMDroidMap osmDroidMap;

    OSMDroidVisualisationAdapter visAdap;

       /** Execute the following procedures:
     * 1.) Create OSMDroid map
     * 2.) Trigger controller workflow
     * @param savedInstanceState
     * @author Benny, Timo
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.ctx = this;

        final Context finalContext = this;


        // 1.) Create OSMDroid map.
        // ------------------------
        // This is the blank/default map, before sth. is drawn on it
        osmDroidMap = createMap(this, 2.290849, 48.856359, 6);


        // 2.) Create OSMDroidVisualizationAdapter
        // ---------------------------------------
        visAdap = new OSMDroidVisualisationAdapter();
        visAdap.linkMap(osmDroidMap);


        // 3.) Always visualise the current prediction on activity start.
        //     Hand own VisAdapter to VisWorkflow to
        //     display visualisations on map
        // -----------------------------------------
        VisualizationWorkflow visWorkflow = new VisualizationWorkflow(
                finalContext,
                visAdap,
                PredictionWorkflow.vis_past,
                PredictionWorkflow.vis_pred);
        visWorkflow.trigger();

        // zoom/pan past & pred visualisations into view
        // these are guaranteed to be set because this activity
        // is not until its ready

        // 4.) Set Buttons & register event handlers
        // -----------------------------------------
        buttonSettings = findViewById(R.id.navbar_button_settings);
        buttonBack = findViewById(R.id.navbar_button_back);
        registerEventHandlers(this);

    }


    private OSMDroidMap createMap(Context ctx, double centerLon, double centerLat, int zoom){
        // note: the actions in the OSMDroidMap constructor have to happen *before*
        // setContentView is called.
        osmDroidMap = new OSMDroidMap(ctx);

        setContentView(R.layout.activity_osm);

        MapView mapView = findViewById(R.id.map);
        GeoPoint center = new GeoPoint(centerLat, centerLon);
        osmDroidMap.initMap(mapView, center, zoom);
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

       if ( ! visAdap.areVisCurrent(PredictionWorkflow.vis_past, PredictionWorkflow.vis_pred))  {

           visAdap.clear();

           VisualizationWorkflow visWorkflow = new VisualizationWorkflow(
                   ctx,
                   visAdap,
                   PredictionWorkflow.vis_past,
                   PredictionWorkflow.vis_pred);
           visWorkflow.trigger();

       }

        Message.show_pending_messages(this);

        // if(PredictionWorkflow.getInstance(this).isRefreshNeeded()) PredictionWorkflow.getInstance(this).refreshPrediction(this);
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

        /* manual interaction with the map will always disable osmdroid's
           followLocation. This is used to update the button accordingly.
        */
        osmDroidMap.mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                toggleShowLocBtn(false);
                // in fact, osmdroid does this automatically on pan,
                // however when following the location is enabled and
                // the pred/vis is refreshed, you pan away from the map
                // and you will snap back
                osmDroidMap.disableFollowLocation();
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
    public void onRefreshClick() {
        if (visAdap != null) visAdap.clear();

        PredictionWorkflow.getInstance(this).refreshPrediction(ctx, new AsyncTaskCallback(){

            @Override
            public void onFinish() {
                // call visualisation
                VisualizationWorkflow visWorkflow = new VisualizationWorkflow(
                        ctx,
                        visAdap,
                        PredictionWorkflow.vis_past,
                        PredictionWorkflow.vis_pred);
                visWorkflow.trigger();
                // instantly refresh map view
                // (else we'd only get an update on an interaction)
                osmDroidMap.mapView.invalidate();

                // this behaviour is horribly coupled but i lack the time.
                // refreshing the prediction disables following the location
                // cf OSMDroidVisualisationAdapter.clear()
                toggleShowLocBtn(false);
            }

            @Override
            public void onCancel() {
                Log.e("OSM", "prediction calculation task/thread interrupted");
            }

            @Override
            public Context getContext() {
                return ctx;
            }
        });


    }


    /**
     * Click handler triggered by the according button in
     * fragments.FloatingMapButtons
     */
    @Override
    public void onShowDataClick() {
        disableFollowLocationWithUI();
        visAdap.showData();

    }

    /**
     * Click handler triggered by the according button in
     * fragments.FloatingMapButtons
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onShowPredClick() {
        disableFollowLocationWithUI();
        visAdap.showPrediction();
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
            enableFollowLocationWithUI();
        } else {
            disableFollowLocationWithUI();
        }
    }

    /*
        has to be here in the activity because
        only here we can modify the fragment
     */
    private void enableFollowLocationWithUI() {
        if (!osmDroidMap.isFollowingLocation()) {
            // toggle button
            toggleShowLocBtn(true);
            // enable functionality
            osmDroidMap.enableFollowLocation();
        }
    }

    private void disableFollowLocationWithUI() {
        if (osmDroidMap.isFollowingLocation()) {
            toggleShowLocBtn(false);
            osmDroidMap.disableFollowLocation();
        }
    }

    public static void setSettingsChanged(){
        Log.d("Settings", "OSM got new Settings");
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

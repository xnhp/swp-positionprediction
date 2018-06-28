package project.software.uni.positionprediction.activities;

import android.content.Intent;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
import project.software.uni.positionprediction.fragments.FloatingMapButtons;
import project.software.uni.positionprediction.interfaces.PredictionAlgorithmReturnsTrajectory;
import project.software.uni.positionprediction.osm.MapInitException;
import project.software.uni.positionprediction.osm.OSMDroidMap;
import project.software.uni.positionprediction.osm.OSMDroidVisualisationAdapter;
import project.software.uni.positionprediction.util.BearingListener;
import project.software.uni.positionprediction.util.BearingProvider;
import project.software.uni.positionprediction.util.Message;
import project.software.uni.positionprediction.util.OrientationListener;
import project.software.uni.positionprediction.util.OrientationProvider;

public class OSM extends AppCompatActivity implements FloatingMapButtons.floatingMapButtonsClickListener {

    OSMDroidMap osmDroidMap;

    private Button buttonSettings = null;
    private Button buttonDownload = null;
    private Button buttonBack     = null;
    private ImageView compassArrow = null;

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
        // todo: automatically set to position of visualisation
        // todo: already done by timo, just not merged into this branch yet.
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

        // todo Set color in offline mode

        compassArrow = findViewById(R.id.compassArrow);

        buttonSettings = findViewById(R.id.navbar_button_settings);
        buttonBack = findViewById(R.id.navbar_button_back);
        buttonDownload = findViewById(R.id.map_download_button);
        registerEventHandlers(osm);


        // this.testOrientationProvider();
        this.testBearingProvider();


    }

    private void testOrientationProvider() {
        OrientationProvider op = new OrientationProvider(this);
        op.registerOrientationUpdates(new OrientationListener() {
            @Override
            public void onOrientationChanged(float newOrientation) {
                Log.i("OSM", "received new orientation: " + newOrientation);
            }
        });
    }

    private void testBearingProvider() {
        // todo: only for testing
        BearingProvider provider = new BearingProvider();

        Location targetLocation = new Location("foo");
        targetLocation.setLatitude(48.856614); // somewhere in
        targetLocation.setLongitude(2.352222); // paris

        provider.registerBearingUpdates(this, targetLocation, new BearingListener() {
            @Override
            public void onBearingChanged(float newBearing) {
                Log.i("OSM", "received bearing: " + newBearing);
                rotateArrow(newBearing);

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });
    }


    private void rotateArrow(float angle){
        // todo: angle = 0 -> no rotation
        // so if image is of arrow pointing leftwards,
        // rotate it by 90°.
        // todo: set origin of rotation to center of graphic
        // todo: have graphic not be clipped
        // todo: move all this into fragment
        // todo: comments in calculation of angle in BearingProvider (the +2*pi thing)
        Matrix matrix = new Matrix();
        compassArrow.setScaleType(ImageView.ScaleType.MATRIX);
        matrix.postRotate(angle, 100f, 100f);
        compassArrow.setImageMatrix(matrix);
    }



    private void showPrediction(Bird bird) {
        // obtain an adapter
        OSMDroidVisualisationAdapter myVisAdap = new OSMDroidVisualisationAdapter();
        // set the map for the adapter
        myVisAdap.linkMap(osmDroidMap);

        // have it draw the visualisation
        // (I am making the assumption that an algorithm only has one specific fitting Visualisation)
        PredictionAlgorithmReturnsTrajectory algorithm = new AlgorithmExtrapolationExtended();
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


        // manual interaction with the map will always disable osmdroid's
        // followLocation. This is used to update the button accordingly.
        osmDroidMap.mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.i("foo", "on touch listener on mapview called, returning true");
                toggleShowLocBtn(false);
                return false; // indicates that the touch event was not
                // successfully handled (it will propagated
                // further, such as to the mapView to actually
                // pan/zoom/rotate it).
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




}

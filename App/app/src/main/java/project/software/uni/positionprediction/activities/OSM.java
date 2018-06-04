package project.software.uni.positionprediction.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import project.software.uni.positionprediction.R;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.Date;

import project.software.uni.positionprediction.algorithm.AlgorithmExtrapolationExtended;
import project.software.uni.positionprediction.algorithm.PredictionParameters;
import project.software.uni.positionprediction.controllers.PredictionWorkflowController;
import project.software.uni.positionprediction.datatype.Bird;
import project.software.uni.positionprediction.interfaces.SingleTrajPredictionAlgorithm;
import project.software.uni.positionprediction.osm.OSMDroidMap;
import project.software.uni.positionprediction.osm.OSMDroidVisualisationAdapter;

public class OSM extends AppCompatActivity {

    OSMDroidMap mymap;

    private Button buttonSettings = null;
    private Button buttonDownload = null;
    private Button buttonPanTo    = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final OSM osm = this;

        // TODO: put this in own method
        // note: the actions in the OSMDroidMap constructor have to happen *before*
        // setContentView is called.
        mymap = new OSMDroidMap(this);

        setContentView(R.layout.activity_osm);



        MapView mapView = (MapView) findViewById(R.id.map);
        GeoPoint center = new GeoPoint(48.856359, 2.290849);
        mymap.initMap(mapView, center, 6);



       /* ArrayList<IGeoPoint> testPosGp = new ArrayList<>();
        testPosGp.add(new GeoPoint(47.680503, 9.177198));
        testPosGp.add(new GeoPoint(47.679463, 9.179558));
        testPosGp.add(new GeoPoint(47.678871, 9.181532));
*/
        // mymap.drawTracks(testPosGp, "#ff0077", "#00ff88");


        Bird myBird = new Bird(42,17, "Zwitschi"); // TODO tmp
        showVis(myBird); // TODO tmp


        buttonSettings = findViewById(R.id.navigation_button_settings);
        buttonDownload = findViewById(R.id.map_download_button);
        //buttonPanTo    = findViewById(R.id.map_panto_button);
        registerEventHandlers(osm);
    }



    private void showVis(Bird bird) {
        // obtain an adapter
        OSMDroidVisualisationAdapter myVisAdap = new OSMDroidVisualisationAdapter();
        // set the map for the adapter
        myVisAdap.linkMap(mymap);

        // have it draw the visualisation
        // TODO: the info that it is a single traj will come in dynamically, not
        // via the method name, because:
        // the kind of prediction algorithm used also is flexible and will be determined in
        // the Activity (or above)
        // (I am making the assumption that an algorithm only has one specific fitting Visualisation)
        SingleTrajPredictionAlgorithm algorithm = AlgorithmExtrapolationExtended.getInstance();
        PredictionWorkflowController.doSingleTrajPrediction(
                myVisAdap,
                algorithm,
                new PredictionParameters(
                        new Date("today"),
                        new Date("another time"),
                        bird
                )
        );
    }




    private void registerEventHandlers(final OSM osm) {
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buttonIntent =  new Intent(osm, Settings.class);
                startActivity(buttonIntent);
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

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        if (mymap != null) mymap.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        if (mymap != null) mymap.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
}

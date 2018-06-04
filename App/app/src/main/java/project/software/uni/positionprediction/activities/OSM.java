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

import project.software.uni.positionprediction.datatype.Locations2D;
import project.software.uni.positionprediction.osm.OSMDroidMap;
import project.software.uni.positionprediction.osm.OSMDroidAdapter;
import project.software.uni.positionprediction.visualisation.PolygonVis;
import project.software.uni.positionprediction.visualisation.SingleTrajectoryVis;

public class OSM extends AppCompatActivity {

    OSMDroidMap mymap;

    private Button buttonSettings = null;
    private Button buttonDownload = null;
    private Button buttonPanTo    = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final OSM osm = this;

        // note: the actions in the OSMDroidMap constructor have to happen *before*
        // setContentView is called.
        mymap = new OSMDroidMap(this);

        setContentView(R.layout.activity_osm);

        // =======
        // BUTTONS
        // =======

        buttonSettings = findViewById(R.id.navigation_button_settings);
        buttonDownload = findViewById(R.id.map_download_button);
        //buttonPanTo    = findViewById(R.id.map_panto_button);



        // =========
        // MAP INIT.
        // =========

        MapView mapView = (MapView) findViewById(R.id.map);
        GeoPoint center = new GeoPoint(47.680503, 9.177198);
        mymap.initMap(mapView, center, 14);



        // ==========================
        // HARD-CODED TEST ("MAINAU")
        // ==========================

        // The prediction would output an Object of type Locations2D or Locations3D

        Locations2D mainau = new Locations2D(
                "47.701975, 9.190788;"
                        + "47.704131, 9.189782;"
                        + "47.705888, 9.190251;"
                        + "47.707683, 9.192522;"
                        + "47.707973, 9.196295;"
                        + "47.707445, 9.198616;"
                        + "47.704690, 9.201016;"
                        + "47.703535, 9.202132;"
                        + "47.702446, 9.200501;"
                        + "47.702618, 9.194439"
        );

        Locations2D uniMainau = new Locations2D(
                "47.691057, 9.186505;"
                        + "47.693811, 9.189909;"
                        + "47.698893, 9.189131;"
                        + "47.701975, 9.190788"
        );


        // Depending on the semantics of the prediction output, the suitable Visualization subclass
        // has to be chosen

        PolygonVis mainauVis = new PolygonVis(mainau);
        SingleTrajectoryVis uniMainauVis = new SingleTrajectoryVis(uniMainau);



        // obtain an adapter to the OSMdroid map and draw

        OSMDroidAdapter osmDroid = new OSMDroidAdapter(mymap);
        osmDroid.visualise(mainauVis);
        osmDroid.visualise(uniMainauVis);


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



    // ============
    // MAP BEHAVIOR
    // ============

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
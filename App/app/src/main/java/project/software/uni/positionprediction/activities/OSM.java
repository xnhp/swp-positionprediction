package project.software.uni.positionprediction.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import project.software.uni.positionprediction.R;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.osm.OSMDroidMap;

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

        buttonSettings = findViewById(R.id.navigation_button_settings);
        buttonDownload = findViewById(R.id.map_download_button);
        buttonPanTo    = findViewById(R.id.map_panto_button);

        MapView mapView = (MapView) findViewById(R.id.map);
        GeoPoint center = new GeoPoint(48.856359, 2.290849);
        mymap.initMap(mapView, center, 6);



        // pick me up:
        // create a sequence of test positions, render them nicely on the map
        ArrayList<IGeoPoint> testPos = new ArrayList<>();
        testPos.add(new GeoPoint(47.680503, 9.177198));
        testPos.add(new GeoPoint(47.679463, 9.179558));
        testPos.add(new GeoPoint(47.678871, 9.181532));

        mymap.showFastPoints(testPos);



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


        buttonPanTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Executing animations subsequentially like this does not randomly execute
                // one of them
                mymap.panWithAnimationTo(new GeoPoint(24.168111, 15.909570));
                //mymap.setZoom(10);
                mymap.setZoomWithEvenAnimation(10);
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

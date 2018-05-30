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

import project.software.uni.positionprediction.classes.Location3D;
import project.software.uni.positionprediction.osm.OSMDroidMap;
import project.software.uni.positionprediction.osm.OSMDroidVisualisationAdapter;
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

        buttonSettings = findViewById(R.id.navigation_button_settings);
        buttonDownload = findViewById(R.id.map_download_button);
        //buttonPanTo    = findViewById(R.id.map_panto_button);

        MapView mapView = (MapView) findViewById(R.id.map);
        GeoPoint center = new GeoPoint(48.856359, 2.290849);
        mymap.initMap(mapView, center, 6);



        ArrayList<IGeoPoint> testPosGp = new ArrayList<>();
        testPosGp.add(new GeoPoint(47.680503, 9.177198));
        testPosGp.add(new GeoPoint(47.679463, 9.179558));
        testPosGp.add(new GeoPoint(47.678871, 9.181532));

        // mymap.drawTracks(testPosGp, "#ff0077", "#00ff88");



        ArrayList<Location3D> testPosLoc = new ArrayList<>();
        testPosLoc.add(new Location3D(47.680503, 9.177198));
        testPosLoc.add(new Location3D(47.679463, 9.179558));
        testPosLoc.add(new Location3D(47.678871, 9.181532));
        // we would receive a visualisation object as output from a prediction algorithm
        SingleTrajectoryVis myVis = new SingleTrajectoryVis();
        myVis.traj = testPosGp;
        myVis.pointColor = "#ff0077"; // pink
        myVis.lineColor = "#00ff88";  // bright green


        // obtain an adapter
        OSMDroidVisualisationAdapter myVisAdap = new OSMDroidVisualisationAdapter();
        // set the map for the adapter
        myVisAdap.linkMap(mymap);
        // have it draw the visualisation
        myVisAdap.visualiseSingleTraj(myVis);


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

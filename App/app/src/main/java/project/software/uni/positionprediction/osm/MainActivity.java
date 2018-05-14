package project.software.uni.positionprediction.osm;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import project.software.uni.positionprediction.R;

import project.software.uni.positionprediction.osm.OSMDroidMap;

/*
    test scenario:
        1.) open with internet access - do not scroll around to avoid downloading of other tiles
        2.) press mail icon to download specific area
        3.) disable internet access
        4.) scroll south to see downloaded area

*/

public class MainActivity extends AppCompatActivity {

    // dummy values for testing
    // `subafrica` is a subset of `africa`
    BoundingBox africa = new BoundingBox(39.362595,58.975977, -49.615474,-27.508398 );
    BoundingBox subafrica = new BoundingBox(19.635663, 12.921289,7.006371,-4.305273);
    OSMDroidMap mymap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        MapView mapView = (MapView) findViewById(R.id.map);
        GeoPoint center = new GeoPoint(48.856359, 2.290849);
        mymap = new OSMDroidMap(MainActivity.this, mapView, center, 6);


        // handler for action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mymap.saveAreaToCache(subafrica, 5,7);

                Snackbar.make(view, "Button was clicked", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

}

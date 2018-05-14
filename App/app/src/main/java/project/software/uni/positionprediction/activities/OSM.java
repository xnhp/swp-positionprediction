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

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.osm.OSMDroidMap;

public class OSM extends AppCompatActivity {

    OSMDroidMap mymap = null;

    private Button buttonSettings = null;
    private Button buttonDownload = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_osm);

        buttonSettings = findViewById(R.id.navigation_button_settings);
        buttonDownload = findViewById(R.id.map_download_button);

        final OSM osm = this;


        MapView mapView = (MapView) findViewById(R.id.map);
        GeoPoint center = new GeoPoint(48.856359, 2.290849);
        mymap = new OSMDroidMap(OSM.this, mapView, center, 6);


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
}

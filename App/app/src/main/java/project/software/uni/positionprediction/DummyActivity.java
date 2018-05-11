package project.software.uni.positionprediction;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.StorageUtils;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import project.software.uni.positionprediction.movebank.MovebankConnector;

public class DummyActivity extends AppCompatActivity {

    private static Context context;

    MapView map = null;
    CacheManager cmgr = null;
    BoundingBox bb = new BoundingBox(0,0,10,10);
    BoundingBox africa = new BoundingBox(39.362595,58.975977, -49.615474,-27.508398 );
    BoundingBox subafrica = new BoundingBox(19.635663, 12.921289,7.006371,-4.305273);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);
        DummyActivity.context = getApplicationContext();


        /*

         test scenario:
         1.) open with internet access - do not scroll around to avoid downloading of other tiles
         2.) press mail icon to download specific area
         3.) disable internet access
         4.) scroll south to see downloaded area

         */


        // cf end of this section: https://developer.android.com/training/permissions/requesting
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                System.out.println("not granted");

                // Permission is not granted

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        42);

        } else {
            System.out.println("granted");
            // Permission has already been granted
        }

        GeoPoint startPoint = new GeoPoint(48.856359, 2.290849);
        final GeoPoint otherPoint = new GeoPoint(48.860998, 2.295033);
        GeoPoint zero       = new GeoPoint(0F, 0F);
        GeoPoint waydown    = new GeoPoint(-39.019778, -28.422452);
        GeoPoint wayup      = new GeoPoint(76.105834, 66.846111);
        final ArrayList<GeoPoint> all = new ArrayList<GeoPoint>(Arrays.asList(startPoint, otherPoint));

        //load/initialize the osmdroid configuration, this can be done
        final Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        //inflate and create the map
        setContentView(R.layout.activity_map);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        IMapController mapController = map.getController();
        mapController.setZoom(6);
        mapController.setCenter(startPoint);

        cmgr = new CacheManager(map);

        cmgr.cleanAreaAsync(DummyActivity.this, africa, 5, 7);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cm.cleanAreaAsync(ctx, all, 1, 17);
                triggerDownload();
                Snackbar.make(view, "foo", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



    }


    private void triggerDownload() {
        //this triggers the download
        cmgr.downloadAreaAsync(this, subafrica, 5, 7, new CacheManager.CacheManagerCallback() {
            @Override
            public void onTaskComplete() {
                Toast.makeText(DummyActivity.this, "Download complete!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTaskFailed(int errors) {
                Toast.makeText(DummyActivity.this, "Download complete with " + errors + " errors", Toast.LENGTH_LONG).show();
            }

            @Override
            public void updateProgress(int progress, int currentZoomLevel, int zoomMin, int zoomMax) {
                //NOOP since we are using the build in UI
            }

            @Override
            public void downloadStarted() {
                //NOOP since we are using the build in UI
            }

            @Override
            public void setPossibleTilesInArea(int total) {
                //NOOP since we are using the build in UI
            }
        });
    }




    @Override
    protected void onResume() {
        super.onResume();

        // needed for compass, my location overlays
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // needed for compass, my location overlays
        map.onPause();
    }


    public static Context getAppContext() {
        return DummyActivity.context;
    }


}

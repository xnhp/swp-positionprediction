package project.software.uni.positionprediction.osm;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import project.software.uni.positionprediction.activities.OSM;


/**
 * This class embodies a map that displays OpenStreetMap/Mapnik data via OSMDroid.
 * It exposes methods to programmatically navigate the map, as well as save and clear offline data.
 *
 * The corresponding layout component (given to the constructor of this class) would be sth like
 *      <org.osmdroid.views.MapView android:id="@+id/map"
 *        android:layout_width="fill_parent"
 *        android:layout_height="fill_parent" />
 *
 * TODO: Check whether osmdroid would download the same area twice although cached
 * TODO: Check when cached data is deleted
 */
public class OSMDroidMap {

    public MapView map = null; // initalised by constructor
        // exposed for calling map.onResume() and map.onPause() in the activity.
    private IMapController mapController = null;
    private CacheManager cacheManager = null;
    private Context context = null;

    public OSMDroidMap(Context ctx, MapView view, GeoPoint center, final double zoom) {
        // I decided to only take a MapView here instead of taking the layout id and doing the cast
        // to `MapView` here because after all the mapping from view id to to a MapView object is not
        // the responsibility of this class.
        // sample usage: argument `view` could e.g. be given as `(MapView) findViewById(R.id.map)`;
        map = (MapView) view;
        mapController = map.getController();
        cacheManager = new CacheManager(map);
        context = ctx;

        map.setTileSource(OSMDroidMapConfiguration.onlineTileSourceBase);

        // load OSMDroid configuration
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setZoom(zoom);
        setCenter(center);

        // TODO
        // PermissionManager.ensurePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        // cf end of this section: https://developer.android.com/training/permissions/requesting
        // TODO: Might be that
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            System.out.println("not granted");

            // Permission is not granted

            Activity actvt = (Activity) context;

            ActivityCompat.requestPermissions(actvt,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    42);

        } else {
            System.out.println("granted");
            // Permission has already been granted
        }
    }



    /**
     * Saves all zoom levels within range of parameters of a specified region to the cache.
     * @param bbox area to be saved
     * @param zoomMin minimal zoom level to be saved
     * @param zoomMax maximum zoom level to be saved
     */
    public void saveAreaToCache(BoundingBox bbox, int zoomMin, int zoomMax) {
        // TODO: What should the UI look like when downloading maps? should there be a progress bar?
        // TODO: osmdroid also provides other methods for downloading. which is best suited?
        // TODO: move callbacks out of this module?
        cacheManager.downloadAreaAsync(context, bbox, 5, 7, new CacheManager.CacheManagerCallback() {
            @Override
            public void onTaskComplete() {
                Toast.makeText(context, OSMDroidMapConfiguration.downloadCompleteText, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTaskFailed(int errors) {
                // TODO
                Toast.makeText(context, "Download complete with " + errors + " errors", Toast.LENGTH_LONG).show();
            }

            @Override
            public void updateProgress(int progress, int currentZoomLevel, int zoomMin, int zoomMax) {
                // TODO
                //NOOP since we are using the build in UI
            }

            @Override
            public void downloadStarted() {
                // TODO
                //NOOP since we are using the build in UI
                System.out.println("download started");
            }

            @Override
            public void setPossibleTilesInArea(int total) {
                // TODO
                //NOOP since we are using the build in UI
                System.out.println("set possible tiles");
            }
        });
    }

    public void cleanAreaFromCache(BoundingBox bbox, int zoomMin, int zoomMax) {
        // zoom levels are of type `int` here because `cleanAreaAsync` requires it. This seems to  be
        // an inconsistency in osmdroid.
        cacheManager.cleanAreaAsync(context, bbox, zoomMin, zoomMax);
    }

    /**
     * Set the zoom level of the map
     * @param zoom sensible zoom levels for OSM: minimum 2, maximum 19
     */
    public void setZoom(final double zoom) {
        mapController.setZoom(zoom);
    }

    /**
     * Set the zoom level of the map
     * @param zoom sensible zoom levels for OSM: minimum 2, maximum 19
     */
    public void setZoom(int zoom) {
        // a more recent implementation of setZoom requires its argument to be of type float.
        // this is for consistency with other methods which take parameters related to zoom
        // (e.g. minZoom) but have them declared as `int`s.
        mapController.setZoom((float) zoom);
    }

    /**
     * Pan the map so that `center` is in the center of the visible map.
     * @param center
     */
    public void setCenter(GeoPoint center) {
        mapController.setCenter(center);
    }

    // TODO: nicely integrate the map into the layout

    // TODO: Expose methods for pan, etc
}

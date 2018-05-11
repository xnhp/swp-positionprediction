package project.software.uni.positionprediction.osm;

import android.content.Context;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;


/**
 * This class embodies a map that displays OpenStreetMap/Mapnik data via OSMDroid.
 * It exposes methods to programmatically navigate the map, as well as save and clear offline data.
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
            }

            @Override
            public void setPossibleTilesInArea(int total) {
                // TODO
                //NOOP since we are using the build in UI
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

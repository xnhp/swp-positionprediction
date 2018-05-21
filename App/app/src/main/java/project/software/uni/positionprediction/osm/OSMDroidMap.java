package project.software.uni.positionprediction.osm;


import android.Manifest;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.util.PermissionManager;



/**
 * This class embodies a map that displays OpenStreetMap/Mapnik data via OSMDroid.
 * It exposes methods to programmatically navigate the map, as well as save and clear offline data.
 *
 * Note that zooming and panning at the same time is not supported yet. Calling both methods at the
 * same time will result in weird behaviour.
 *
 * The corresponding layout component (given to the constructor of this class) would be sth like
 *      <org.osmdroid.views.MapView android:id="@+id/map"
 *        android:layout_width="fill_parent"
 *        android:layout_height="fill_parent" />
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

        // use Mapnik by default
        // TODO: other tile sources interesting?
        map.setTileSource(TileSourceFactory.MAPNIK);

        // load OSMDroid configuration
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setZoom(zoom);
        setCenter(center);

        PermissionManager.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.dialog_permission_storage_text, (AppCompatActivity) context);
    }



    /**
     * Saves all zoom levels within range of parameters of a specified region to the cache.
     * osmdroid checks if tiles are already downloaded and does not redownload in that case.
     * (cf. CacheManager.loadTile())
     * osmdroid checks if cached tiles exceed a hardcoded capacity, then removes tiles that are not
     * in or close to the viewport (cf. MapTileCache.garbageCollection()).
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
                Toast.makeText(context, R.string.dialog_map_download_complete, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTaskFailed(int errors) {
                // TODO
                Toast.makeText(context, "Download complete with " + errors + " errors", Toast.LENGTH_LONG).show();
            }

            @Override
            public void updateProgress(int progress, int currentZoomLevel, int zoomMin, int zoomMax) {
                // TODO
                //NOOP since we are using the built in UI
            }

            @Override
            public void downloadStarted() {
                // TODO
                //NOOP since we are using the built in UI
                System.out.println("download started");
            }

            @Override
            public void setPossibleTilesInArea(int total) {
                // TODO
                //NOOP since we are using the built in UI
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
     * Set the zoom level of the map, no animation
     * @param zoom sensible zoom levels for OSM/Mapnik: minimum 2, maximum 19
     */
    public void setZoom(final double zoom) {
        mapController.setZoom(zoom);
    }

    /**
     * Set the zoom level of the map, no animation
     * @param zoom sensible zoom levels for OSM/Mapnik: minimum 2, maximum 19
     */
    public void setZoom(final int zoom) {
        // a more recent implementation of setZoom requires its argument to be of type float.
        // this is for consistency with other methods which take parameters related to zoom
        // (e.g. minZoom) but have them declared as `int`s.
        mapController.setZoom((float) zoom);
    }

    private double getZoomLevel() {
        return map.getProjection().getZoomLevel();
    }

    /**
     * Set the zoom level of the map, with animation
     * The animation is always of the specified duration. Hence animating between big differences
     * will show a "faster" animation of the contents.
     * @param zoom target zoom level
     * @param duration the *complete* duration of the animation, not an "animation speed" as
     *                 as some of the osmdroid source code implies. If null, default value is used (500).
     */
    public void setZoomWithAnimation(final int zoom, final Long duration) {
        // a more recent implementation of setZoom requires its argument to be of type float.
        // this is for consistency with other methods which take parameters related to zoom
        // (e.g. minZoom) but have them declared as `int`s.
        mapController.zoomTo((float) zoom, duration);
    }

    /**
     * Set the zoom level of the map, with animation
     * The animation will longer or shorter, depending on how big the animation distance is.
     * @param zoom target zoom level
     * @param durationPerLevel animation length per zoom level
     */
    public void setZoomWithEvenAnimation(final int zoom, final Long durationPerLevel) {
        final Long duration = Math.abs( (long) getZoomLevel() - zoom ) * durationPerLevel;
        setZoomWithAnimation(zoom, duration);
    }

    public void setZoomWithEvenAnimation(final int zoom) {
        setZoomWithEvenAnimation(zoom, (long) 500);
    }

    /**
     * Pan the map so that `center` is in the center of the visible map.
     * @param center
     */
    public void setCenter(GeoPoint center) {
        mapController.setCenter(center);
    }

    public void panWithAnimationTo(GeoPoint newCenter) {
        mapController.animateTo(newCenter);
    }

}
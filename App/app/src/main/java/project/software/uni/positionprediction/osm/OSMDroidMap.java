package project.software.uni.positionprediction.osm;


import android.Manifest;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.util.PermissionManager;



/**
 * This class embodies a mapView that displays OpenStreetMap/Mapnik data via OSMDroid.
 * It exposes methods to programmatically navigate the mapView, as well as save and clear offline data.
 *
 * Note that zooming and panning at the same time is not supported yet. Calling both methods at the
 * same time will result in weird behaviour.
 *
 *
 *
 * Usage:
 *
 * o In an Activity:
 *
 *      mymap = new OSMDroidMap(this);  // this = context
 *      // Note that the actions in the constructor have to happen *before* setContentView is called in the
 *      // containing Activity.
 *      // [...]
 *      MapView mapView = (MapView) findViewById(R.id.map);
 *      GeoPoint center = new GeoPoint(48.856359, 2.290849);
 *      int zoom = 3;
 *      mymap.initMap(mapView, center, zoom)
 *
 *
 * o Call mymap.onPause() and mymap.onResume() in the Activity's onPause() and onResume() methods.
 *
 * o The corresponding layout component (given to the constructor of this class) would be sth like
 *      <org.osmdroid.views.MapView android:id="@+id/mapView"
 *        android:layout_width="fill_parent"
 *        android:layout_height="fill_parent" />
 */
public class OSMDroidMap implements LocationListener {

    public MapView mapView = null; // initalised by constructor
        // exposed for calling mapView.onResume() and mapView.onPause() in the activity.
    private IMapController mapController = null;
    private CacheManager cacheManager = null;
    private Context context = null;

    private Marker locationMarker;
    private LocationManager locationManager;
    private MyLocationNewOverlay locationOverlay = null;
    private CompassOverlay compassOverlay = null;

    // TODO: refresh tiles when switching from offline to inline
    // cf https://github.com/osmdroid/osmdroid/blob/ae026862fe4666ab6c8d037b9e2f8805233c8ebf/OpenStreetMapViewer/src/main/java/org/osmdroid/StarterMapActivity.java#L25

    public OSMDroidMap(Context ctx) {

        context = ctx;

        // load OSMDroid configuration
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // TODO: What happens if obtaining permission fails?
        PermissionManager.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.dialog_permission_storage_text, (AppCompatActivity) context);
        PermissionManager.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, R.string.dialog_permission_finelocation_text, (AppCompatActivity) context);
    }

    public void initMap(MapView view, GeoPoint center, final double zoom) {
        // I decided to only take a MapView here instead of taking the layout id and doing the cast
        // to `MapView` here because after all the mapping from view id to to a MapView object is not
        // the responsibility of this class.
        // sample usage: argument `view` could e.g. be given as `(MapView) findViewById(R.id.mapView)`;
        mapView = (MapView) view;
        mapController = mapView.getController();
        cacheManager = new CacheManager(mapView);

        // use Mapnik by default
        // TODO: other tile sources interesting?
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        // disable gray zoom buttons at bottom of map (enabled by default)
        mapView.setBuiltInZoomControls(false);


        setZoom(zoom);
        setCenter(center);

        //enableCompassOverlay();
        //enableRotationGestures();

        //enableCustomLocationMarker();
    }

    /**
     * Adds compass overlay to the map.
     * Note: This displays a separate graphic in the corner of the map, not the dot in the map
     * at the user's current position.
     */
    private void enableCompassOverlay() {
        CompassOverlay overlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context), mapView);
        overlay.enableCompass();
        // show a pointer arrow that indicates the device's real world orientation on the map
        // instead of a compass rose picture
        overlay.setPointerMode(true);
        mapView.getOverlays().add(overlay);
    }

    /**
     * Shows current location using a built-in marker
     * TODO: test this on an actual device, doesnt work in emulator?
     */
    private void enableLocationOverlay() {
        MyLocationNewOverlay overlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context),mapView);
        overlay.enableMyLocation();

        mapView.getOverlays().add(overlay);
    }


    private void enableCustomLocationMarker() {
        // TODO: write method that enables displaying of custom location marker, move this there
        locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        // cf onLocationChanged
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, this);
        Drawable myDrawable = this.context.getDrawable(R.drawable.ic_dashboard_black_24dp);
        locationMarker = new Marker(mapView);
        locationMarker.setIcon(myDrawable);
        locationMarker.setImage(myDrawable);
    }

    private void enableRotationGestures(){
        RotationGestureOverlay overlay = new RotationGestureOverlay(context, mapView);
        overlay.setEnabled(true);
        mapView.setMultiTouchControls(true);
        mapView.getOverlays().add(overlay);
    }

    /**
     * Map will always center on the user's location.
     * Manual panning is disabled.
     * For a nice implementation that additionally allows panning, see
     * https://github.com/osmdroid/osmdroid/blob/db1d2e54b44bc10c6b47c49df2a08f19664ae6f5/OpenStreetMapViewer/src/main/java/org/osmdroid/samplefragments/location/SampleFollowMe.java
     */
    private void enableFollowLocation() {
        if (locationOverlay != null) locationOverlay.enableFollowLocation();
    }

    private void disableFollowLocation() {
        if (locationOverlay != null) locationOverlay.disableFollowLocation();
    }

    /**
     * Pass-through onResume handler of the mapView to be called by the implementing Activity.
     * needed for compass, my location overlays, v6.0.0 and up
     */
    public void onResume() {
        if (mapView != null) mapView.onResume();
        // resume location, compass updates
        if (locationOverlay != null) locationOverlay.onResume();
        if (compassOverlay != null) compassOverlay.onResume();
    }

    /**
     * Pass-through onPause handler of the mapView to be called by the implementing Activity.
     * needed for compass, my location overlays, v6.0.0 and up
     */
    public void onPause() {
        if (mapView != null) mapView.onPause();
        // pause location, compass updates while activity is not active
        if (locationOverlay !=null) locationOverlay.onPause();
        if (compassOverlay !=null) compassOverlay.onPause();
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
     * Set the zoom level of the mapView, no animation
     * @param zoom sensible zoom levels for OSM/Mapnik: minimum 2, maximum 19
     */
    public void setZoom(final double zoom) {
        mapController.setZoom(zoom);
    }

    /**
     * Set the zoom level of the mapView, no animation
     * @param zoom sensible zoom levels for OSM/Mapnik: minimum 2, maximum 19
     */
    public void setZoom(final int zoom) {
        // a more recent implementation of setZoom requires its argument to be of type float.
        // this is for consistency with other methods which take parameters related to zoom
        // (e.g. minZoom) but have them declared as `int`s.
        mapController.setZoom((float) zoom);
    }

    private double getZoomLevel() {
        return mapView.getProjection().getZoomLevel();
    }

    /**
     * Set the zoom level of the mapView, with animation
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
     * Set the zoom level of the mapView, with animation
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
     * Pan the mapView so that `center` is in the center of the visible mapView.
     * @param center
     */
    public void setCenter(GeoPoint center) {
        mapController.setCenter(center);
    }

    public void panWithAnimationTo(GeoPoint newCenter) {
        mapController.animateTo(newCenter);
    }

    @Override
    public void onLocationChanged(Location location) {
        locationMarker.setPosition(new GeoPoint(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
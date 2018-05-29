package project.software.uni.positionprediction.osm;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions;
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.util.GeoUtils;
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
public class OSMDroidMap {

    public MapView mapView = null; // initalised by constructor
    // exposed for calling mapView.onResume() and mapView.onPause() in the activity.
    private IMapController mapController = null;
    private CacheManager cacheManager = null;
    private Context context = null;

    private Marker marker;
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
        // such as https://wiki.openstreetmap.org/wiki/Hike_%26_Bike_Map
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        // disable gray zoom buttons at bottom of map (enabled by default)
        mapView.setBuiltInZoomControls(false);


        setZoom(zoom);
        setCenter(center);

        enableCompassOverlay(); // works
        enableRotationGestures(); // works

        //enableLocationOverlay(); // works

        // enableFollowLocation(); // TODO

        Marker myMarker = createMarker(mapView, context.getDrawable(R.drawable.ic_home_black_24dp));
        placeMarker(mapView, myMarker, center);
        // Note that as of now, the marker has to have already been placed on the map with placeMarker()
        // this means we have to supply it with an initial position (or else we would have to rethink
        // what the placeMarker method is for).
        // TODO: not do that, check dynamically whether marker is already placed or not.
        enableCustomLocationMarker(myMarker);
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
     */
    private void enableLocationOverlay() {
        MyLocationNewOverlay overlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mapView);
        overlay.enableMyLocation();

        mapView.getOverlays().add(overlay);
    }


    /**
     * Create a marker for a map (but do not show it)
     * @param view
     * @param icon
     * @return
     */
    private Marker createMarker(MapView view, Drawable icon) {
        Marker myMarker = new Marker(mapView);
        myMarker.setIcon(icon);
        myMarker.setInfoWindow(null); // speech bubble on tap
        myMarker.setPanToView(false); // If set to true, when clicking the marker, the map will be centered on the marker position.
        return myMarker;
    }


    /**
     * Place a new marker on the map.
     *
     * For more methods cf https://github.com/osmdroid/osmdroid/blob/987bdea49a899f14844674a8faa19f74c648cc57/OpenStreetMapViewer/src/main/java/org/osmdroid/samplefragments/data/SampleMarker.java
     *  @param view The MapView
     * @param location Location of the Marker
     * @param marker Icon to be displayed for the marker
     */
    private void placeMarker(MapView view, Marker marker, GeoPoint location) {
        marker.setPosition(location);
        view.getOverlays().add(marker);
    }


    /**
     * Binds a marker to the user's current position.
     * When the device location changes, the marker location on the map also changes.
     * Note that as of now, the marker has to have already been placed on the map with placeMarker()
     * TODO: onResume(), does the location have to be explicitly updated?
     */
    private void enableCustomLocationMarker(final Marker marker) {
        PermissionManager.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, R.string.dialog_permission_finelocation_text, (AppCompatActivity) context);

        // create a locationManager that handles obtaining the location if there is none yet
        if (locationManager == null) locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);

        // we encapsulate the LocationListener here and not in a method of the class
        // because the callback content is specific to that marker.
        // TODO: At some point, we will most likely want to access the user's current location for
        // other purposes as well (such as centering the map around it).
        // Then we could either use Marker.getPosition() or make save the location in a field and
        // rewrite this.
        registerLocationUpdates(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                marker.setPosition(new GeoPoint(location.getLatitude(), location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                // cf https://developer.android.com/reference/android/location/LocationListener.html#onStatusChanged(java.lang.String,%20int,%20android.os.Bundle)
                // TODO: Hide marker?
            }

            @Override
            public void onProviderEnabled(String s) {
                // TODO: redisplay marker?
            }

            @Override
            public void onProviderDisabled(String s) {
                // TODO: hide marker?
            }
        });
    }

    /**
     * Update the device location and call the callback with the new location.
     * @param listener
     */
    @SuppressLint("MissingPermission") // TODO
    private void registerLocationUpdates(LocationListener listener) {
        Log.d("Location", "call to update location");
        // because receiving the first location might take a while,
        // we call a first update immediately with the last known location
        // cf https://developer.android.com/reference/android/location/LocationManager.html#requestLocationUpdates(java.lang.String,%20long,%20float,%20android.location.LocationListener)
        listener.onLocationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        //  register the current activity to be updated periodically by the named provider
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, listener);
    }


    /**
     * Draw a list of tracks (position records) as points connected by a line.
     * @param tracks
     */
    // TODO: this might return a "folder" overlay with the points and the polyline overlay
    public void drawTracks(List<IGeoPoint> tracks) {
        // TODO: pass styling in here as parameter
        showFastPoints(tracks);
        drawPolyLine(tracks);
    }

    private Polyline drawPolyLine(List<IGeoPoint> tracks) {
        Polyline line = new Polyline(); // a polyline is a kind of overlay
        List<GeoPoint> points = GeoUtils.castDownGeoPointList(tracks);
        line.setPoints(points); // no method chaining here because setPoints doesnt return the object...
        mapView.getOverlayManager().add(line);
        return line;
    }


    /**
     * Show a number of same-looking markers on the map in a fast way.
     * @param poss Has to merely implement IGeoPoint (marked or unmarked, ...)
     * @return The newly created overlay containing the points.
     * TODO: move styling out of this method.
     * cf https://github.com/osmdroid/osmdroid/wiki/Markers,-Lines-and-Polygons#fast-overlay
     * require IGeoPoint here because SimplePointTheme requires so.
     */
    private SimpleFastPointOverlay showFastPoints(List<IGeoPoint> poss) {
        SimplePointTheme theme = new SimplePointTheme(poss, false);
        SimpleFastPointOverlayOptions options = TrackingPointOverlayOptions
                // we subclass SimplePointOverlayOptions to be able to change the color
                // SimplePointOverlayOptions doesn't expose a setter for that.
                .getDefaultStyle()
                // has to be called first because the parent classes methods return the object in the
                // more general type
                .setPointColor("0088ff")
                // --
                .setAlgorithm(SimpleFastPointOverlayOptions.RenderingAlgorithm.MAXIMUM_OPTIMIZATION)
                .setRadius(10) // radios of circles to be drawn
                .setIsClickable(false) // true by default
                .setCellSize(15) // cf internal doc
                .setSymbol(SimpleFastPointOverlayOptions.Shape.CIRCLE)
                ;
        final SimpleFastPointOverlay overlay = new SimpleFastPointOverlay(theme, options);
        mapView.getOverlays().add(overlay);
        return overlay;
    }


    /*
    ===== MAP CONTROL =====
     */

    private void enableRotationGestures() {
        RotationGestureOverlay overlay = new RotationGestureOverlay(context, mapView);
        overlay.setEnabled(true);
        mapView.setMultiTouchControls(true);
        mapView.getOverlays().add(overlay);
    }

    /**
     * Map will always center on the user's location.
     * Manual panning is disabled.
     * For a nice implementation that additionally allows panning, see
     * TODO: Error handling?
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
        if (locationOverlay != null) {
            locationOverlay.onResume();

        }
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


}
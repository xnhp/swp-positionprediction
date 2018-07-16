package project.software.uni.positionprediction.osm;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.modules.SqlTileWriter;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions;
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme;

import java.util.List;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.datatypes.Locations;
import project.software.uni.positionprediction.util.GeoDataUtils;
import project.software.uni.positionprediction.util.PermissionManager;
import project.software.uni.positionprediction.visualisation.IVisualisationAdapter;

import static android.graphics.Color.argb;
import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;
import static project.software.uni.positionprediction.util.GeoDataUtils.LocationToGeoPoint;


/**
 * This class embodies a mapView that displays OpenStreetMap data via OSMDroid via a specified Tile Provider.
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

    public MapView mapView = null; // initialised by constructor
    // exposed for calling public methods from the activity
    private IMapController mapController = null;
    private CacheManager cacheManager = null;
    private Context context;

    private LocationManager locationManager;
    private MyLocationNewOverlay locationOverlay = null;
    private CompassOverlay compassOverlay = null;


    // TODO: refresh tiles when switching from offline to inline
    // cf https://github.com/osmdroid/osmdroid/blob/ae026862fe4666ab6c8d037b9e2f8805233c8ebf/OpenStreetMapViewer/src/main/java/org/osmdroid/StarterMapActivity.java#L25


    public OSMDroidMap(Context ctx) {

        context = ctx;

        // load OSMDroid configuration
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue("SP_1-2"); // todo: put this somewhere else

        // TODO: What happens if obtaining permission fails?
        // todo: need this here?
        PermissionManager.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.dialog_permission_storage_text, 0,(AppCompatActivity) context);
        PermissionManager.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, R.string.dialog_permission_finelocation_text, 0,(AppCompatActivity) context);
    }


    public void initMap(MapView view, GeoPoint center, final double zoom) {
        // I decided to only take a MapView here instead of taking the layout id and doing the cast
        // to `MapView` here because after all the mapping from view id to to a MapView object is not
        // the responsibility of this class.
        // sample usage: argument `view` could e.g. be given as `(MapView) findViewById(R.id.mapView)`;
        mapView = (MapView) view;
        mapController = mapView.getController();

        // this ensures that the garbage collection methods of *this* CacheManager
        //    don't remove any tiles saved by it.
        // initialise a CacheManager with a custom tile writer as additional argument
        // for *persistent* offline saving
        // cf https://github.com/osmdroid/osmdroid/wiki/Offline-Map-Tiles, look for "Tile Archives"
        SqlTileWriter noDelTilewriter = OSMCacheControl.getInstance(this.context).tileWriter;
        //tileWriter.setCleanupOnStart(false);
        cacheManager = new CacheManager(mapView, noDelTilewriter);

        // use Mapnik by default
        // TODO: other tile sources interesting?
        // such as https://wiki.openstreetmap.org/wiki/Hike_%26_Bike_Map
        // (!!) note that this calls for a clearing of the cache (cf cacheManager)
        // however, the interface implementation of IFileSystemCache given to the CacheManager
        // may override the `remove()` method.
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        // disable gray zoom buttons at bottom of map (enabled by default)
        mapView.setBuiltInZoomControls(false);




        setZoom(zoom);
        setCenter(center);

        // enableCompassOverlay(); // works but dont need
        enableRotationGestures(); // works

        enableLocationOverlay(); // works

        // enableFollowLocation();

        /*
        This is an alternative method of a location marker
        the marker and obtaining/updating the location is managed "manually",
        without using the osmdroid library.
        This potentially provides more flexibility
        locationMarker = createMarker(mapView, context.getDrawable(R.drawable.ic_menu_mylocation));
        placeMarker(mapView, locationMarker, center);
        // Note that as of now, the marker has to have already been placed on the map with placeMarker()
        // this means we have to supply it with an initial position (or else we would have to rethink
        // what the placeMarker method is for).
        // TODO: not do that, check dynamically whether marker is already placed or not.
        enableCustomLocationMarker(locationMarker);
        */
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
     * Shows current location of the user using osmdroid's LocationOverlay.
     * provides functionality like always centering the map around the location.
     */
    public void enableLocationOverlay() {
        // not my wording, part of osmdroid ¯\_(ツ)_/¯
        MyLocationNewOverlay overlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mapView);

        overlay.enableMyLocation();

        Bitmap bitmapMarker = BitmapFactory.decodeResource(
                context.getResources(),
                R.drawable.ic_menu_mylocation
        );
        overlay.setPersonIcon(bitmapMarker);

        // the "hotspot" is the exact position of the icon that is mapped
        // to the users current location.
        // assume it is the center of the icon (e.g. for crosshair icon)
        overlay.setPersonHotspot(bitmapMarker.getWidth() / 2, bitmapMarker.getHeight() / 2);

        // save reference for accessing it from other methods
        // (such as e.g.) toggling of following the location.
        this.locationOverlay = overlay;

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
     * Alternatively, osmdroid's locationOverlay can be used
     * Binds a marker to the user's current position.
     * When the device location changes, the marker location on the map also changes.
     * Note that as of now, the marker has to have already been placed on the map with placeMarker()
     * TODO: onResume(), does the location have to be explicitly updated?
     */
    private void enableCustomLocationMarker(final Marker marker) {
        PermissionManager.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, R.string.dialog_permission_finelocation_text, PermissionManager.PERMISSION_FINE_LOCATION, (AppCompatActivity) context);

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
                Log.d("location on map", "location changed to " + location);
                // TODO: error handling
                if (location != null) {
                    marker.setPosition(new GeoPoint(location.getLatitude(), location.getLongitude()));
                }
                Log.i("FloatingMapButtons", "followLocation is " + locationOverlay.isFollowLocationEnabled());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d("location on map", "status changed");
                // cf https://developer.android.com/reference/android/location/LocationListener.html#onStatusChanged(java.lang.String,%20int,%20android.os.Bundle)
                // TODO: Hide marker?
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d("location on map", "provider enabled");
                // TODO: redisplay marker?
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d("location on map", "provider disabled");
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
        Log.d("Location on map", "call to update location");
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
    // todo: obsolete
    // TODO: this might return a "folder" overlay with the points and the polyline overlay
    public void drawTracksUniform(List<GeoPoint> tracks, String lineColor, String pointColor) {
        // TODO: pass styling in here as parameter
        drawPolyLine(tracks, lineColor);
        drawPointsUniform(tracks, pointColor,10);
    }


    /**
     * Show a number of same-looking markers on the map in a fast way.
     * @return The newly created overlay containing the points.
     * cf https://github.com/osmdroid/osmdroid/wiki/Markers,-Lines-and-Polygons#fast-overlay
     * require IGeoPoint here because SimplePointTheme requires so.
     */

    public SimpleFastPointOverlay drawPointsUniform(List<GeoPoint> GeoPoints, String pointColor, int pointRadius) {
        List<IGeoPoint> iGeoPoints = GeoDataUtils.GeoPointsToIGeoPoints(GeoPoints);
        SimplePointTheme theme = new SimplePointTheme(iGeoPoints, false);
        SimpleFastPointOverlayOptions options = PointsOptions
                .getDefaultStyle()
                // has to be called first because the parent classes methods return the object in the
                // more general type
                .setPointColor(pointColor)
                .setAlgorithm(SimpleFastPointOverlayOptions.RenderingAlgorithm.MAXIMUM_OPTIMIZATION)
                .setRadius(pointRadius) // radios of circles to be drawn
                .setIsClickable(false) // true by default
                .setCellSize(15) // cf internal doc
                .setSymbol(SimpleFastPointOverlayOptions.Shape.CIRCLE)
                ;
        final SimpleFastPointOverlay overlay = new SimpleFastPointOverlay(theme, options);
        mapView.getOverlays().add(overlay);
        return overlay;
    }

    public Polyline drawPolyLine(List<GeoPoint> geoPoints, String lineColor) {
        Polyline polyline = new Polyline(); // a polyline is a kind of overlay
        polyline.setColor(Color.parseColor(lineColor));
        //List<GeoPoint> points = geoPoints;
        polyline.setPoints(geoPoints); // no method chaining here because setPoints doesnt return the object...
        mapView.getOverlayManager().add(polyline);
        return polyline;
    }


    public Polygon drawPolygon(List<GeoPoint> geoPoints, String lineColor, String fillColor, float opacity) {
        int fc = Color.parseColor(fillColor);
        int alpha = (int)(opacity * 255);
        int _fillColor = argb(alpha, red(fc), green(fc), blue(fc));
        Polygon polygon = new Polygon();
        polygon.setFillColor(_fillColor);
        polygon.setStrokeColor(Color.parseColor(lineColor));
        polygon.setPoints(geoPoints);

        //polygons supports holes too, points should be in a counter-clockwise order
        //List<List<GeoPoint>> holes = new ArrayList<>();
        //holes.add(geoPoints);
        //polygon.setHoles(holes);

        mapView.getOverlayManager().add(polygon);
        return polygon;
    }




    /*
    ===== MAP CONTROL =====
     */

    private void enableRotationGestures() {
        // TODO: deprecation warning, what else to use?
        RotationGestureOverlay overlay = new RotationGestureOverlay(context, mapView);
        overlay.setEnabled(true);
        mapView.setMultiTouchControls(true);
        mapView.getOverlays().add(overlay);
    }

    /**
     * Center map on user's location and keep it there until another
     * user interaction (panning, zooming etc)
     *
     * NOTE: contrary to osmdroid's documentation (cf MyLocationNewOverlay)
     * this DOES NOT disable manual panning. In fact, on panning the map
     * locationOverlay.isFollowLocationEnabled() is set to false again.
     * TODO: Error handling?
     */
    public void enableFollowLocation() {
        if (locationOverlay != null) {
            locationOverlay.enableFollowLocation();
        }
    }

    public void disableFollowLocation() {
        if (locationOverlay != null) locationOverlay.disableFollowLocation();
    }

    public boolean isFollowingLocation() {
        return locationOverlay.isFollowLocationEnabled();
    }

    /**
     * Pass-through onResume handler of the mapView to be called by the implementing Activity.
     * needed for compass, my location overlays, v6.0.0 and up
     */
    public void onResume() {
        if (mapView != null) {
            mapView.onResume();
            // in case e.g. the visualisation has changed
            mapView.invalidate();
        }
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

    // Calculate the correct zoom level for a given spread (in degrees) of elements that shall be visible
    public static int calculateZoomLevel(double spread) {
        int zoomLevel = 0;
        int correction = 10;
        if (spread < 0.0005) {
            zoomLevel = 19 - correction;
        } else if (spread < 0.001) {
            zoomLevel = 18 - correction;
        } else if (spread < 0.003) {
            zoomLevel = 17 - correction;
        } else if (spread < 0.005) {
            zoomLevel = 16 - correction;
        } else if (spread < 0.011) {
            zoomLevel = 15 - correction;
        } else if (spread < 0.022) {
            zoomLevel = 14 - correction;
        } else if (spread < 0.044) {
            zoomLevel = 13 - correction;
        } else if (spread < 0.088) {
            zoomLevel = 12 - correction;
        } else if (spread < 0.176) {
            zoomLevel = 11 - correction;
        } else if (spread < 0.352) {
            zoomLevel = 10 - correction;
        } else if (spread < 0.703) {
            zoomLevel = 9 - correction;
        } else if (spread < 1.406) {
            zoomLevel = 8 - correction;
        } else if (spread < 2.813) {
            zoomLevel = 7 - correction;
        } else if (spread < 5.625) {
            zoomLevel = 6 - correction;
        } else if (spread < 11.25) {
            zoomLevel = 5 - correction;
        } else if (spread < 22.5) {
            zoomLevel = 4 - correction;
        } else if (spread < 45) {
            zoomLevel = 3 - correction;
        } else if (spread < 90) {
            zoomLevel = 2 - correction;
        } else if (spread < 180) {
            zoomLevel = 1 - correction;
        }
        return zoomLevel;
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

    public void setMapCenter(Locations locs) {
        this.setCenter(LocationToGeoPoint(locs.getCenter()));
    }

    public void setMapZoom(Locations locs) {
        this.setZoom(OSMDroidMap.calculateZoomLevel(locs.getSpread()));
    }

    /**
     * Additionally checks if the boxes' bounds are far enough apart.
     * If so, it enlargens the BB by some predefined constant.
     * e.g. if all values are equal, osmdroid doesnt do anything at all.
     * ( `if (nextZoom == Double.MIN_VALUE) { return } ... `)
     *
     * IMPORTANT:
     * note that when `animate` is true, the following problem occurs
     * when the zoom distance is very far,
     * zoomToBoundingBox zooms to the wrong center
     *
     * if `animate` is false, it works perfectly fine, which
     * very much hints that this is an issue with osmdroid.
     *
     * the calculated bounding box passed into here
     * does not change for the same visualisation.
     * (our code about this seems to be correct)
     *
     * i am not the only one having this problem:
     * see https://github.com/osmdroid/osmdroid/issues/955
     * and this pull request that might solve it
     * https://github.com/osmdroid/osmdroid/pull/948
     * Theoretically, zoomToBoundingBox should have been fixed as per
     * https://github.com/osmdroid/osmdroid/pull/702
     *
     * I think this is already included in the distr of osmdroid
     * that is used here.
     *
     * triggering a setCenter afterwards with the center of bounding box does not
     * have any effect.
     *
     * doing this: https://github.com/osmdroid/osmdroid/issues/955#issuecomment-370509751
     * does not have any effect either.
     *
     * another thing to try would be to use postDelayed instead but, honestly,
     * zooming to some random place and then panning to the correct place,
     * each with animation is absolutely not what we want.
     */
    // having this here in the OSMDroidMap class seems convoluted but is
    // necessary since we call a method on the mapview.
    public void safeZoomToBoundingBox(IVisualisationAdapter visAdap, final BoundingBox boundingBox, final boolean animate, final int zoomPadding) {
        mapView.zoomToBoundingBox(
                visAdap.getSafeBoundingBox(boundingBox),
                animate,
                zoomPadding
        );
    }


}
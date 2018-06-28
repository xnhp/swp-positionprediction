package project.software.uni.positionprediction.util;

import android.content.Context;
import android.hardware.GeomagneticField;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * This class will provide means to get the direction
 * (bearing) relative to the users current position
 * AND bearing.
 *
 * Returns the angle the user has to turn i.o.t.
 * have the bird right in front of him.
 *
 */
public class BearingProvider implements LocationListener, OrientationListener {

    private Location targetLocation;
    private BearingListener listener;
    private Location userLocation;

    // the angle between the device's y axis and the *magnetic* north pole
    // in degrees.
    double userOrientation;


    public  void registerBearingUpdates(Context ctx,
                                        Location targetLocation,
                                        BearingListener listener) {
        this.targetLocation = targetLocation;
        this.listener = listener;

        // register location updates
        LocationProvider.registerLocationListener(ctx, this);

        // register orientation updates
        OrientationProvider op = new OrientationProvider(ctx);
        op.registerOrientationUpdates(this);
    }

    // location
    @Override
    public void onLocationChanged(Location location) {
        userLocation = location;
        updateBearing();
    }

    // location
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    // location
    @Override
    public void onProviderEnabled(String s) {
        // hand through
        listener.onProviderEnabled(s);
    }

    // location
    @Override
    public void onProviderDisabled(String s) {
        listener.onProviderDisabled(s);
    }

    /**
     * Listener attached to an OrientationProvider.
     * @param newOrientation
     */
    @Override
    public void onOrientationChanged(float newOrientation) {
        this.userOrientation = newOrientation;
        updateBearing();
    }


    /**
     * Recalculate the bearing based on the current values.
     */
    private void updateBearing() {
        listener.onBearingChanged(
                calculateBearing()
        );
    }


    private float calculateBearing() {
        // bearing of the line between the user location and
        // the target location in degrees east of *geographic* north
        float bearWrtGeoNorth = this.userLocation.bearingTo(this.targetLocation);

        // heading of the user relative to degrees east of
        // (magnetic) north pole.
        float headWrtMagnNorth = (float) this.userOrientation; // todo: cast okay?

        // declination is the angle between magnetic and geographic
        // north pole. cf http://www.magnetic-declination.com/what-is-magnetic-declination.php
        GeomagneticField geoField = new GeomagneticField(
                Double.valueOf(userLocation.getLatitude()).floatValue(),
                Double.valueOf(userLocation.getLongitude()).floatValue(),
                Double.valueOf(userLocation.getAltitude()).floatValue(),
                System.currentTimeMillis()
        );

        float bearWrtMagnNorth = bearWrtGeoNorth + geoField.getDeclination();

        // the angle between the line that of the gaze of the user looking forward
        // and the line between him and the target location
        float targetBearing = bearWrtMagnNorth - headWrtMagnNorth;

        // up until this, we were in a range of -180..180
        float targetBearing360 = normalizeDegree(targetBearing);

        Log.i("BearingProvider", "determined bearing:" + targetBearing360);

        return targetBearing360;
    }



    /**
     * Change angle from -180..180 to 0..360
     * @param value
     * @return
     */
    private float normalizeDegree(float value) {
        if (value < 0) return value + 360;
        return value;
    }

}

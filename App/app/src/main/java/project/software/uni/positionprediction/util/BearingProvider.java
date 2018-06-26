package project.software.uni.positionprediction.util;

import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import static android.content.Context.SENSOR_SERVICE;

/**
 * This class will provide means to get the direction
 * (bearing) relative to the users current position
 * AND bearing.
 *
 * Returns the angle the user has to turn i.o.t.
 * have the bird right in front of him.
 *
 */
public class BearingProvider implements SensorEventListener, LocationListener {

    private Location targetLocation;
    private BearingListener listener;
    private Location userLocation;

    // the angle between the device's y axis and the *magnetic* north pole
    // in degrees.
    double azimuth;


    public  void registerBearingUpdates(Context ctx,
                                              Location targetLocation,
                                              BearingListener listener) {
        this.targetLocation = targetLocation;
        this.listener = listener;

        // initialise compass
        SensorManager mSensorManager = (SensorManager) ctx.getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(
                this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL
        );

        registerLocationUpdates(ctx);
    }

    private  void registerLocationUpdates(Context ctx) {
        LocationProvider.registerLocationListener(ctx, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        userLocation = location;
        updateBearingByUserLocation();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        // hand through
        listener.onProviderEnabled(s);
    }

    @Override
    public void onProviderDisabled(String s) {
        listener.onProviderDisabled(s);
    }

    /**
     * Update the bearing after a change in user location
     */
    private void updateBearingByUserLocation() {
        listener.onBearingChanged(
                calculateBearing(this.userLocation, targetLocation)
        );
    }

    /**
     * Update the bearing after a change in target location
     */
    private void updateBearingByTargetLocation() {
        listener.onBearingChanged(
                calculateBearing(this.userLocation, targetLocation)
        );
    }

    private float calculateBearing(Location userLocation, Location targetLocation) {
        // bearing of the line between the user location and
        // the target location in degrees east of *geographic* north
        float bearWrtGeoNorth = userLocation.bearingTo(targetLocation);

        // heading of the user relative to degrees east of
        // (magnetic) north pole.
        float headWrtMagnNorth = (float) azimuth; // todo: cast okay?

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
     * Called when the compass (user heading w.r.t. north pole)
     * is updated
     * @param sensorEvent
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] inR = new float[16];
        float[] orientVals = new float[16];
        SensorManager.getOrientation(inR, orientVals);
        // angle of rotation about the -z axis. This value represents
        // the angle between the device's y axis and the magnetic north pole
        this.azimuth = Math.toDegrees(orientVals[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // noop
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

package project.software.uni.positionprediction.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Provides the current orientation/heading/facing
 * of the user.
 *
 * Getting the device orientation w.r.t to world coordinates
 * actually requires the result from two sensors: Accelerometer
 * and Magnetic Field
 *
 * todo: detach sensors onPause, reattach onResume
 * todo: make this static
 */
public class OrientationProvider implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelSensor;
    private Sensor magnSensor;

    private float[] lastAccelValue = new float[3];
    private float[] lastMagnValue = new float[3];

    // indicate whether there has been
    // something measured already.
    private boolean wasAccelSet = false;
    private boolean wasMagnSet = false;

    private float[] rotationMatrix = new float[9];
    // these are userOrientation (heading), pitch, roll.
    // we are only interested in userOrientation
    private float[] orientationVals = new float[3];

    // reference to the currently attached event listener
    OrientationListener listener = null;


    public OrientationProvider(Context ctx) {
        // get accelerometer and magnetometer sensors
        sensorManager = (SensorManager) ctx.getSystemService(SENSOR_SERVICE);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void registerOrientationUpdates(OrientationListener newListener) {
        this.listener = newListener;

        // registering sensor listeners only makes sense when we have
        // attached something to to react to it.
        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /* handle both sensor event changes at once */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == accelSensor) {
            System.arraycopy(event.values, 0, lastAccelValue, 0, event.values.length);
            wasAccelSet = true;
        } else if (event.sensor == magnSensor) {
            System.arraycopy(event.values, 0, lastMagnValue, 0, event.values.length);
            wasMagnSet = true;
        }
        if (wasAccelSet && wasMagnSet) {
            // get the rotation matrix that denotes the difference between the devices
            // and the worlds coordinate system
            // this is the identity matrix when the device is aligned with the world's
            // coordinate system, that is, when the device's X axis points toward East,
            // the Y axis points to the North Pole and the device is facing the sky.
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelValue, lastMagnValue);

            // Computes the device's orientation (w.r.t to world coordinates) based on the rotation matrix.
            SensorManager.getOrientation(rotationMatrix, orientationVals);

            // 180 when device is upstanding
            // range of orientiationVals[0] is -pi..+pi, hence we add pi to
            // get a range from 0..2pi (radians)
            float orientationDegrees = (float) Math.toDegrees(orientationVals[0] + Math.PI);


            listener.onOrientationChanged(orientationDegrees);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // noop
    }
}

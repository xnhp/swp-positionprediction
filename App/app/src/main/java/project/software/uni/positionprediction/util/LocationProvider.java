package project.software.uni.positionprediction.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;

import project.software.uni.positionprediction.R;

/**
 * This class will be the central means for accessing
 * device/user location.
 */
public class LocationProvider {

    static LocationManager lm;

    @SuppressLint("MissingPermission") // we do take care of permissions
    public static void registerLocationListener(Context ctx, LocationListener listener) {
        PermissionManager.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                R.string.dialog_permission_finelocation_text,
                PermissionManager.PERMISSION_FINE_LOCATION,
                (AppCompatActivity) ctx);
        lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        // because receiving the first location update might take a little bit,
        // we call onLocationChanged immediately with the last known location.
        Location lastLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (lastLocation != null) {
            lastLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        // this probably makes some later null checks obsolete
        if (lastLocation != null) {
            listener.onLocationChanged(lm.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        }

        // attach listener
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, listener);

    }

    public static void clearLocationListener(LocationListener listener) {
        if (lm == null) return;
        lm.removeUpdates(listener);
    }
}

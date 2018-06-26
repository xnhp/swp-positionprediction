package project.software.uni.positionprediction.util;

/**
 * Called when the bearing to a target location
 * is updated (user position or target location changes)
 */
public interface BearingListener {
    void onBearingChanged(float newBearing);
    void onProviderEnabled(String s);
    void onProviderDisabled(String s);
}

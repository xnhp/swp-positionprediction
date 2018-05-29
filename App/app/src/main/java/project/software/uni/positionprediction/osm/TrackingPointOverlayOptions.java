package project.software.uni.positionprediction.osm;

import android.graphics.Color;

import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions;

public class TrackingPointOverlayOptions extends SimpleFastPointOverlayOptions {

    public static TrackingPointOverlayOptions getDefaultStyle() {
        return new TrackingPointOverlayOptions();
    }

    public TrackingPointOverlayOptions setPointColor(String col) {
        this.mPointStyle.setColor(Color.parseColor("#0088ff"));
        return this;
    }


}

package project.software.uni.positionprediction.osm;

import android.graphics.Color;

import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions;

public class PointsOptions extends SimpleFastPointOverlayOptions {


    public static PointsOptions getDefaultStyle() {
        return new PointsOptions();
    }

    public PointsOptions setPointColor(String col) {
        this.mPointStyle.setColor(Color.parseColor(col));
        return this;
    }


}

package project.software.uni.positionprediction.osm;

import android.support.annotation.NonNull;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;

import java.util.ArrayList;
import java.util.Iterator;

class OSMStyledPointList implements SimpleFastPointOverlay.PointAdapter{

    ArrayList<IGeoPoint> pts = new ArrayList<>();

    // apparently StyledLabelledGeoPoint fits into IGeoPoint

    @Override
    public int size() {
        return pts.size();
    }

    @Override
    public IGeoPoint get(int i) {
        return (IGeoPoint) pts.get(i);
    }

    @Override
    public boolean isLabelled() {
        return false;
    }

    @Override
    public boolean isStyled() {
        return true;
    }

    @NonNull
    @Override
    public Iterator<IGeoPoint> iterator() {
        return pts.iterator();
    }
}
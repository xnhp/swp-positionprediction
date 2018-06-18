package project.software.uni.positionprediction.datatype;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TrackingPoints {

    private List<TrackingPoint> trackingpoints;

    public TrackingPoints(List<TrackingPoint> trackingpoints) {
        this.trackingpoints = trackingpoints;
    }

    public TrackingPoints add(TrackingPoint point) {
        this.trackingpoints.add(point);
        return this;
    }

    public TrackingPoint get(int i) {
        return this.trackingpoints.get(i);
    }

    public TrackingPoints addAll(TrackingPoints points) {
        this.trackingpoints.addAll(points.trackingpoints);
        return this;
    }

    public Iterator<TrackingPoint> iterator() {
        return this.trackingpoints.iterator();
    }

    public int getLength() {
        return this.trackingpoints.size();
    }

}

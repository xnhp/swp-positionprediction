package project.software.uni.positionprediction.visualisation_new;

import org.osmdroid.util.BoundingBox;

public class CloudVis extends Visualisation {

    Points points;
    Polygon hull;

    @Override
    public BoundingBox getBoundingBox() {
        return points.getBoundingBox().concat(hull.getBoundingBox());
    }
}

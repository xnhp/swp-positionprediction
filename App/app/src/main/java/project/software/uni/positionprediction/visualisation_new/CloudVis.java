package project.software.uni.positionprediction.visualisation_new;

import org.osmdroid.util.BoundingBox;

public class CloudVis extends Visualisation {

    public Points points;
    public Polygon hull;

    @Override
    public BoundingBox getBoundingBox() {
        BoundingBox bbox = null;
        if(points != null) bbox = points.getBoundingBox();
        if(hull != null) bbox = bbox.concat(hull.getBoundingBox());
        return bbox;
    }
}

package project.software.uni.positionprediction.visualisation;

import org.osmdroid.util.BoundingBox;

import project.software.uni.positionprediction.datatypes.Locations;

public class CloudVis extends Visualisation {

    public Points points; // also contains the styling of the points
    public Polygon hull; // points denoting the hull boundaries

    public CloudVis(Points points){
        this.points = points;
    }

    public void setHull(Locations locs, String hullLineCol, String hullFillCol, float hullOpacity) {
        this.hull = new Polygon(locs, hullLineCol, hullFillCol, hullOpacity);
    }

    @Override
    public BoundingBox getBoundingBox() {
        BoundingBox bbox = null;
        if(points != null) bbox = points.getBoundingBox();
        if(hull != null) bbox = bbox.concat(hull.getBoundingBox());
        return bbox;
    }

    public boolean hasHull(){
        if(hull == null) return false;
        return true;
    }

    public Polygon getHull() {
        return hull;
    }

    public Points getPoints() {
        return points;
    }
}
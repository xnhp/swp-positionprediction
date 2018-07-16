package project.software.uni.positionprediction.visualisation;

import org.osmdroid.util.BoundingBox;

import project.software.uni.positionprediction.datatypes.Locations;
import project.software.uni.positionprediction.visualisation.Points;
import project.software.uni.positionprediction.visualisation.Polygon;
import project.software.uni.positionprediction.visualisation.Visualisation;

public class CloudVis extends Visualisation {

    public Points points;
    public Polygon hull;

    public CloudVis(Points points){
        this.points = points;
    }

    public void setHull(Locations locs) {
        this.hull = new Polygon(locs);
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
package project.software.uni.positionprediction.visualisation_new;

import org.osmdroid.util.BoundingBox;

public class TrajectoryVis extends Visualisation {

    private Polyline line;
    private Polygon funnel;

    public TrajectoryVis(Polyline line){
        this.line = line;
    }

    public void setFunnel(Polygon funnel) {
        this.funnel = funnel;
    }

    public boolean hasFunnel(){
        if(funnel == null) return false;
        return true;
    }

    @Override
    public BoundingBox getBoundingBox() {
<<<<<<< HEAD
        BoundingBox bb;
        if (funnel != null) {
            bb = line.getBoundingBox().concat(funnel.getBoundingBox());
        } else {
            bb = line.getBoundingBox();
        }
        return bb;
=======
        BoundingBox bbox = null;
        if(line != null) bbox = line.getBoundingBox();
        if(funnel != null) bbox = bbox.concat(funnel.getBoundingBox());
        return bbox;
>>>>>>> 15d141bc13c5a7f9765735ec9b87aa3c319c41bd
    }

    public Polyline getLine() {
        return line;
    }
}

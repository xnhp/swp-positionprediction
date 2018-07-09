package project.software.uni.positionprediction.visualisation;

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
        BoundingBox bb;
        if (funnel != null) {
            bb = line.getBoundingBox().concat(funnel.getBoundingBox());
        } else {
            bb = line.getBoundingBox();
        }
        return bb;

        // Commit second option...
       /** BoundingBox bbox = null;
        if(line != null) bbox = line.getBoundingBox();
        if(funnel != null) bbox = bbox.concat(funnel.getBoundingBox());
        return bbox; */
    }

    public Polyline getLine() {
        return line;
    }
    public Polygon getFunnel() {return funnel;}
}

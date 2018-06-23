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

    @Override
    public BoundingBox getBoundingBox() {
        BoundingBox bb;
        if (funnel != null) {
            bb = line.getBoundingBox().concat(funnel.getBoundingBox());
        } else {
            bb = line.getBoundingBox();
        }
        return bb;
    }

    public Polyline getLine() {
        return line;
    }
}

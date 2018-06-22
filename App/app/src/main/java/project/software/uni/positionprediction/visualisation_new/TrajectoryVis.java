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
        return line.getBoundingBox().concat(funnel.getBoundingBox());
    }

    public Polyline getLine() {
        return line;
    }
}

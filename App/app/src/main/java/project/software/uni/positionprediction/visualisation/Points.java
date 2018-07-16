package project.software.uni.positionprediction.visualisation;

import java.util.ArrayList;
import java.util.Iterator;

import project.software.uni.positionprediction.datatypes.Location;
import project.software.uni.positionprediction.datatypes.Locations;

public class Points extends Geometry {

    public final ArrayList<StyledPoint> styledPoints = new ArrayList<>();

    public Points(Locations pts, String pointColor, int pointRadius) {
        super(pts);
        makeStyledObjects(pointColor, pointRadius);
    }


    // todo: rewrite
    private void makeStyledObjects(String pointColor, int pointRadius) {
        Iterator<Location> it = locations.iterator();
        Location prev = locations.get(0);
        while(it.hasNext()) {
            Location next = it.next();
            this.styledPoints.add(new StyledPoint(next, pointColor, pointRadius));
        }
    }

}

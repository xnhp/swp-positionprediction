package project.software.uni.positionprediction.visualisation;

import project.software.uni.positionprediction.datatype.Location;
import project.software.uni.positionprediction.datatype.Location3D;

public class StyledLineSegment {
    public final Location start;
    public final Location end;
    public final String lineColor;

    public StyledLineSegment(Location start, Location end, String lineColor) {
        this.start = start;
        this.end = end;
        this.lineColor = lineColor;
    }
}

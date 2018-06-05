package project.software.uni.positionprediction.visualisation;

import project.software.uni.positionprediction.datatype.Location3D;

public class StyledLineSegment {
    public final Location3D start;
    public final Location3D end;
    public final String lineColor;

    public StyledLineSegment(Location3D start, Location3D end, String lineColor) {
        this.start = start;
        this.end = end;
        this.lineColor = lineColor;
    }
}

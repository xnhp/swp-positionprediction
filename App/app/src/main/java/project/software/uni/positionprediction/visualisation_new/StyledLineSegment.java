package project.software.uni.positionprediction.visualisation_new;

import project.software.uni.positionprediction.datatypes_new.Location;

public class StyledLineSegment extends Primitive{
    public final Location start;
    public final Location end;
    public final String lineColor;

    public StyledLineSegment(Location start, Location end, String lineColor) {
        this.start = start;
        this.end = end;
        this.lineColor = lineColor;
    }
}

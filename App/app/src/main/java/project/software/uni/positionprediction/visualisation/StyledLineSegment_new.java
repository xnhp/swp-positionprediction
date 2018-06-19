package project.software.uni.positionprediction.visualisation;

import project.software.uni.positionprediction.datatypes_new.Location;

public class StyledLineSegment_new {
    public final Location start;
    public final Location end;
    public final String lineColor;

    public StyledLineSegment_new(Location start, Location end, String lineColor) {
        this.start = start;
        this.end = end;
        this.lineColor = lineColor;
    }
}

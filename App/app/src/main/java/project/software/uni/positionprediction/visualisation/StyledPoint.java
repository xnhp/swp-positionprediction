package project.software.uni.positionprediction.visualisation;

import project.software.uni.positionprediction.datatype.Location;
import project.software.uni.positionprediction.datatype.Location3D; /**
 * Holds location and styling data for a single point on the map.
 * Can be used in single, multiple trajectories or other places.
 */
public class StyledPoint {
    public final Location location;
    public final String pointColor;
    public final int pointRadius;

    public StyledPoint(Location loc, String pointColor, int radius) {
        this.location = loc;
        this.pointColor = pointColor;
        this.pointRadius = radius;
    }
}

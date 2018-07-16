package project.software.uni.positionprediction.visualisation;

public class PredCloudStyle extends TrajectoryStyle {

    static public String pointCol = "#ff0077"; // pink
    static public String lineCol = "#ff0077"; // pink
    static public int pointRadius = 20;

    // the line connecting this trajectory to a predecessor
    // trajectory (e.g. past tracking data)
    static public String connectingLineColor = "#f7f300"; // yellow

    // funnel style
    static public String hullLineCol = "#ff0000"; // red
    static public String hullFillCol = "#ff0000"; // red
    static public float hullOpacity = 0.5f;
}

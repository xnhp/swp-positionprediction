package project.software.uni.positionprediction.visualisation;

public class PredTrajectoryStyle extends TrajectoryStyle {

    static public String pointCol = "#ff0077"; // pink
    static public String lineCol = "#e28a16"; // orange
    static public int pointRadius = 20;

    // the line connecting this trajectory to a predecessor
    // trajectory (e.g. past tracking data)
    static public String connectingLineColor = "#f7f300"; // yellow

    // funnel style
    static public String funnelLineCol = "#ff0000";
    static public String funnelFillCol = "#ff0000";
    static public float funnelOpacity = 0.5f;
}

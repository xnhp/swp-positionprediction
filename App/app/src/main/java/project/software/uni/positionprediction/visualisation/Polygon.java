package project.software.uni.positionprediction.visualisation;

import project.software.uni.positionprediction.datatypes.Locations;

public class Polygon extends Geometry {

    public String outlineColor;
    public String fillColor;
    public float fillOpacity;

    public Polygon(Locations locs, String hullLineCol, String hullFillCol, float hullOpacity) {
        super(locs);
        this.outlineColor = hullLineCol;
        this.fillColor = hullFillCol;
        this.fillOpacity = hullOpacity;
    }
}
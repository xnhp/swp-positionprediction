package project.software.uni.positionprediction.visualisation;

import project.software.uni.positionprediction.datatypes.Locations;

public class Funnel extends Polygon {

    public Funnel(Locations locations, String funnelFillCol, String funnelLineCol, float funnelOpacity) {
        super(locations, funnelLineCol, funnelFillCol, funnelOpacity);
    }
}

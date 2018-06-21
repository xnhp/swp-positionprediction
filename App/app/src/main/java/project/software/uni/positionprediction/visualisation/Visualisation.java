package project.software.uni.positionprediction.visualisation;

import org.osmdroid.util.BoundingBox;

public abstract class Visualisation {

    // the geometries are implemented individually for each subtype

    private  BoundingBox boundingBox;
    public abstract BoundingBox getBoundingBox();

}

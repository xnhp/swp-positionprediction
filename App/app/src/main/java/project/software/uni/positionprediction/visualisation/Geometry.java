package project.software.uni.positionprediction.visualisation;


import org.osmdroid.util.BoundingBox;

import java.util.Iterator;

import project.software.uni.positionprediction.datatypes.Location;
import project.software.uni.positionprediction.datatypes.Locations;

/**
 * This class represents an abstract "visualisation" of a prediction result.
 * Image it saying "a point here"/"a box there".
 * This is decoupled from actually drawing it on a map.
 * This will be passed to the actual map instance (e.g. of type OSMDroidMap, or, to be more precise
 * the corresponding mapView) (or Cesium) which takes care of calling the correct methods for actually drawing it.
 */
public abstract class Geometry {

    // Data
    public Locations locations;

    // Constructors

    public Geometry(){}
    public Geometry(Locations locations){
        this.locations = locations;
    }

    public Iterator<Location> getLocationsIterator() {
        return locations.iterator();
    }

    BoundingBox getBoundingBox(){
        return locations.getBoundingBox();
    }

}

package project.software.uni.positionprediction.visualisation;


import java.util.ArrayList;
import java.util.Iterator;

import project.software.uni.positionprediction.datatype.Location;
import project.software.uni.positionprediction.datatype.Location3D;
import project.software.uni.positionprediction.datatype.Locations;

/**
 * This class represents an abstract "visualisation" of a prediction result.
 * Image it saying "a point here"/"a box there".
 * This is decoupled from actually drawing it on a map.
 * This will be passed to the actual map instance (e.g. of type OSMDroidMap, or, to be more precise
 * the corresponding mapView) (or Cesium) which takes care of calling the correct methods for actually drawing it.
 */
public class Visualisation {

    // Data
    // todo: remove, only use styledPoints TJ: Why remove?!
    public Locations locations;

    // Constructor
    public Visualisation(Locations locations){
        this.locations = locations;
    }

    public Iterator<Location> iterator() {
        return locations.iterator();
    }

}

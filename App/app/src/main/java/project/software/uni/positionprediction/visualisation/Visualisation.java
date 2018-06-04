package project.software.uni.positionprediction.visualisation;


import project.software.uni.positionprediction.datatype.Locations2D;

/**
 * This class represents an abstract "visualisation" of a prediction result.
 * Image it saying "a point here"/"a box there".
 * This is decoupled from actually drawing it on a map.
 * This will be passed to the actual map instance (e.g. of type OSMDroidMap, or, to be more precise
 * the corresponding mapView) (or Cesium) which takes care of calling the correct methods for actually drawing it.
 */
public class Visualisation {

    // Data
    public Locations2D locations;

    // Constructor
    public Visualisation(Locations2D locations){
        this.locations = locations;
    }

}

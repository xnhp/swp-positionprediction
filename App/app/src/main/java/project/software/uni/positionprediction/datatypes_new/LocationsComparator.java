package project.software.uni.positionprediction.datatypes_new;

import java.util.Comparator;

import project.software.uni.positionprediction.util.Dimension;

public class LocationsComparator implements Comparator<Location> {
    Dimension dim;
    public LocationsComparator(Dimension dim){
        this.dim = dim;
    };

    public final int compare(Location locA, Location locB){return 0;}

    public final int compare(Location locA, Location locB, Dimension dim){
        double a = locA.getDimension(dim);
        double b = locB.getDimension(dim);
        if(a < b){
            return -1;
        } else if(a == b){
            return 0;
        } else {
            return 1;
        }
    }
}

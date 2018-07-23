package project.software.uni.positionprediction.datatypes;

import java.util.Comparator;

public class LocationsComparator implements Comparator<Location> {
    EDimension dim;
    public LocationsComparator(EDimension dim){
        this.dim = dim;
    }

    public final int compare(Location locA, Location locB){
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

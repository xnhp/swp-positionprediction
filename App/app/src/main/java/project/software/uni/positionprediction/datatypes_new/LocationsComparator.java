package project.software.uni.positionprediction.datatypes_new;

import java.util.Comparator;

public class LocationsComparator implements Comparator<Location> {
    EDimension dim;
    public LocationsComparator(EDimension dim){
        this.dim = dim;
    };

    public final int compare(Location locA, Location locB){return 0;}

    public final int compare(Location locA, Location locB, EDimension dim){
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

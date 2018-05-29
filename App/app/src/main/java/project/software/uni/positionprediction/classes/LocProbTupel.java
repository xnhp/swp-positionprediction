package project.software.uni.positionprediction.classes;

import android.location.Location;


/**
 * Class to define Tupel <Location, Probability> to return a List of those Tupels in other methods
 */
public class LocProbTupel {

    // Class variables
    private Location loc;
    private double prob;

    // Constructor
    public LocProbTupel(Location loc, double prob){
        this.loc = loc;
        this.prob = prob;
    }

    // Getter and Setter
    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public double getProb() {
        return prob;
    }

    public void setProb(double prob) {
        this.prob = prob;
    }
}

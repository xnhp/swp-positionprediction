package project.software.uni.positionprediction.datatypes_new;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single trajectory (ordered sequence of points)
 */
public class Trajectory extends Shape {

    private boolean hasFunnel = false;
    public Trajectory() {
        super();
    }
    public Trajectory(Locations locs) {
        super(locs);
    }
    public Trajectory(Locations locs, boolean has_funnel) {
        super(locs);
        if (has_funnel){
            if(locs.haveValues()) {
                this.hasFunnel = true;
            } else {
                throw new InvalidParameterException("Locations must have values");
            }
        }
    }

    public boolean hasFunnel() {
        return hasFunnel;
    }

    public Locations calculateFunnelCoords() {
        Locations coords_right = new Locations();
        Locations coords_left = new Locations();
        for(Location loc : locations) {
            if (loc instanceof LocationWithValue) {
                if (((LocationWithValue) loc).getValue() instanceof Number) {
                    Location right = new Location();
                    coords_right.add(right);
                    Location left = new Location();
                    coords_left.add(left);
                    // todo (Sebastian): calculate coords for funnel
                }
            }
        }
        //List<Integer> aList = new ArrayList<>();

        // assemble polygon coordinates clockwise
        return new Locations(Collection.add(coords_left, coords_right.reverse()));

    }
}


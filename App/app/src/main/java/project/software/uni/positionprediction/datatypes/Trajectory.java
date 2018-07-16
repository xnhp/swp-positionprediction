package project.software.uni.positionprediction.datatypes;

import android.util.Log;

import java.security.InvalidParameterException;

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

    public boolean hasFunnel(){
        if(locations.size() > 0
                && locations.get(0) instanceof LocationWithValue
                && ((LocationWithValue) locations.get(0)).getValue() instanceof Number
                //&& (double)((LocationWithValue) locations.get(0)).getValue() != 0
                ){
            return true;
        }
        return false;
    }

    public Locations calculateFunnelSegment(
            LocationWithValue<Double> p0,
            LocationWithValue<Double> p1,
            LocationWithValue<Double> p2)
    {
        Location v = p1.getVectorFrom(p0);
        Location w = p2.getVectorFrom(p1);

        // Log.i("osm adapter", "v has alt " + v.hasAltitude()); // returns false
        // Log.i("osm adapter", "w has alt " + w.hasAltitude()); // returns false

        double angle1 = v.getAngle(w); // throws RuntimeException
        double angle2;
        Location vector;

        // calculate LEFT vector
        /*if (angle1 <= 180)// left turn
        {
        */
            angle2 = angle1 / 2;
            vector = v.rotate(180 - angle2);
        /*} else // right turn
        {
            angle2 = (360 - angle1) / 2;
            vector = v.rotate(angle2);
        }*/

        vector = vector.setLength(p1.getValue());
        //vectors.add(vector);


        Location left = p1.add(vector);
        Location right = p1.add(vector.multiply(-1));

        //Log.i("osm adapter", "left " + left.toString());
        //Log.i("osm adapter", "right " + right.toString());

        Locations result = new Locations();
        result.add(left);
        result.add(right);
        return result;
    }


    public Locations calculateLastFunnelSegment(
            LocationWithValue<Double> p0,
            LocationWithValue<Double> p1
    ){
        Location v = p1.getVectorFrom(p0);

        Location vector = v.rotate(90);

        vector = vector.setLength(p1.getValue());

        Location left = p1.add(vector);
        Location right = p1.add(vector.multiply(-1));

        Locations result = new Locations();
        result.add(left);
        result.add(right);
        return result;

        //Log.i("osm adapter", "last left " + left.toString());
        //Log.i("osm adapter", "last right " + right.toString());
    }


    public Locations calculateFunnelCoords(Location lastPastPoint) {

        Locations locs_left = new Locations();
        Locations locs_right = new Locations();

        //Locations vectors = new Locations();

        if (locations.get(0) instanceof LocationWithValue) {
            if (((LocationWithValue) locations.get(0)).getValue() instanceof Number) {
                if(locations.size() > 1) {
                    Log.i("Trajectory", ">1 locations");
                    int last = locations.size()-1;
                    Log.i("Trajectory", "last = " + last);
                    // The first segment (tip of the funnel)
                    LocationWithValue<Double> p0 = new LocationWithValue(lastPastPoint,0);
                    LocationWithValue<Double> p1 = (LocationWithValue<Double>) locations.get(0);
                    LocationWithValue<Double> p2 = (LocationWithValue<Double>) locations.get(1);

                    Locations funnelSegment = calculateFunnelSegment(p0, p1, p2);

                    Location left = funnelSegment.get(0);
                    Location right = funnelSegment.get(1);

                    locs_left.add(lastPastPoint);
                    locs_left.add(left);
                    locs_right.add(right);

                    for (int i = 1; i < last; i++) {

                        p0 = (LocationWithValue<Double>) locations.get(i - 1);
                        p1 = (LocationWithValue<Double>) locations.get(i);
                        p2 = (LocationWithValue<Double>) locations.get(i + 1);

                        funnelSegment = calculateFunnelSegment(p0, p1, p2);

                        left = funnelSegment.get(0);
                        right = funnelSegment.get(1);

                        locs_left.add(left);
                        locs_right.add(right);

                    }

                    // calculate points for last point with 90 degree angle
                    p0 = (LocationWithValue<Double>) locations.get(last - 1);
                    p1 = (LocationWithValue<Double>) locations.get(last);

                    funnelSegment = calculateLastFunnelSegment(p0, p1);

                    left = funnelSegment.get(0);
                    right = funnelSegment.get(1);

                    locs_left.add(left);
                    locs_right.add(right);
                } else if(locations.size() == 1){
                    Log.i("Trajectory", "1 location");

                    LocationWithValue<Double> p0 = new LocationWithValue(lastPastPoint,0);
                    LocationWithValue<Double> p1 = (LocationWithValue<Double>) locations.get(0);

                    Locations funnelSegment = calculateLastFunnelSegment(p0, p1);

                    Location left = funnelSegment.get(0);
                    Location right = funnelSegment.get(1);

                    locs_left.add(lastPastPoint);
                    locs_left.add(left);
                    locs_right.add(right);

                }

            }
        }

        // assemble polygon coordinates clockwise
        Locations funnelLocs = new Locations(Collection.add(locs_left, locs_right.reverse()));

        return funnelLocs;

    }

    /*
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
    */
}



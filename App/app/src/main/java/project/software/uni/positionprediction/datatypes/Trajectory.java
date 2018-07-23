package project.software.uni.positionprediction.datatypes;

import android.util.Log;

import java.security.InvalidParameterException;

/**
 * Represents a single trajectory (ordered sequence of points)
 */
public class Trajectory extends Shape {

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
                boolean hasFunnel = true;
            } else {
                throw new InvalidParameterException("Locations must have values");
            }
        }
    }

    public boolean hasFunnel(){
        return locations.size() > 0
                && locations.get(0) instanceof LocationWithValue
                && ((LocationWithValue) locations.get(0)).getValue() instanceof Number;
    }

    public Locations calculateFunnelSegment(
            LocationWithValue<Double> p0,
            LocationWithValue<Double> p1,
            LocationWithValue<Double> p2)
    {

        Vector3 v0 = Vector3.fromLocation(p0);
        Vector3 v1 = Vector3.fromLocation(p1);
        Vector3 v2 = Vector3.fromLocation(p2);

        Vector3 v = Vector3.sub(v1, v0);
        Vector3 w = Vector3.sub(v2, v1);

        double angle = Vector3.getAngle(v, w);

        Vector3 vector;

        vector = v.rotate(Math.PI/2 + (angle / 2.0));
        vector.setLength(0.05 * p1.getValue());

        Location left = Vector3.add(v1, vector).toLocation();
        Location right = Vector3.sub(v1, vector).toLocation();



        Locations result = new Locations();
        result.add(left);
        result.add(right);
        return result;
    }


    public Locations calculateLastFunnelSegment(
            LocationWithValue<Double> p0,
            LocationWithValue<Double> p1
    ){
        Vector3 v0 = Vector3.fromLocation(p0);
        Vector3 v1 = Vector3.fromLocation(p1);
        Vector3 v = Vector3.sub(v1, v0);


        Vector3 vector = v.rotate(Math.PI / 2.0);

        vector.setLength(0.05 * p1.getValue());

        Location left = Vector3.add(v1, vector).toLocation();
        Location right = Vector3.sub(v1, vector).toLocation();

        Locations result = new Locations();
        result.add(left);
        result.add(right);
        return result;

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

        return new Locations(Collection.add(locs_left, locs_right.reverse()));

    }

}



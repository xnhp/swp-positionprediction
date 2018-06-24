package project.software.uni.positionprediction.datatypes_new;

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


}
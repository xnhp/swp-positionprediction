package project.software.uni.positionprediction.datatypes;

/**
 * Represents a single cloud
 */
public class Cloud extends Shape {

    private Locations hull;

    public Cloud() {
        super();
    }
    public Cloud(Locations locs) {
        super(locs);
        hull = calculateHull();
    }

    // todo: This is a dummy!
    private Locations calculateHull(){
        return locations;
    }
}

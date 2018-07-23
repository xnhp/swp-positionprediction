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

    public Cloud(Locations points, Locations hull) {
        super(points);
        this.hull = hull;
    }

    private Locations calculateHull(){
        return locations;
    }

    public boolean hasHull() {
        return hull != null;
    }

    public Locations getHull() {
        return hull;
    }
}

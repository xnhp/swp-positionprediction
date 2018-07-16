package project.software.uni.positionprediction.datatypes;

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

    // todo: This is a dummy!
    private Locations calculateHull(){
        return locations;
    }

    public boolean hasHull() {
        if(hull == null) return false;
        return true;
    }

    public Locations getHull() {
        return hull;
    }
}
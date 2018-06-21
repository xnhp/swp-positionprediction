package project.software.uni.positionprediction.datatypes_new;

public class Shape {

    protected Locations locations = new Locations();

    public Shape(){}

    public Shape(Locations locations){
        this.locations = locations;
    }

    public void setLocations(Locations locations) {
        this.locations = locations;
    }

    public Locations getLocations() {
        return locations;
    }

    public int size(){
        return locations.size();
    }

    public Location getLocation(int index){
        return locations.get(index);
    }

    public void addLocation(Location loc) {
        locations.add(loc);
    }
}

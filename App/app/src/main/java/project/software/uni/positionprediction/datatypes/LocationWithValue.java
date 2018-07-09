package project.software.uni.positionprediction.datatypes;

public class LocationWithValue<T> extends Location {

    private T value;

    public LocationWithValue(){
        super();
    }

    public LocationWithValue(Location location, T value){
        super(location);
        this.value = value;
    }

    public T getValue(){
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void setLocation(Location loc){
        this.setLon(loc.getLon());
        this.setLat(loc.getLat());
        this.setAlt(loc.getAlt());
    }

}

package project.software.uni.positionprediction.datatypes_new;

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



}

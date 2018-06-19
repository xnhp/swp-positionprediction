package project.software.uni.positionprediction.datatypes_new;

import java.util.Date;

/**
 * Created by simon on 29.05.18.
 */

public class TrackedLocation extends Location {

    private Date date;

    public TrackedLocation(Location location, Date date){
        super(location);
        this.date = date;
    }

    public TrackedLocation() {
        super();
    }

    /*
    public Location getLocation(){
        return (Location) this;
        Location location = null;
        if(has_altitude) {
            location = new Location(lon, lat, alt);
        } else {
            location = new Location(lon, lat);
        }
        return location;
    }
    */

    public Date getDate(){
        return date;
    }

}

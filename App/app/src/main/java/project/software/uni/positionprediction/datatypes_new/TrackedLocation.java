package project.software.uni.positionprediction.datatypes_new;

import java.util.Date;

/**
 * Created by simon on 29.05.18.
 */

public class TrackedLocation extends Location {

    private Date date;

    public TrackedLocation(){
        super();
    }

    public TrackedLocation(Location location, Date date){
        super(location);
        this.date = date;
    }

    public Date getDate(){
        return date;
    }

}

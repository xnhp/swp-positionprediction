package project.software.uni.positionprediction.datatype;

import java.util.Date;

import project.software.uni.positionprediction.classes.Location;

/**
 * Created by simon on 29.05.18.
 */

public class TrackingPoint {

    private Location location;

    private Date date;


    public TrackingPoint(Location location, Date date){
        this.location = location;
        this.date = date;
    }

    public Date getDate(){
        return date;
    }

    public Location getLocation(){
        return location;
    }

}

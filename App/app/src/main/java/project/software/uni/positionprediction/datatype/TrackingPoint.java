package project.software.uni.positionprediction.datatype;

import java.util.Date;

/**
 * Created by simon on 29.05.18.
 */

public class TrackingPoint {

    private Location2D location; // todo

    private Date date;


    public TrackingPoint(Location2D location, Date date){
        this.location = location;
        this.date = date;
    }

    public Date getDate(){
        return date;
    }

    public Location2D getLocation(){
        return location;
    }

}

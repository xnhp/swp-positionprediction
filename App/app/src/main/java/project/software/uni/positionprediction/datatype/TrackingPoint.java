package project.software.uni.positionprediction.datatype;

import java.util.Date;

/**
 * Created by simon on 29.05.18.
 */

public class TrackingPoint {

    private Location3D location;

    private Date date;


    public TrackingPoint(Location3D location, Date date){
        this.location = location;
        this.date = date;
    }

    public Date getDate(){
        return date;
    }

    public Location3D getLocation(){
        return location;
    }

}

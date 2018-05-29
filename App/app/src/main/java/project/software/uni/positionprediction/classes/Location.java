package project.software.uni.positionprediction.classes;

public class Location {

    private double loc_long;
    private double loc_lat;

    public Location(double loc_long, double loc_lat) {
        this.loc_long = loc_long;
        this.loc_lat = loc_lat;
    }

    public double getLoc_long() {
        return loc_long;
    }

    public void setLoc_long(double loc_long) {
        this.loc_long = loc_long;
    }

    public double getLoc_lat() {
        return loc_lat;
    }

    public void setLoc_lat(double loc_lat) {
        this.loc_lat = loc_lat;
    }
}

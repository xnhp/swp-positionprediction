package project.software.uni.positionprediction.datatype;


public class Location2D {

    // Class variables
    private double loc_long;
    private double loc_lat;
    private double loc_height;


    // Constructor
    public Location2D(double loc_long, double loc_lat) {
        this.loc_long = loc_long;
        this.loc_lat = loc_lat;
    }


    // Getter and Setter
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


    // Own methods

    /**
     * Adds two locations
     *
     * @param location
     * @return
     */
    public Location2D add(Location2D location) {
        double loc_long = location.getLoc_long();
        double loc_lat = location.getLoc_lat();
        double res_long = this.getLoc_long() + loc_long;
        double res_lat = this.getLoc_lat() + loc_lat;
        return new Location2D(res_long, res_lat);
    }


    /**
     * Print method for Location2D
     */
    public void print() {
        System.out.print("[" + this.getLoc_long() + ", " + this.getLoc_lat() + "]\n");

    }
}

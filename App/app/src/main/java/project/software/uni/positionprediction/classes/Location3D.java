package project.software.uni.positionprediction.classes;

public class Location3D {

    // Class variables
    private double loc_long;
    private double loc_lat;
    private double loc_height;

    // Constructor
    public Location3D(double loc_long, double loc_lat, double loc_height) {
        this.loc_long = loc_long;
        this.loc_lat = loc_lat;
        this.loc_height = loc_height;
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

    public double getLoc_height() { return loc_height; }

    public void setLoc_height(double loc_height) { this.loc_height = loc_height; }

    // Own methods

    /**
     * Adds two locations
     *
     * @param location
     * @return
     */
    public Location3D add(Location3D location) {
        double loc_long = location.getLoc_long();
        double loc_lat = location.getLoc_lat();
        double loc_height = location.getLoc_height();
        double res_long = this.getLoc_long() + loc_long;
        double res_lat = this.getLoc_lat() + loc_lat;
        double res_h = this.getLoc_height() + loc_height;
        return new Location3D(res_long, res_lat, res_h);

    }

    /**
     * Print method for Location3D
     */
    public void print() {
        System.out.print("[" + this.getLoc_long() + ", " + this.getLoc_lat() + ", " + this.getLoc_height() + "]\n");

    }
}

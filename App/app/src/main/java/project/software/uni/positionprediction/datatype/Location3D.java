package project.software.uni.positionprediction.datatype;

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
     * @param location
     * @return
     */
    public Location3D add(Location3D location) {
        return new Location3D(
                this.getLoc_long() + location.getLoc_long(),
                this.getLoc_lat() + location.getLoc_lat(),
                this.getLoc_height() + location.getLoc_height()
        );

    }


    /**
     * Multiplies vector with scalar
     * @param scalar
     * @return
     */
    public Location3D multiply(double scalar){
        return new Location3D(
                this.getLoc_long() * scalar,
                this.getLoc_lat() * scalar,
                this.getLoc_height() * scalar
        );
    }


    /**
     * Subtracts vector with second vector
     * @param location
     * @return
     */
    public Location3D subtract(Location3D location) {
        return this.add( location.multiply(-1) );
    }


    /**
     * Divides vector by number
     * @param number
     * @return
     */
    public Location3D divide(double number) {
        if (number == 0) {
            // Todo Message
            return null;
        }
        return this.multiply( (double) (1/number));
    }



    /**
     * Print method for Location3D
     */
    public void print() {
        System.out.print("[" + this.getLoc_long() + ", " + this.getLoc_lat() + ", " + this.getLoc_height() + "]\n");

    }
}

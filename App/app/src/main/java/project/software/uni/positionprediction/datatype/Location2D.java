package project.software.uni.positionprediction.datatype;


import android.location.Location;

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
     * Transforms 2D locations without height-value to 3D with default height value 0
     * @return
     */
    public Location3D to3D (){
        return new Location3D(this.loc_long, this.loc_lat, 0);
    }


    /**
     * Adds two locations
     * @param location
     * @return
     */
    public Location2D add(Location2D location) {
        return new Location2D(
                this.getLoc_long() + location.getLoc_long(),
                this.getLoc_lat() + location.getLoc_lat()
        );

    }


    /**
     * Multiplies vector with scalar
     * @param scalar
     * @return
     */
    public Location2D multiply(double scalar){
        return new Location2D(
                this.getLoc_long() * scalar,
                this.getLoc_lat() * scalar
        );
    }


    /**
     * Subtracts vector with second vector
     * @param location
     * @return
     */
    public Location2D subtract(Location2D location) {
        return this.add( location.multiply(-1) );
    }



    /**
     * Divides vector by number
     * @param number
     * @return
     */
    public Location2D divide(double number) {
        if (number == 0) {
            // Todo Message
            return null;
        }
        return this.multiply( (double) (1/number));
    }




    /**
     * Print method for Location2D
     */
    public void print() {
        System.out.print("[" + this.getLoc_long() + ", " + this.getLoc_lat() + "]\n");

    }
}

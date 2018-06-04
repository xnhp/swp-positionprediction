package project.software.uni.positionprediction.datatype;

import android.location.Location;

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

    public Location3D to3D(){
        return this;
    }



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
     * Computes angle between current and given vector
     * @param loc_pre
     * @return
     */
    public double getAngle(Location3D loc_pre) {
        Location3D vec_horizontal = new Location3D(0,1,0);
        Location3D vec = this.getVectorFrom(loc_pre);

        return Math.acos( vec_horizontal.scalarproduct(vec) / (vec_horizontal.abs() * vec.abs()));
    }


    /**
     * Gets vector from given location to current location
     * @param loc
     * @return
     */
    public Location3D getVectorFrom(Location3D loc){
        double loc_long = this.getLoc_long() - loc.getLoc_long();
        double loc_lat = this.getLoc_lat() - loc.getLoc_long();
        double loc_height = this.getLoc_height() - loc.getLoc_height();
        return new Location3D(loc_long, loc_lat, loc_height);
    }


    /**
     * Computes scalarproduct of vectors
     * @param vec
     * @return
     */
    public double scalarproduct(Location3D vec) {
        double a1 = this.getLoc_long();
        double a2 = this.getLoc_lat();
        double a3 = this.getLoc_height();
        double b1 = vec.getLoc_long();
        double b2 = vec.getLoc_lat();
        double b3 = vec.getLoc_height();
        return (a1*b1 + a2*b2 + a3*b3);
    }

    /**
     * Computes the length of a vector
     * @return
     */
    public double abs() {
        double a1 = Math.pow(this.getLoc_long(), 2);
        double a2 = Math.pow(this.getLoc_lat(), 2);
        double a3 = Math.pow(this.getLoc_height(), 2);
        return Math.sqrt(a1+a2+a3);
    }

    /**
     * Print method for Location3D
     */
    public void print() {
        System.out.print("[" + this.getLoc_long() + ", " + this.getLoc_lat() + ", " + this.getLoc_height() + "]\n");

    }
}

package project.software.uni.positionprediction.datatypes;

/**
 * Created by simon on 16.07.18.
 */

/**
 * This Class is made to convert goe-coordinates into a cartesian Room to do transformations
 */


public class Vector3 {

    private final static double radius =  6371; // Earth radius in KM

    private double x;
    private double y;
    private double z;


    public Vector3(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Vector3 fromLocation(Location loc){
        double lat = Math.toRadians(loc.getLat());
        double lon = Math.toRadians(loc.getLon());

        double x = Math.cos(lat) * Math.cos(lon);
        double y = Math.cos(lat) * Math.sin(lon);
        double z = Math.sin(lat);

        return new Vector3(x, y, z);
    }


    /**
     * This Method converts the Vector back to a goe location
     * @return Location
     */
    public Location toLocation(){
        double lat = Math.toDegrees(Math.asin(z));
        double lon = Math.toDegrees(Math.atan2(y, x));

        return new Location(lon, lat);
    }

    /**
     * This Method subtracts two Vectors
     * @param v1 Vector so subtract from
     * @param v2 Vector to subtract
     * @return v1 - v2
     */
    public static Vector3 sub(Vector3 v1, Vector3 v2){
        return new Vector3(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }

    /**
     * This Method adds two Vectors
     * @param v1 first Vector
     * @param v2 second Vector
     * @return v1 + v2
     */
    public static Vector3 add(Vector3 v1, Vector3 v2){
        return new Vector3(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
    }


    /**
     * This Method calculates the Length of the Vector
     * @return
     */
    public double getLength(){
        return Math.sqrt(x*x + y*y + z*z);
    }

    /**
     * This Method calculates the dot product of two Vectors
     * @param v1 first Vector
     * @param v2 second Vector
     * @return v1 * v2
     */
    public static double dot(Vector3 v1, Vector3 v2){
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    /**
     * This Method returns the angle between two vectors
     * @param v1 first Vector
     * @param v2 second Vector
     * @return the angle between v1 and v2
     */
    public static double getAngle(Vector3 v1, Vector3 v2){
        return Math.acos(dot(v1, v2) / (v1.getLength() * v2.getLength()));
    }

    /**
     * This Method rotates the Vector
     * @param angle to angle to rotate with
     * @return the rotated Vector
     */
    public Vector3 rotate(double angle){
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        return new Vector3(x * cos + z * sin, y, -x * sin + z * cos);
    }

    /**
     * This Method sets the length of the vector to the given value
     * @param length the new length
     */
    public void setLength(double length){

        double abs = this.getLengthXZ();

        this.x /= abs;
        this.z /= abs;

        this.x *= length;
        this.z *= length;
    }

    /**
     * This Method calculates the Length in the xz plane
     * @return length
     */
    public double getLengthXZ(){
        return Math.sqrt(this.x * this.x + this.z * this.z);
    }


    public String toString(){
        return "[" + x + ", " + y + ", " + z + "]";
    }

}

package project.software.uni.positionprediction.datatypes_new;

/**
 * Elementary class for holding location values.
 */
public class Location {


    // CLASS VARIABLES

    private double lon; // Latitude
    private double lat; // Longitude
    private double alt; // Altitude
    private boolean has_altitude;


    // CONSTRUCTORS

    public Location(Location loc){
        this.lon = loc.getLon();
        this.lat = loc.getLat();
        this.alt = loc.getAlt();
        this.has_altitude = loc.hasAltitude();
    }

    public Location(double lon, double lat, double alt) {
        this.lon = lon;
        this.lat = lat;
        this.alt = alt;
        this.has_altitude = true;
    }

    public Location(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
        this.alt = 0;
        this.has_altitude = false;
    }

    public Location() {

    }


    // GETTERS AND SETTERS

    // A parametrized getter for either lat, lon or alt
    public double getDimension(EDimension dim) {
        double value = 0;
        switch (dim) {
            case LAT:
                value = getLat();
                break;
            case LON:
                value = getLon();
                break;
            case ALT:
                value = getAlt();
                break;
        }
        return value;
    }

    // Basic getters
    public double getLon() {
        return lon;
    }
    public double getLat() {
        return lat;
    }
    public double getAlt() {
        return alt;
    }
    public boolean hasAltitude() {
        return has_altitude;
    }

    // basic setters
    public void setLon(double lon) {
        this.lon = lon;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public void setAlt(double alt) {
        this.alt = alt;
    }
    public void setHasAltitude(boolean has_altitude) {
        this.has_altitude = has_altitude;
    }


    // =============================================================================================


    // VECTOR ARITHMETICS

    /**
     * Add the values of the given location to this location and return a *new* location object.
     * @param loc
     * @return
     */
    public Location add(Location loc)  {
        if (loc.has_altitude && this.has_altitude) {
            return new Location(
                    this.lon + loc.lon,
                    this.lat + loc.lat,
                    this.alt + loc.alt
            );
        } else if (!loc.has_altitude && !this.has_altitude) {
            return new Location(
                    this.lon + loc.lon,
                    this.lat + loc.lat
            );
        } else {
            IncompatibleLocationException e = new IncompatibleLocationException();
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * multiply values of a location vector by a scalar
     * @param scalar
     * @return a new location object
     */
    public Location multiply(double scalar) {
        double newLat = this.lat * scalar;
        double newLon = this.lon * scalar;
        if (this.has_altitude) {
            double newAlt = this.alt * scalar;
            return new Location(newLon, newLat, newAlt);
        } else {
            return new Location(newLon, newLat);
        }
    }

    /**
     * Subtract one location from the other
     * @param loc
     * @return a new location object
     */
    public Location subtract(Location loc)  {
        return this.add( loc.multiply(-1) );
    }

    /**
     * Divide values of a location vector by a given number
     * @param number
     * @return a new location object
     */
    public Location divide(double number) {
        if (number == 0) {
            IllegalArgumentException e = new IllegalArgumentException("division by zero");
            e.printStackTrace();
            throw new RuntimeException();
        }
        return this.multiply( (double) (1/number));
    }


    /**
     * Gets vector from given location to current location
     * @param loc
     * @return a new location object
     */
    public Location getVectorFrom(Location loc){
        double loc_long = this.lon - loc.lon;
        double loc_lat = this.lat - loc.lon;
        if (!this.has_altitude && !loc.has_altitude) {
            return new Location(loc_long, loc_lat);
        } else if (this.has_altitude && loc.has_altitude){
            double loc_height = this.alt - loc.alt;
            return new Location(loc_long, loc_lat, loc_height);
        } else {
            IncompatibleLocationException e = new IncompatibleLocationException();
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Computes angle between current and given vector
     * @param loc_pre
     * @return
     */
    public double getAngle(Location loc_pre) {
        Location vec_horizontal = new Location(0,1,0);
        Location vec = this.getVectorFrom(loc_pre);
        return Math.acos( vec_horizontal.dotProduct(vec) / (vec_horizontal.abs() * vec.abs()));
    }

    /**
     * Computes dot product of vectors
     * @param vec
     * @return
     */
    public double dotProduct(Location vec) {
        double a1 = this.lon;
        double a2 = this.lat;
        double b1 = vec.lon;
        double b2 = vec.lat;
        if (vec.has_altitude && this.has_altitude) {
            double a3 = this.alt;
            double b3 = vec.alt;
            return (a1*b1 + a2*b2 + a3*b3);
        } else if (!vec.has_altitude && !this.has_altitude) {
            return (a1*b1 + a2*b2);
        } else {
            IncompatibleLocationException e = new IncompatibleLocationException();
            e.printStackTrace();
            throw new RuntimeException();
        }

    }


    /**
     * Computes the length of a vector
     * @return
     */
    public double abs() {
        double a1 = Math.pow(this.lon, 2);
        double a2 = Math.pow(this.lat, 2);
        if (!this.has_altitude) {
            double a3 = Math.pow(this.alt, 2);
            return Math.sqrt(a1+a2+a3);
        } else {
            return Math.sqrt(a1+a2);
        }


    }

    /**
     * Rotation about the z-axis (height). We don't need any other rotation, because the
     * angle doesn't change with the third dimension (on the plane). We get the 3D movement
     * with the addition of the vectors, not the angle.
     * @param angle
     * @return
     */
    public Location rotate(double angle) {
        // TODO: not set alt=0 here
        // work with 2D vector instead
        if (!has_altitude) {
            this.setAlt(0);
        }

        double v1 = this.getLon();
        double v2 = this.getLat();
        double v3 = this.getAlt();

        double r11 = Math.cos(angle);
        double r12 = - Math.sin(angle);
        double r13 = 0;
        double r21 = Math.sin(angle);
        double r22 = Math.cos(angle);
        double r23 = 0;
        double r31 = 0;
        double r32 = 0;
        double r33 = 0;

        double res1 = r11*v1 + r12*v2 + r13*v3;
        double res2 = r21*v1 + r22*v2 + r23*v3;
        double res3 = r31*v1 + r32*v2 + r33*v3;

        return new Location(res1, res2, res3);

    }


    // =============================================================================================

    /**
     * Print method for Location3D
     */
    public void print() {
        System.out.print("[" + this.lon + ", " + this.lat + ", " + this.alt + "]\n");

    }

    public String toString(){
        return "[" + this.lon + ", " + this.lat + ", " + this.alt + "]";
    }

    public Location to3D() {
        if (!this.has_altitude) {
            return new Location(this.lon, this.lat, 0);
        } else return this;
    }



}


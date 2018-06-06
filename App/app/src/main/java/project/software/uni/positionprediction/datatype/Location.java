package project.software.uni.positionprediction.datatype;

/**
 * Elementary class for holding (abstract, not map-dependent) location values.
 * TODO: This will replace Location2D, Location3D
 */
public class Location {
    public double lon; // Latitude
    public double lat; // Longitude
    public double alt; // Altitude
    boolean has_altitude;

    public Location(double lon, double lat, double alt) {
        this.lon = lon;
        this.lat = lat;
        this.alt = alt;
        this.has_altitude = true;
    }

    public Location(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
        this.has_altitude = false;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getAlt() {
        return alt;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public boolean isHas_altitude() {
        return has_altitude;
    }

    public void setHas_altitude(boolean has_altitude) {
        this.has_altitude = has_altitude;
    }


    /**
     * Transforms 2D locations without height-value to 3D with default height value 0
     * @return
     */
    public Location to3D () {
        if (has_altitude) {
            return new Location(this.lon, this.lat, this.alt);
        } else {
            return new Location(this.lon, this.lat, 0);
        }
    }


    /**
     * Add the values of the given location to this location and return a *new* location object.
     * @param loc
     * @return
     */
    public Location add(Location loc)  {
        if (loc.has_altitude && this.has_altitude) {
            return new Location(
                    this.lat + loc.lat,
                    this.lon + loc.lon,
                    this.alt + loc.alt
            );
        } else if (!loc.has_altitude && !this.has_altitude) {
            return new Location(
                    this.lat + loc.lat,
                    this.lon + loc.lon
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
            return new Location(newLat, newLon, newAlt);
        } else {
            return new Location(newLon, newLon);
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



    /**
     * Print method for Location
     */
    public void print() {
        if (!has_altitude) {
            this.setAlt(0);
        }
        System.out.print("[" + this.getLon() + ", " + this.getLat() + ", " + this.getAlt() + "]\n");

    }

}

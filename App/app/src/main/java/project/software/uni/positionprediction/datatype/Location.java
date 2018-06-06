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
     * Adds two locations
     * @param location
     * @return
     */
    public Location add(Location location) {
        if (!has_altitude) {
            this.setAlt(0);
        }
        return new Location(
                this.getLon() + location.getLon(),
                this.getLat() + location.getLat(),
                this.getAlt() + location.getAlt()
        );

    }


    /**
     * Multiplies vector with scalar
     * @param scalar
     * @return
     */
    public Location multiply(double scalar){
        if (!has_altitude) {
            this.setAlt(0);
        }
        return new Location(
                this.getLon() * scalar,
                this.getLat() * scalar,
                this.getAlt() * scalar
        );
    }


    /**
     * Subtracts vector with second vector
     * @param location
     * @return
     */
    public Location subtract(Location location) {
        return this.add( location.multiply(-1) );
    }


    /**
     * Divides vector by number
     * @param number
     * @return
     */
    public Location divide(double number) {
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
    public double getAngle(Location loc_pre) {
        if (!has_altitude) {
            this.setAlt(0);
        }
        if (!loc_pre.has_altitude) {
            loc_pre.setAlt(0);
        }
        Location vec_horizontal = new Location(0,1,0);
        Location vec = this.getVectorFrom(loc_pre);

        return Math.acos( vec_horizontal.scalarproduct(vec) / (vec_horizontal.abs() * vec.abs()));
    }


    /**
     * Gets vector from given location to current location
     * @param loc
     * @return
     */
    public Location getVectorFrom(Location loc){
        if (!has_altitude) {
            this.setAlt(0);
        }
        if (!loc.has_altitude) {
            loc.setAlt(0);
        }
        double Lon = this.getLon() - loc.getLon();
        double loc_lat = this.getLat() - loc.getLon();
        double loc_height = this.getAlt() - loc.getAlt();
        return new Location(Lon, loc_lat, loc_height);
    }


    /**
     * Computes scalarproduct of vectors
     * @param vec
     * @return
     */
    public double scalarproduct(Location vec) {
        if (!has_altitude) {
            this.setAlt(0);
        }
        if (!vec.has_altitude) {
            vec.setAlt(0);
        }
        double a1 = this.getLon();
        double a2 = this.getLat();
        double a3 = this.getAlt();
        double b1 = vec.getLon();
        double b2 = vec.getLat();
        double b3 = vec.getAlt();
        return (a1*b1 + a2*b2 + a3*b3);
    }

    /**
     * Computes the length of a vector
     * @return
     */
    public double abs() {
        if (!has_altitude) {
            this.setAlt(0);
        }
        double a1 = Math.pow(this.getLon(), 2);
        double a2 = Math.pow(this.getLat(), 2);
        double a3 = Math.pow(this.getAlt(), 2);
        return Math.sqrt(a1+a2+a3);
    }


    /**
     * Rotation about the z-axis (height). We don't need any other rotation, because the
     * angle doesn't change with the third dimension (on the plane). We get the 3D movement
     * with the addition of the vectors, not the angle.
     * @param angle
     * @return
     */
    public Location rotate(double angle) {
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

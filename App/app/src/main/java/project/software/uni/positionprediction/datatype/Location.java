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
}

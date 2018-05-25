package project.software.uni.positionprediction.classes;

public class Tupel {

    // Class variables
    double results[];
    double x_min;
    double x_max;
    double y_min;
    double y_max;
    double raster_x;
    double raster_y;


    // Constructor
    public Tupel(double[] results, double x_min, double x_max, double y_min, double y_max, double raster_x, double raster_y) {
        this.results = results;
        this.x_min = x_min;
        this.x_max = x_max;
        this.y_min = y_min;
        this.y_max = y_max;
        this.raster_x = raster_x;
        this.raster_y = raster_y;
    }


    // Getter and Setter
    public double getX_min() {
        return x_min;
    }

    public void setX_min(double x_min) {
        this.x_min = x_min;
    }

    public double getX_max() {
        return x_max;
    }

    public void setX_max(double x_max) {
        this.x_max = x_max;
    }

    public double getY_min() {
        return y_min;
    }

    public void setY_min(double y_min) {
        this.y_min = y_min;
    }

    public double getY_max() {
        return y_max;
    }

    public void setY_max(double y_max) {
        this.y_max = y_max;
    }

    public double getRaster_x() {
        return raster_x;
    }

    public void setRaster_x(double raster_x) {
        this.raster_x = raster_x;
    }

    public double getRaster_y() {
        return raster_y;
    }

    public void setRaster_y(double raster_y) {
        this.raster_y = raster_y;
    }
}



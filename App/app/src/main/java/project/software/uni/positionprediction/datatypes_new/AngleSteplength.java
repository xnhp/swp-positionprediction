package project.software.uni.positionprediction.datatypes_new;

public class AngleSteplength {

    private double angle;
    private double steplength;

    public AngleSteplength(double angle, double steplength) {
        this.angle = angle % (2*Math.PI);
        this.steplength = steplength;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getSteplength() {
        return steplength;
    }

    public void setSteplength(double steplength) {
        this.steplength = steplength;
    }
}

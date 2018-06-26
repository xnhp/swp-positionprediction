package project.software.uni.positionprediction.algorithm;

import org.junit.Test;

import java.io.File;

import project.software.uni.positionprediction.datatypes_new.Location;

import static org.junit.Assert.assertEquals;


public class AlgorithmTests {

    private double delta = 0.000001;


    @Test
    public void path() throws Exception {
        File dir = new File("src/main/java/project/software/uni/positionprediction/algorithm");
        if (dir.exists()) {
            System.out.println("Project Path " +dir.getPath() + " exists");
            File[] a = dir.listFiles();
            for (int i = 0; i<a.length; i++) {
                System.out.println("Project Path " + a[i].getName());
            }
        }
    }



    @Test
    public void arithmetic() throws Exception {
        Location a = new Location(4.55, 11, -1);
        Location b = new Location(2, -3, 4);
        double number = 3.11;

        // Addition
        Location method_add = a.add(b);
        Location own_add = new Location(6.55, 8, 3);

        assertEquals(method_add.getLon(), own_add.getLon(), delta);
        assertEquals(method_add.getLat(), own_add.getLat(), delta);
        assertEquals(method_add.getAlt(), own_add.getAlt(), delta);

        // Subtraction
        Location method_sub = a.subtract(b);
        Location own_sub = new Location(2.55, 14, -5);

        assertEquals(method_add.getLon(), own_add.getLon(), delta);
        assertEquals(method_add.getLat(), own_add.getLat(), delta);
        assertEquals(method_add.getAlt(), own_add.getAlt(), delta);

        // Multiplication
        Location method_mult = a.multiply(number);
        Location own_mult = new Location(14.1505, -34.21, -3.11);

        assertEquals(method_add.getLon(), own_add.getLon(), delta);
        assertEquals(method_add.getLat(), own_add.getLat(), delta);
        assertEquals(method_add.getAlt(), own_add.getAlt(), delta);

        // Division
        Location method_div = a.divide(number);
        Location own_div = new Location(1.463022508, -3.536977492, -0.3215434084);

        assertEquals(method_add.getLon(), own_add.getLon(), delta);
        assertEquals(method_add.getLat(), own_add.getLat(), delta);
        assertEquals(method_add.getAlt(), own_add.getAlt(), delta);

    }


    @Test
    public void vector() throws Exception {

        // getangle
        Location v01 = new Location(1,0,0);
        Location v02 = new Location( 0, 1);
        double computed_angle = v01.getAngle(v02);
        double expected_angle = Math.PI / 2;
        assertEquals(expected_angle, computed_angle, delta);

        // scalarproduct
        Location v11 = new Location(1,0,0);
        Location v12 = new Location( 0, 1);
        double computed_sp = v11.dotProduct(v12);
        double expected_sp = 0;
        assertEquals(expected_sp, computed_sp, delta);

        // abs
        Location v21 = new Location(1,0);
        double computed_abs = v21.abs();
        double expected_abs = 1;
        assertEquals(expected_sp, computed_sp, delta);

        // rotate
        Location v31 = new Location(1,1);
        double alpha = Math.PI;
        Location computed_loc = v31.rotate(alpha);
        Location expected_loc = new Location(-1,-1,0);
        assertEquals(computed_loc.getLon(), expected_loc.getLon(), delta);
        assertEquals(computed_loc.getLat(), expected_loc.getLat(), delta);
        assertEquals(computed_loc.getAlt(), expected_loc.getAlt(), delta);

    }


}

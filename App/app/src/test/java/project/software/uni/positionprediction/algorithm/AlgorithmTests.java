package project.software.uni.positionprediction.algorithm;

import org.junit.Test;
import project.software.uni.positionprediction.datatype.Location;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AlgorithmTests {

    private double delta = 0.000001;

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
        Location v1 = new Location(1,0,0);
        Location v2 = new Location( 0, 1, 0);
        double computed_angle = v1.getAngle(v2);
        double expected_angle = Math.PI / 2;

        assertEquals(expected_angle, computed_angle, delta);

    }


}

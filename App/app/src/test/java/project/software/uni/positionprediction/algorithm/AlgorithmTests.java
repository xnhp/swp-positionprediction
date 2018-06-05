package project.software.uni.positionprediction.algorithm;

import org.junit.Test;
import project.software.uni.positionprediction.datatype.Location3D;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AlgorithmTests {

    private double delta = 0.000001;

    @Test
    public void arithmetic3D() throws Exception {
        Location3D a = new Location3D(4.55, 11, -1);
        Location3D b = new Location3D(2, -3, 4);
        double number = 3.11;

        // Addition
        Location3D method_add = a.add(b);
        Location3D own_add = new Location3D(6.55, 8, 3);

        assertEquals(method_add.getLoc_long(), own_add.getLoc_long(), delta);
        assertEquals(method_add.getLoc_lat(), own_add.getLoc_lat(), delta);
        assertEquals(method_add.getLoc_height(), own_add.getLoc_height(), delta);

        // Subtraction
        Location3D method_sub = a.subtract(b);
        Location3D own_sub = new Location3D(2.55, 14, -5);

        assertEquals(method_add.getLoc_long(), own_add.getLoc_long(), delta);
        assertEquals(method_add.getLoc_lat(), own_add.getLoc_lat(), delta);
        assertEquals(method_add.getLoc_height(), own_add.getLoc_height(), delta);

        // Multiplication
        Location3D method_mult = a.multiply(number);
        Location3D own_mult = new Location3D(14.1505, -34.21, -3.11);

        assertEquals(method_add.getLoc_long(), own_add.getLoc_long(), delta);
        assertEquals(method_add.getLoc_lat(), own_add.getLoc_lat(), delta);
        assertEquals(method_add.getLoc_height(), own_add.getLoc_height(), delta);

        // Division
        Location3D method_div = a.divide(number);
        Location3D own_div = new Location3D(1.463022508, -3.536977492, -0.3215434084);

        assertEquals(method_add.getLoc_long(), own_add.getLoc_long(), delta);
        assertEquals(method_add.getLoc_lat(), own_add.getLoc_lat(), delta);
        assertEquals(method_add.getLoc_height(), own_add.getLoc_height(), delta);

    }
}

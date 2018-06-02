package project.software.uni.positionprediction.algorithm;

import android.content.Context;
import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;


import project.software.uni.positionprediction.datatype.Location3D;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AlgorithmTests {

    private double delta = 0.000000001;

    @Test
    public void arithmetic3D() throws Exception {
        Location3D a = new Location3D(4.55, 11, -1);
        Location3D b = new Location3D(2, -3, 4);
        double number = 3.11;

        Location3D method_add = a.add(b);
        Location3D own_add = new Location3D(6.55, 8, 3);

        assertEquals(method_add.getLoc_long(), own_add.getLoc_long(), delta);
        assertEquals(method_add.getLoc_lat(), own_add.getLoc_lat(), delta);
        assertEquals(method_add.getLoc_height(), own_add.getLoc_height(), delta);




        // Todo divide, multiply, subtract


    }
}

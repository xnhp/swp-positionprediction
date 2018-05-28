package project.software.uni.positionprediction;

import org.junit.Test;

import project.software.uni.positionprediction.classes.AlgorithmExtrapolation;
import project.software.uni.positionprediction.classes.Location;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }


    /**
     * Test for the computation of the average
     * @throws Exception
     */
    @Test
    public void average_isCorrect() throws Exception {
        /**
        Location d1 = new Location(0,0);
        Location d2 = new Location(1,1);
        Location d3 = new Location(2,0);
        Location d4 = new Location(3,2);
        Location d5 = new Location(4,1);
        Location data[] = {d1, d2, d3, d4, d5};
        Location comp = new Location((double) 1/5, (double) 4/5);
        comp.print();

        AlgorithmExtrapolation a = new AlgorithmExtrapolation();
        Location with_method = a.next_Location(data);
        assertEquals(comp.getLoc_lat(), with_method.getLoc_lat(), 0.001);
        assertEquals(comp.getLoc_long(), with_method.getLoc_long(), 0.001);
         */
    }



    /**
     * Test for running the prediction algorithm to check results with System.out
     * @throws Exception
     */
    @Test
    public void run_prediction_algorithm() throws Exception {
        AlgorithmExtrapolation alg = new AlgorithmExtrapolation();
        alg.predict_interpolation(null, null, 0);
    }


}
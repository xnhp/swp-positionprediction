
package project.software.uni.positionprediction;

import org.junit.Test;

import project.software.uni.positionprediction.algorithm.AlgorithmExtrapolationExtended;

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


    @Test
    public void run_prediction_algorithm() throws Exception {
        AlgorithmExtrapolationExtended alg = new AlgorithmExtrapolationExtended();
        alg.predict(null, null, 0);
    }


}

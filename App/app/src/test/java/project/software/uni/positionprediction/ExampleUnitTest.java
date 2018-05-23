package project.software.uni.positionprediction;

import org.junit.Test;

import project.software.uni.positionprediction.classes.PredictionImplementation;
import project.software.uni.positionprediction.interfaces.PredictionAlgorithm;

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
     * Test for running the prediction algorithm to check results with System.out
     * @throws Exception
     */
    public void run_prediction_algorithm() throws Exception {
        PredictionAlgorithm alg = new PredictionImplementation();
        alg.predict(null, null, 0);
    }


}
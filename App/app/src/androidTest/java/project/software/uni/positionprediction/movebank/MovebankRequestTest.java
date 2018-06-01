package project.software.uni.positionprediction.movebank;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

import project.software.uni.positionprediction.activities.BirdSelect;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class MovebankRequestTest {



    @Test
    public void requestData() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        final CountDownLatch signal = new CountDownLatch(1);

        MovebankRequest mbr = new MovebankRequest("https://httpbin.org/status/500", appContext);
        mbr.requestData("",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fail();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // allow test to pass
                        signal.countDown();
                    }
                }
        );


        signal.await(); // wait for countdown to be 0?
    }
}

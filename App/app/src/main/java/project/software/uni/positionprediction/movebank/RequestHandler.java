package project.software.uni.positionprediction.movebank;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by simon on 23.05.18.
 */

public abstract class RequestHandler implements Response.Listener<String>, Response.ErrorListener {

    public abstract void handleResponse(String response);

    @Override
    public void onResponse(String response) {
        handleResponse(response);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("RequestError", error.toString());
        handleResponse(null);
    }

}

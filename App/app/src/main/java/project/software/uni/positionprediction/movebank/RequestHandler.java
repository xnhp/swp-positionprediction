package project.software.uni.positionprediction.movebank;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import project.software.uni.positionprediction.datatypes_new.Request;

/**
 * Created by simon on 23.05.18.
 */

public abstract class RequestHandler implements Response.Listener<String>, Response.ErrorListener {


    private Request request;

    public void setRequest(Request request){
        this.request = request;
    }

    public abstract void handleResponse(Request request);

    @Override
    public void onResponse(String response) {
        request.setResponseStatus(MovebankRequest.getStatusForRequest(request));
        request.setResponse(response);
        handleResponse(request);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("RequestError", error.toString());
        Request request = new Request(-1);
        request.setResponseStatus(-1);
        handleResponse(request);
    }

}

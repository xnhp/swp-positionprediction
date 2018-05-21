package project.software.uni.positionprediction.movebank;


import android.content.Context;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import project.software.uni.positionprediction.activities.BirdSelect;


public class MovebankRequest {

    // TODO: put this in a configuration file
    private static final String BASE_URL = "https://www.movebank.org/movebank/service/direct-read?";
    private String username = "SP_1-2";
    private String password = "Xamhdg9adB";

    private String baseUrl = null;
    private Context context = null;

    /**
     * If passed a baseUrl, context use that. If not, use the hardcoded one.
     * This is to enable sending requests to localhost, in order to mock a stub http host
     * that sends "fake" replies for testing.
     * @param baseUrl The base URL to send the API requests to.
     */
    public MovebankRequest(String baseUrl, Context context) {
        this.baseUrl = baseUrl;
        this.context = context;
    }

    public MovebankRequest() {
        this.baseUrl = BASE_URL;
        // TODO: (BM) I don't think there should be a link to the BirdSelect activity.
        this.context = BirdSelect.getAppContext();
    }

    /**
     * Sends a request to the Movebank API for the specified attributes
     * @param attributes URL-encoded string of attributes that are requested.
     * @return TODO
     *
     * TODO: (BM) imo the onResponse and onError listeners should be given as arguments
     */
    public String requestData(String attributes,
                              com.android.volley.Response.Listener<String> responseListener,
                              com.android.volley.Response.ErrorListener errorListener){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this.context);

        String url = baseUrl+attributes;
        // Request a string response from the provided URL.
        System.out.println("sending string request to " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                responseListener,
                errorListener
                );

        /*{
            @Override
            public Map <String, String> getHeaders() {
                HashMap< String, String > headers = new HashMap <> ();
                String creds = String.format("%s:%s",username,password);
                String encodedCredentials = Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                headers.put("Authorization", "Basic " + encodedCredentials);

                return headers;
            }
        };*/

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        //TODO fix return
        return "";
    }


    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public void setUserCreds(String username, String password){
        this.username=username;
        this.password=password;
    }

    /*
    // TODO: Can just check for HTTP Status Code here?
    public boolean isUserCredsValid(){

        String typeAttr = "attributes";

        String result = requestData(typeAttr);
        //TODO check result

        return result=="";
    }*/

}

package project.software.uni.positionprediction.movebank;


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


    /**
     * Sends a request to the Movebank API for the specified attributes
     * @param attributes URL-encoded string of attributes that are requested.
     * @return TODO
     *
     * TODO: (BM) imo the onResponse and onError listeners should be given as arguments
     */
    public String requestData(String attributes){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(BirdSelect.getAppContext());

        String url = BASE_URL+attributes;
        // Request a string response from the provided URL.

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener <String> () {
                    @Override
                    public void onResponse(String response)
                    {
                        //TODO: handle response
                        System.out.println(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // log and show error message with error code;
                    }

                })

        {
            @Override
            public Map <String, String> getHeaders() {
                HashMap< String, String > headers = new HashMap <> ();
                String creds = String.format("%s:%s",username,password);
                String encodedCredentials = Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                headers.put("Authorization", "Basic " + encodedCredentials);

                return headers;
            }
        };

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

    public boolean isUserCredsValid(){

        String typeAttr = "attributes";

        String result = requestData(typeAttr);
        //TODO check result
        // TODO: Can just check for HTTP Status Code here?
        return result=="";
    }

}

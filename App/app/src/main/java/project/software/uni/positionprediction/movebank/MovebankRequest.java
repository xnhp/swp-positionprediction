package project.software.uni.positionprediction.movebank;

import project.software.uni.positionprediction.R;

import android.content.Context;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;



public class MovebankRequest {

    // TODO: put this in a configuration file
    private String username = null;
    private String password = null;

    private String baseUrl = null;
    private Context context = null;

    private RequestQueue queue = null;

    /**
     * If passed a baseUrl, context use that. If not, use the hardcoded one.
     * This is to enable sending requests to localhost, in order to mock a stub http host
     * that sends "fake" replies for testing.
     * @param baseUrl The base URL to send the API requests to.
     */
    public MovebankRequest(String baseUrl, Context context) {

        this.context = context;
        this.queue = Volley.newRequestQueue(this.context);

        this.baseUrl = baseUrl;
        this.password = context.getResources().getString(R.string.movebank_password);
        this.username = context.getResources().getString(R.string.movebank_user);

    }

    public MovebankRequest(Context context) {

        this.context = context;
        this.queue = Volley.newRequestQueue(this.context);


        this.baseUrl = context.getResources().getString(R.string.movebank_base_url);
        this.password = context.getResources().getString(R.string.movebank_password);
        this.username = context.getResources().getString(R.string.movebank_user);

    }

    /**
     * Sends a request to the Movebank API for the specified attributes
     * @param attributes URL-encoded string of attributes that are requested.
     */
    public void requestDataAsync( String attributes,
                                  Response.Listener<String> responseListener,
                                  Response.ErrorListener errorListener){

        String url = baseUrl+attributes;
        // Request a string response from the provided URL.
        System.out.println("sending string request to " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                responseListener,
                errorListener
                )

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
    }

    /**
     * Sends a request to the Movebank API and waits for the response
     * This Method must never be called from the main thread.
     * @param attributes URL-encoded string of attributes that are requested.
     */
    public String requestDataSync(String attributes){

        RequestFuture<String> requestFuture = RequestFuture.newFuture();

        String url = baseUrl+attributes;

        // Request a string response from the provided URL.
        System.out.println("sending string request to " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
               requestFuture, requestFuture
        )

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
        requestFuture.setRequest(queue.add(stringRequest));

        try{
            String response = requestFuture.get(10, TimeUnit.SECONDS);
            return response;

        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            return null;
        }
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


    // TODO: Can just check for HTTP Status Code here?
    public boolean isUserCredsValid(){

        String typeAttr = "attributes";

        String result = requestDataSync(typeAttr);

        //TODO check result

        return result=="";
    }

}

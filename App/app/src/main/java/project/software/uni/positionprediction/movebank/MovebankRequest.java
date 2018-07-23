package project.software.uni.positionprediction.movebank;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import project.software.uni.positionprediction.R;



public class MovebankRequest {

    private String username = null;
    private String password = null;

    private String baseUrl = null;
    private Context context = null;

    private static Map<Integer, Integer> statusMap;
    private int currentRequestCode;

    /**
     * If passed a baseUrl, context use that. If not, use the hardcoded one.
     * This is to enable sending requests to localhost, in order to mock a stub http host
     * that sends "fake" replies for testing.
     * @param baseUrl The base URL to send the API requests to.
     */
    public MovebankRequest(String baseUrl, Context context) {

        this.context = context;

        this.baseUrl = baseUrl;
        this.password = context.getResources().getString(R.string.movebank_password);
        this.username = context.getResources().getString(R.string.movebank_user);

        statusMap = new HashMap<Integer, Integer>();
    }

    public MovebankRequest(Context context) {

        this.context = context;


        this.baseUrl = context.getResources().getString(R.string.movebank_base_url);
        this.password = context.getResources().getString(R.string.movebank_password);
        this.username = context.getResources().getString(R.string.movebank_user);

        statusMap = new HashMap<Integer, Integer>();

    }

    /**
     * Sends a request to the Movebank API for the specified attributes
     * @param attributes URL-encoded string of attributes that are requested.
     */
    public void requestDataAsync( String attributes,
                                  RequestHandler requestHandler){

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = baseUrl+attributes;
        // Request a string response from the provided URL.
        Log.i("MovebankRequest", "sending string request to " + url);

        project.software.uni.positionprediction.datatypes.Request request =
                new project.software.uni.positionprediction.datatypes.Request(getNextRequestCode());

        requestHandler.setRequest(request);

        StringRequest stringRequest = createStringRequest(request, url, requestHandler, requestHandler);
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    /**
     * Sends a request to the Movebank API and waits for the response
     * This Method must never be called from the main thread.
     * @param attributes URL-encoded string of attributes that are requested.
     */
    public project.software.uni.positionprediction.datatypes.Request requestDataSync(String attributes){

        RequestQueue queue = Volley.newRequestQueue(context);

        RequestFuture<String> requestFuture = RequestFuture.newFuture();

        String url = baseUrl+attributes;

        // Request a string response from the provided URL.
        Log.i("MovebankRequest", "sending string request to " + url);

        project.software.uni.positionprediction.datatypes.Request request =
                new project.software.uni.positionprediction.datatypes.Request(getNextRequestCode());

        StringRequest stringRequest = createStringRequest(request, url, requestFuture, requestFuture);

        // Add the request to the RequestQueue.
        requestFuture.setRequest(queue.add(stringRequest));

        try{
            String response = requestFuture.get(10, TimeUnit.SECONDS);
            request.setResponseStatus(statusMap.remove(new Integer(request.getId())));
            request.setResponse(response);
            return request;

        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            System.out.println(e.toString());

            if(e.getCause() instanceof VolleyError) {

                VolleyError err = (VolleyError) e.getCause();
                if(err.networkResponse != null) request.setResponseStatus(err.networkResponse.statusCode);
                else request.setResponseStatus(-1);

            } else {
                request.setResponseStatus(-1);
            }

            return request;
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

    /**
     * This Method return the status Code for the given Request
     * @param request the Request to return the HttpStatusCode for
     * @return the HttpStatusCode as int
     */
    public static int getStatusForRequest(project.software.uni.positionprediction.datatypes.Request request){
        return statusMap.remove(request.getId());
    }


    /**
     * This Method returns Thread-Save a unique Code for the next Request
     * @return Code for the Request
     */
    private synchronized int getNextRequestCode(){
        return currentRequestCode++;
    }


    /**
     * This Method creates a StringRequest with the required headers
     * @param request a Request object
     * @param url the url to send the Request to
     * @param responseListener the responseListener
     * @param errorListener the errorListener
     * @return a StringRequest
     */
    private StringRequest createStringRequest(final project.software.uni.positionprediction.datatypes.Request request,
                                              String url,
                                              Response.Listener<String> responseListener,
                                              Response.ErrorListener errorListener){

        return new StringRequest(Request.Method.GET, url,
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

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                statusMap.put(new Integer(request.getId()), new Integer(response.statusCode));

                return super.parseNetworkResponse(response);
            }
        };

    }

}

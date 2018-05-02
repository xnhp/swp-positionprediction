package uni.movebanktest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String user = "";
        final String password= "";

        final TextView mTextView = (TextView) findViewById(R.id.text_view);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://www.movebank.org/movebank/service/json-auth?study_id=16880941&individual_local_identifiers[]=Mary&individual_local_identifiers[]=Butterball&individual_local_identifiers[]=Schaumboch&&max_events_per_individual=2000&sensor_type=gps";

        // Request a string response from the provided URL.

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener <String> () {
                    @Override
                    public void onResponse(String response)
                    {
                        //TODO: handle response
                        mTextView.setText(response.substring(0, 500));
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
            public Map <String, String> getHeaders() throws AuthFailureError {
            HashMap< String, String > headers = new HashMap < String, String > ();
            String creds = String.format("%s:%s",user,password);
            String encodedCredentials = Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
            headers.put("Authorization", "Basic " + encodedCredentials);

            return headers;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
}

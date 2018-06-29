package project.software.uni.positionprediction.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.movabo.webserver.WebServer;
import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.datatypes_new.BirdData;
import project.software.uni.positionprediction.movebank.SQLDatabase;
import project.software.uni.positionprediction.datatypes_new.Bird;
import project.software.uni.positionprediction.util.LoadingIndicator;
import project.software.uni.positionprediction.util.PermissionManager;
import project.software.uni.positionprediction.datatypes_new.Bird;
import project.software.uni.positionprediction.fragments.FloatingMapButtons;



/**
 * todo: TJ 180622: to be replaced by correct workflow
 */


public class Cesium extends AppCompatActivity implements FloatingMapButtons.floatingMapButtonsClickListener {

    private Button buttonSettings = null;
    private Button buttonBack = null;
    //private Button buttonOffline = null;

    private WebServer webServer;
    private static String CESIUM_URI = "http://localhost:8080/";
    private WebView webView;


    private Context context = this;

    // the bird that was selected in a previous activity
    Bird selectedBird;

    private int pastDataPoints = 50;

    private ArrayList<Double> longitudes = new ArrayList<>();
    private ArrayList<Double> latitudes = new ArrayList<>();

    //private Locations pastTracks = new SingleTrajectory();

    // android.location, not ours
    Location userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cesium);

        LoadingIndicator.getInstance().hide();

        final Cesium cesium = this;

        this.buttonSettings = findViewById(R.id.navbar_button_settings);
        this.buttonBack = findViewById(R.id.navbar_button_back);
        //this.buttonOffline = findViewById(R.id.offline_btn);

        this.selectedBird = (Bird) getIntent().getSerializableExtra("selectedBird");

        registerEventHandlers(cesium);

        // Load the webserver.
        this.webServer = new WebServer(getAssets());

        try {
            // Dynamically add the input stream (a copy of webServerRoot/test.csv) before the server has started
            // Web Location: http://localhost:8080/copy.csv
            webServer.setVariableData("copy.csv", getAssets().open("webServerRoot/test.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Start the server
            this.webServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Dynamically add a String as a file after the server has started.
        // Web Location: http://localhost:8080/test.html
        webServer.setVariableData("test.html", "<!DOCTYPE HTML><html><head><title>test</title></head><body><h1>Test</h1></body></html>");

        SQLDatabase db = SQLDatabase.getInstance(this);
        BirdData birddata = db.getBirdData(2911040, 2911059);

        /*
        Locations tracks = birddata.getTrackingPoints();

        for (int i = 0; i < pastDataPoints; i++) {
            //loc_data[i] = tracks[size - 1 - pastDataPoints + i].getLocation().to3D();
            //pastTracks.add( tracks.get(tracks.length-1 - pastDataPoints + i).getLocation() );
        }
        */
        launchWebView(webView);

    }


    class JsObject {

        /**
         * return a json string containing the current user location.
         * Note: the default way to receive a location is letting the LocationManager
         * actively call a listener in the application.
         * However, we need to *provide* a method that will return the location.
         * We have a (locally defined) listener write the most recent location into
         * a field which is then accessed by a getter method.
         * We have the javascript regularly access this getter for location updates.
         *
         * I decided to not fetch location via javascript since that introduces
         * a range of different problems concerning javascript and the WebView.
         *
         * @return a JSON string containing lat and lng if a location fix has been
         * previously obtained. an empty JSON string otherwise.
         */
        @SuppressLint("MissingPermission") // todo
        @JavascriptInterface
        public String getUserLocationJSON() {
            JSONObject jo = new JSONObject();
            try {
                if (userLocation != null) {
                    jo.put("lat", userLocation.getLatitude());
                    jo.put("lng", userLocation.getLongitude());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jo.toString();
            // cf:
            // Building JSONObjects: https://stackoverflow.com/a/18983290/156884
            // the thing with the "builder pattern in java 7" does not work here.
            // https://duckduckgo.com/?q=javascript+parse+json&t=ffab&ia=qa
        }

    }


    // TODO (bm): replace this with locationListener
    @SuppressLint("MissingPermission") // we do take care of permissions
    private void registerLocationListener() {
        PermissionManager.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, R.string.dialog_permission_finelocation_text, PermissionManager.PERMISSION_FINE_LOCATION, (AppCompatActivity) context);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // fill class field with location.
                // location is accessed by e.g. getUserLocationJSON
                Log.i("location", location.toString());
                userLocation = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        locationListener.onLocationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener);
    }

    private void addValues() {
        longitudes.add(9.299999);
        longitudes.add(9.199999);
        longitudes.add(9.099999);
        longitudes.add(8.599999);

        latitudes.add(47.899999);
        latitudes.add(47.799999);
        latitudes.add(47.699999);
        latitudes.add(47.099999);
    }


    private void registerEventHandlers(final Cesium cesium){
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent buttonIntent =  new Intent(cesium, Settings.class);
                startActivity(buttonIntent);
            }
        });

    }

    public void launchBrowser(View view) {
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW);
        launchBrowser.setData(Uri.parse(CESIUM_URI));
        startActivity(launchBrowser);
    }

    // This works only on Lollipop or newer, as at least WebView v36 is required (WebGL support).
    // A correct fallback should maybe be implemented. ;)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    // we are aware that js is enabled in the webview, however we only access
    // local files and trust Cesium.
    @SuppressLint("SetJavaScriptEnabled")
    public void launchWebView(View view) {

        // Copied and modified from https://stackoverflow.com/questions/7305089/how-to-load-external-webpage-inside-webview#answer-7306176
        this.webView = findViewById(R.id.cesium_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JsObject(), "injectedObject");

        final Activity activity = this;

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
            }
        });

        // Load the URL
        webView.loadUrl(CESIUM_URI);

        registerLocationListener();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.webServer != null) {
            // Stop the server when the activity has been destroyed!
            // Otherwise it will stay on for long.
            this.webServer.stop();
        }
    }


    /*
     * Handlers for buttons floating on top of map
     */

    @Override
    public void onShowDataClick() {

    }

    @Override
    public void onShowPredClick() {

    }

    @Override
    public void onShowLocClick() {

    }

    @Override
    public void onSwitchModeClick() {
        finish(); // todo: do we really want this? maybe its better to keep the map activities
                  // so they dont have to be reloaded.
        Intent buttonIntent = new Intent(this, OSM_new.class);
        Log.i("Cesium", "selectedBird is null: " + (selectedBird == null));
        // pass currently selected bird to other activity.
        // this should be removed, instead the other activity
        // should fetch is data from a static field in controllers.PredictionWorkflow
        buttonIntent.putExtra("selectedBird", selectedBird);
        startActivity(buttonIntent);
    }

}

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
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import de.movabo.webserver.WebServer;
import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.algorithms_new.AlgorithmExtrapolationExtended;
import project.software.uni.positionprediction.algorithms_new.PredictionAlgorithm;
import project.software.uni.positionprediction.cesium.CesiumVisAdapter;
import project.software.uni.positionprediction.cesium.JSCaller;
import project.software.uni.positionprediction.controllers.PredictionWorkflow;
import project.software.uni.positionprediction.datatypes_new.Collection;
import project.software.uni.positionprediction.datatypes_new.EShape;
import project.software.uni.positionprediction.datatypes_new.PredictionUserParameters;
import project.software.uni.positionprediction.datatypes_new.Bird;
import project.software.uni.positionprediction.osm.OSMDroidVisualisationAdapter_new;
import project.software.uni.positionprediction.cesium.JSONUtils;
import project.software.uni.positionprediction.util.LoadingIndicator;
import project.software.uni.positionprediction.util.PermissionManager;
import project.software.uni.positionprediction.datatypes_new.Bird;
import project.software.uni.positionprediction.fragments.FloatingMapButtons;
import project.software.uni.positionprediction.util.XML;
import project.software.uni.positionprediction.visualisation_new.IVisualisationAdapter;
import project.software.uni.positionprediction.visualisation_new.TrajectoryVis;
import project.software.uni.positionprediction.visualisation_new.Visualisation;
import project.software.uni.positionprediction.visualisation_new.Visualisations;


/**
 * todo: TJ 180622: to be replaced by correct workflow
 */


public class Cesium extends AppCompatActivity implements FloatingMapButtons.floatingMapButtonsClickListener {

    private Button buttonSettings = null;
    private Button buttonBack = null;

    private WebServer webServer;
    private static String CESIUM_URI = "http://localhost:8080/";
    private WebView webView;

    CesiumVisAdapter visAdap;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cesium);

        LoadingIndicator.getInstance().hide();

        final Cesium cesium = this;
        visAdap = new CesiumVisAdapter();

        this.buttonSettings = findViewById(R.id.navbar_button_settings);
        this.buttonBack = findViewById(R.id.navbar_button_back);

        registerEventHandlers(cesium);


        // for dev/debug purposes
        // triggerTestingPrediction();


        // Load the webserver.
        this.webServer = new WebServer(getAssets());

        /*
        // dont need no csv
        try {
            // Dynamically add the input stream (a copy of webServerRoot/test.csv) before the server has started
            // Web Location: http://localhost:8080/copy.csv
            webServer.setVariableData("copy.csv", getAssets().open("webServerRoot/test.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        try {
            // Start the server
            this.webServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Dynamically add a String as a file after the server has started.
        // Web Location: http://localhost:8080/test.html
        webServer.setVariableData("test.html", "<!DOCTYPE HTML><html><head><title>test</title></head><body><h1>Test</h1></body></html>");

        launchWebView(webView);

    }

    private void triggerTestingPrediction() {

        XML xml = new XML();
        int hoursInPast = xml.getHours_past();

        // If used all data is clicked
        Date date_past;


        Calendar clp = Calendar.getInstance();
        //clp.setTime(new Date());
        clp.set(2005, 01, 01, 00, 00);
        date_past = clp.getTime();

        Calendar clf = Calendar.getInstance();
        clf.setTime(new Date());
        clf.add(Calendar.HOUR, 5);
        Date date_pred = clf.getTime();

        Bird bird = new Bird(2911059, 2911040, "Galapagos");


        // stub/useless vis adapter
        IVisualisationAdapter myVisAdap = new OSMDroidVisualisationAdapter_new();
        PredictionUserParameters predictionUserParameters = new PredictionUserParameters();
        predictionUserParameters.date_past = date_past;
        predictionUserParameters.date_pred = date_pred;
        predictionUserParameters.bird = bird;
        predictionUserParameters.algorithm = (PredictionAlgorithm) new AlgorithmExtrapolationExtended(context);


        PredictionWorkflow predWorkflow;
        predWorkflow = new PredictionWorkflow(
                this,
                predictionUserParameters,
                myVisAdap
        );
        predWorkflow.trigger();
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


        visAdap.linkMap(webView);

        registerOnPageLoadHandler();


    }

    /**
     * Functionality to be executed when the webView is done loading.
     */
    private void registerOnPageLoadHandler() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // todo: remove this
                // todo: call VisController with own visAdap

                if (PredictionWorkflow.vis_past == null) {
                    Log.e("cesium", "no vis_past set (yet?)");
                }
                // cant do that yet here because we have no synchronisation
                // with PredWfCtrl
                // visAdap.visualiseSingleTraj(PredictionWorkflow.vis_past);


                registerLocationListener(webView);
            }
        });
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
                  // todo: so they dont have to be reloaded.
        Intent buttonIntent = new Intent(this, OSM_new.class);
        startActivity(buttonIntent);
    }

    @Override
    public void onRefreshClick() {
        // this is here because when calculating the prediction
        // in this naive way we dont have a way to ensure that
        // the calculation is done (vis_past is set) before
        // this is called.
        // we will be able to move this into registerOnPageloadHandler()
        // when we have integrated the changes that the prediction is made
        // before the cesium activity is launched
        // TODO: implement recalculation of prediction
        if (PredictionWorkflow.vis_past == null) {
            Log.e("cesium", "no vis_past set (yet?)");
        }
        visAdap.visualiseSingleTraj(PredictionWorkflow.vis_past);
    }


    @SuppressLint("MissingPermission") // we do take care of permissions
    private void registerLocationListener(final WebView webView) {
        PermissionManager.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, R.string.dialog_permission_finelocation_text, PermissionManager.PERMISSION_FINE_LOCATION, (AppCompatActivity) context);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    JSCaller.callJS(webView, "updateLocation", JSONUtils.getAndroidLocationJSON(location).toString() );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

}

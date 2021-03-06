package project.software.uni.positionprediction.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import de.movabo.webserver.WebServer;
import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.cesium.CesiumVisAdapter;
import project.software.uni.positionprediction.cesium.JSCaller;
import project.software.uni.positionprediction.cesium.JSONUtils;
import project.software.uni.positionprediction.controllers.PredictionWorkflow;
import project.software.uni.positionprediction.controllers.VisualizationWorkflow;
import project.software.uni.positionprediction.fragments.FloatingMapButtons;
import project.software.uni.positionprediction.util.AsyncTaskCallback;
import project.software.uni.positionprediction.util.LoadingIndicator;
import project.software.uni.positionprediction.util.LocationProvider;
import project.software.uni.positionprediction.util.Message;

public class Cesium extends AppCompatActivity implements FloatingMapButtons.floatingMapButtonsClickListener {

    private Button buttonSettings = null;
    private Button buttonBack = null;

    private WebServer webServer;
    private static String CESIUM_URI = "http://localhost:8080/";
    private WebView webView;

    CesiumVisAdapter visAdap;

    private Context context = this;
    private boolean isWebViewLoaded = false;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            if (location == null) return;

            try {
                JSCaller.callJS(webView, "updateLocation", JSONUtils.getAndroidLocationJSON(location).toString() );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            // noop
        }

        @Override
        public void onProviderEnabled(String s) {
            // noop
        }

        @Override
        public void onProviderDisabled(String s) {
            // noop
            // potentially show error message or remove location icon
        }
    };


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

        try {
            // Start the server
            this.webServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        launchWebView();

        // make WebView Remote-Debuggable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE))
            { WebView.setWebContentsDebuggingEnabled(true); }
        }


    }

    @Override
    public void onResume(){
        super.onResume();

        Message.show_pending_messages(this);

        if ( !visAdap.areVisCurrent(PredictionWorkflow.vis_past, PredictionWorkflow.vis_pred)
                && isWebViewLoaded) {
            // dont call in case the activity is just launched and the web view is not ready yet.
            onRefreshClick();

        }

        LocationProvider.registerLocationListener(context, this.locationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationProvider.clearLocationListener(this.locationListener);
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
    public void launchWebView() {

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

        registerLocationListener(webView);


    }

    /**
     * Functionality to be executed when the webView is done loading.
     */
    private void registerOnPageLoadHandler() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                isWebViewLoaded = true;

                VisualizationWorkflow visWorkflow = new VisualizationWorkflow(
                        context,
                        visAdap,
                        PredictionWorkflow.vis_past,
                        PredictionWorkflow.vis_pred);
                visWorkflow.trigger();

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
        if(this.webView != null){
            webView.destroy();
            webView = null;
            // detach the location listener because there will be no more
            // webview to act on
            LocationProvider.clearLocationListener(this.locationListener);
        }
    }


    /*
     * Handlers for buttons floating on top of map
     */

    @Override
    public void onShowDataClick() {
        visAdap.showData();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onShowPredClick() {
        visAdap.showPrediction();
    }

    @Override
    public void onShowLocClick() {
        JSCaller.callJS(this.webView, "panToUserLoc", null);
    }

    @Override
    public void onSwitchModeClick() {
        // improvement: do we really want to finish the activity? maybe it would
        // be better to leave it alive.
        finish();
        Intent buttonIntent = new Intent(this, OSM.class);
        startActivity(buttonIntent);
    }

    @Override
    public void onRefreshClick() {
        PredictionWorkflow.getInstance(context).refreshPrediction(context, new AsyncTaskCallback(){
            @Override
            public void onFinish() {
                visAdap.clearVis();

                // call visualisation
                VisualizationWorkflow visWorkflow = new VisualizationWorkflow(
                        context,
                        visAdap,
                        PredictionWorkflow.vis_past,
                        PredictionWorkflow.vis_pred);
                visWorkflow.trigger();
            }

            @Override
            public void onCancel() {
                Log.e("Cesium", "prediction calculation task/thread interrupted");
            }

            @Override
            public Context getContext() {
                return context;
            }
        });
    }


    @SuppressLint("MissingPermission") // we do take care of permissions
    private void registerLocationListener(final WebView webView) {

        LocationProvider.registerLocationListener(context, this.locationListener);
    }

}

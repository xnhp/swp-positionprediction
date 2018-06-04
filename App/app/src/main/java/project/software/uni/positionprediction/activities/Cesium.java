package project.software.uni.positionprediction.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import de.movabo.webserver.WebServer;
import project.software.uni.positionprediction.R;


/**
 * (Nearly) Minimal working example for running cesium with a webserver inside the browser or
 * inside a webview.
 */


public class Cesium extends AppCompatActivity {

    private Button buttonSettings = null;
    private Button buttonBack = null;

    private WebServer webServer;
    private static String CESIUM_URI = "http://localhost:8080/";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cesium);


        this.buttonSettings = (Button) findViewById(R.id.navbar_button_settings);
        this.buttonBack = (Button) findViewById(R.id.navbar_button_back);

        final Cesium cesium = this;

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
    }

    public void launchBrowser(View view) {
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW);
        launchBrowser.setData(Uri.parse(CESIUM_URI));
        startActivity(launchBrowser);
    }

    // This works only on Lollipop or newer, as at least WebView v36 is required (WebGL support).
    // A correct fallback should maybe be implemented. ;)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void launchWebView(View view) {

        // Copied from https://stackoverflow.com/questions/7305089/how-to-load-external-webpage-inside-webview#answer-7306176
        this.webView = new WebView(this);
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

        // Load the URL and set the view.
        webView.loadUrl(CESIUM_URI);
        setContentView(webView);
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

}

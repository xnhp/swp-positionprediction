package project.software.uni.positionprediction.cesium;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.webkit.WebView;

import org.json.JSONException;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import project.software.uni.positionprediction.controllers.PredictionWorkflow;
import project.software.uni.positionprediction.visualisation.IVisualisationAdapter;
import project.software.uni.positionprediction.visualisation.TrajectoryVis;

public class CesiumVisAdapter extends IVisualisationAdapter {

    private WebView webView;
    private String LogTag = "CesiumVisAdapter";

    /**
     * Save a reference to the webView for use in other methods.
     * @param webView
     */
    @Override
    public void linkMap(Object webView) {
        this.webView = (WebView) webView;
    }

    @Override
    public void visualiseSingleTraj(TrajectoryVis vis) {
        try {
            String json = JSONUtils.getSingleTrajJSON(vis).toString();
            callJS("visualiseSingleTraj", json);
        } catch (JSONException e) {
            Log.e(LogTag, "Could not JSONify data");
            e.printStackTrace();
        }

    }

    @Override
    public void setMapCenter(GeoPoint centerWithDateLine) {
        try {
            callJS("setCenter", JSONUtils.getGeoPointJSON(centerWithDateLine).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clear() {
        // todo
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void showVisualisation() {
        try {

            BoundingBox bb1 = PredictionWorkflow.vis_past.getBoundingBox();
            BoundingBox bb2 = PredictionWorkflow.vis_pred.getBoundingBox();
            BoundingBox targetBB = bb1.concat(bb2);

            BoundingBox bb = getSafeBoundingBox(targetBB);

            String bbJson = JSONUtils.getBoundingBoxJSON(bb).toString();

            callJS("panToBoundingBox", bbJson);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showData() {
        try {

            BoundingBox bb = getSafeBoundingBox(
                    PredictionWorkflow.vis_past.getBoundingBox()
            );

            String bbJson = JSONUtils.getBoundingBoxJSON(bb).toString();

            callJS("panToBoundingBox", bbJson);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N) // todo
    @Override
    public void showPrediction() {
        try {
            BoundingBox bb = getSafeBoundingBox(
                    PredictionWorkflow.vis_pred.getBoundingBox()
            );

            String bbJson = JSONUtils.getBoundingBoxJSON(bb).toString();

            callJS("panToBoundingBox", bbJson);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void callJS(String methodName, String jsonData) {
        // Additional argument of webView
        JSCaller.callJS(this.webView, methodName, jsonData);
    }
}

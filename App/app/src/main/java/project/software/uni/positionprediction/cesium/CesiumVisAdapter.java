package project.software.uni.positionprediction.cesium;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.webkit.WebView;

import org.json.JSONException;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import project.software.uni.positionprediction.controllers.PredictionWorkflow;
import project.software.uni.positionprediction.visualisation.CloudVis;
import project.software.uni.positionprediction.visualisation.IVisualisationAdapter;
import project.software.uni.positionprediction.visualisation.Polyline;
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

        if(vis.hasFunnel()) Log.e("funnel", "found");

    }

    @Override
    public void drawTrajectoryConnection(Polyline pline) {
        try {
            String json = JSONUtils.getPolyLineJSON(pline).toString();
            callJS("drawTrajectoryConnection",json );
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // again, a prediction might result in several cloud vis's
    // but the visualisationWorkflow unwraps that and calls this
    // once for each cloud
    public void visualiseSingleCloud(CloudVis vis) {
        try {
            String json = JSONUtils.getSingleCloudJSON(vis).toString();
            callJS("visualiseSingleCloud", json);
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

            if (PredictionWorkflow.vis_past == null
                    || PredictionWorkflow.vis_pred == null) return;

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

        if (PredictionWorkflow.vis_past == null) return;

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

        if (PredictionWorkflow.vis_past == null) return;

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

    public void clearVis() {
        callJS("clearVis", null);
    }


    private void callJS(String methodName, String jsonData) {
        // Additional argument of webView
        JSCaller.callJS(this.webView, methodName, jsonData);
    }
}

package project.software.uni.positionprediction.cesium;

import android.util.Log;
import android.webkit.WebView;

import org.json.JSONException;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import project.software.uni.positionprediction.visualisation.IVisualisationAdapter;
import project.software.uni.positionprediction.visualisation.TrajectoryVis;

public class CesiumVisAdapter extends IVisualisationAdapter {

    WebView webView;
    String LogTag = "CesiumVisAdapter";

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
    public void panToBoundingBox(BoundingBox boundingBox) {
        try {
            callJS("panToBoundingBox", JSONUtils.getBoundingBoxJSON(boundingBox).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clear() {
        // todo
    }

    private void callJS(String methodName, String jsonData) {
        JSCaller.callJS(this.webView, methodName, jsonData);
    }
}

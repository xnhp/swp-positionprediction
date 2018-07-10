package project.software.uni.positionprediction.cesium;

import android.webkit.WebView;

public class JSCaller {
    /**
     * Call the specified JS function with methodName
     * with jsonData as a String argument.
     *
     * As of now, no check is made whether that method
     * is actually defined in the JS.
     *
     * Potentially, one could make this more dynamic
     * by writing a java object that exhibits fake
     * methods that just call this method.
     *
     * @param methodName
     * @param jsonData
     */
    public static void callJS(WebView webView, String methodName, String jsonData) {
        String callString = methodName + "(" + "'" + jsonData + "'" + ")" + ";";

        webView.evaluateJavascript(callString, null);
    }
}

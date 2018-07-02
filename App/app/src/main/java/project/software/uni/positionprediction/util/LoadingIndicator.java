package project.software.uni.positionprediction.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import project.software.uni.positionprediction.R;

/**
 * Created by simon on 29.06.18.
 */

/**
 * This Class creates a pupup with a loading indicator
 */
public class LoadingIndicator {

    private ProgressDialog dialog = null;

    private static final LoadingIndicator ourInstance = new LoadingIndicator();

    public static LoadingIndicator getInstance() {
        return ourInstance;
    }

    private LoadingIndicator() {

    }

    /**
     * This Method shows a loading-Indicator if it is not already visible.
     * You can call this Method from any Thread
     * @param context The Context of the Activity to show the loading-Indicator for
     */
    public void show(final Context context){

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                create_dialog(context);
            }
        });
    }

    private synchronized void create_dialog(final Context context){
        if(dialog == null) {
            dialog = new ProgressDialog(context);
            dialog.setMessage(context.getResources().getString(R.string.loading_indicator_text));
            dialog.setCancelable(false);
            dialog.show();

        }
    }

    /**
     * This Method hides the loading-Indicator if it is visible
     * You can call this Method from any Thread
     */
    public void hide(){

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        });
    }

    private synchronized void dismiss(){
        if(dialog != null){
            dialog.dismiss();

            dialog = null;
        }
    }
}

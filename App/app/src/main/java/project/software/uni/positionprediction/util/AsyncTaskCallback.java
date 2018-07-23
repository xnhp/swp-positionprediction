package project.software.uni.positionprediction.util;

import android.content.Context;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.controllers.BadDataException;
import project.software.uni.positionprediction.controllers.RequestFailedException;

/**
 * Can be called by a background task
 * e.g. by PredWfCtrl.
 */
public abstract class AsyncTaskCallback {
    abstract public void onFinish();
    abstract public void onCancel();

    // using this, we can put all this UI
    // related code here and still work in the
    // correct context (that where the callback
    // is)
    abstract public Context getContext();

    public void onException(Exception e) {
        Context c = getContext();
        if (e instanceof BadDataException) {
            int percentage_bad_data = ((BadDataException) e).percentage_bad_data;

            Message.disp_error_handled(c,
                    c.getResources().getString(R.string.percentage_bad_date_warning),
                    c.getResources().getString(R.string.percentage_bad_data_warning_dialog,
                            percentage_bad_data));
        } else if (e instanceof RequestFailedException) {
            Message.disp_error_handled(c,
                    c.getResources().getString(R.string.data_network_request_failed_warning_title),
                    c.getResources().getString(R.string.data_network_request_failed_warning_text));
        }
    }


    public void onWarning(final String title, final String message) {
        Message.disp_error_handled(getContext(), title, message);
    }



}

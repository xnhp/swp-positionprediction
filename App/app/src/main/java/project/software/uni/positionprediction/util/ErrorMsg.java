package project.software.uni.positionprediction.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

public class ErrorMsg {


    /**
     * Example Code for an error
     *
     *      ErrorMsg e = new ErrorMsg();
     *      e.disp(this,
     *              "Error 007",
     *              "Couldn't download data without internet connection. Check your connection.",
     *              true);
     *
     */


    public ErrorMsg() {

    }

    public void disp (Context c, String errorTitle, String errorMsg, boolean button) {
        if (errorTitle == null) {
            Log.e("Warning", "No error title");
        }
        if (errorMsg == null) {
            Log.e("Warning", "No error message");
        }


        AlertDialog.Builder alert = new AlertDialog.Builder(c);

        if (button) {
            alert.setMessage
                    (
                            errorMsg
                    )
                    .setPositiveButton("Continue", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }
                    )
                    .setTitle(errorTitle)
                    .create();
            alert.show();
        } else {
            alert.setMessage
                    (
                            errorMsg
                    )
                    .setTitle(errorTitle)
                    .create();
            alert.show();
        }
    }
}

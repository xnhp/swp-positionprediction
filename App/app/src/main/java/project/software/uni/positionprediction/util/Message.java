package project.software.uni.positionprediction.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import project.software.uni.positionprediction.R;

public class Message {




    public Message() {

    }



    /**
     * Example Code for an error
     *
     *      ErrorMsg e = new ErrorMsg();
     *      e.disp(this,
     *              "Error",
     *              "Couldn't download data without internet connection. Check your connection.",
     *              true);
     *
     */
    public static void disp_error (Context c, String errorTitle, String errorMsg, boolean button) {
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
                    //.setIcon(R.id.)
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


    /**
     * Success message
     * @param c
     * @param title
     * @param msg
     */
    public void disp_success (Context c, String title, String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(c);
        alert.setMessage
                (
                        msg
                )
                //.setIcon(R.drawable.)
                .setTitle(title)
                .create();
        alert.show();
    }


    /**
     * Download message
     * @param c
     * @param dwTitle
     * @param dwMessage
     */
    public void disp_download (Context c, String dwTitle, String dwMessage) {
        AlertDialog.Builder alert = new AlertDialog.Builder(c);
            alert.setMessage
                    (
                            dwMessage
                    )
                    .setIcon(R.drawable.download)
                    .setTitle(dwTitle)
                    .create();
            alert.show();
    }







}

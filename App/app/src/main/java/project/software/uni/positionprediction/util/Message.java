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
     * Display a message to the user, together with two options.  either calls a different action.
     */
    public static void disp_choice(Context c, String title, String message,
                            String positiveLabel,
                            DialogInterface.OnClickListener positiveCallback,
                            String negativeLabel,
                            DialogInterface.OnClickListener negativeCallback) {
        AlertDialog.Builder alert = new AlertDialog.Builder(c);
        alert.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveLabel, positiveCallback);
        if (negativeCallback!=null && negativeLabel!=null) {
            alert.setNegativeButton(negativeLabel, negativeCallback);
        } else {
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // noop
                }
            });
        }
        alert.show();
    }

    /**
     * Like disp_choice, except there is no negative option
     * @param c
     * @param title
     * @param message
     */
    public void disp_confirm(Context c, String title, String message, String positiveLabel,
                             DialogInterface.OnClickListener positiveCallback) {
        disp_choice(c, title, message, positiveLabel, positiveCallback,null, null);
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
    public static void disp_error(final Context c, final String errorTitle, final String errorMsg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (errorTitle == null) {
                    Log.e("Warning", "No error title");
                }
                if (errorMsg == null) {
                    Log.e("Warning", "No error message");
                }

                AlertDialog.Builder alert = new AlertDialog.Builder(c);

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
            }
        });


    }



    public static void disp_wait (Context c, String title, String msg) {
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


    public static void disp_success (Context c, String title, String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(c);
            alert.setMessage
                    (
                            msg
                    )
                    //.setIcon(R.drawable.bonuspack_bubble)
                    .setTitle(title)
                    .create();
            alert.show();
    }




}

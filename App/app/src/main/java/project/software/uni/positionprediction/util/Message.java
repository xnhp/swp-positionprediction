package project.software.uni.positionprediction.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.w3c.dom.DOMStringList;

import java.util.Iterator;
import java.util.LinkedList;

import project.software.uni.positionprediction.R;

public class Message {

    private static LinkedList<MessageEntity> messageList = new LinkedList<>();


    public Message() {

    }

    /**
     * Display a message to the user, together with two options.  either calls a different action.
     */
    public static void disp_choice(Context c, String title, String message,
                                   final String positiveLabel,
                                   final DialogInterface.OnClickListener positiveCallback,
                                   final String negativeLabel,
                                   final DialogInterface.OnClickListener negativeCallback) {
        AlertDialog.Builder alert = new AlertDialog.Builder(c);

        final MessageEntityHandlers entity = new MessageEntityHandlers();

        alert.setTitle(title).setMessage(message);

        alert.setPositiveButton(positiveLabel == null ? "OK" : positiveLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // noop
                messageList.remove(entity);
                if(positiveCallback != null && positiveLabel != null) positiveCallback.onClick(dialogInterface, i);
            }
        });

        alert.setNegativeButton(negativeLabel == null ? "Cancel" : negativeLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // noop
                messageList.remove(entity);
                if (negativeCallback!=null && negativeLabel!=null) negativeCallback.onClick(dialogInterface, i);
            }
        });

        entity.dialog = alert.show();
        entity.title = title;
        entity.message = message;
        entity.type = MessageType.CHOICE;
        entity.negativeCallback = negativeCallback;
        entity.positiveCallback = positiveCallback;
        entity.negativeLabel = negativeLabel;
        entity.positiveLabel = positiveLabel;

        messageList.add(entity);

    }

    /**
     * Like disp_choice, except there is no negative option
     * @param c
     * @param title
     * @param message
     */
    public static void disp_confirm(Context c, String title, String message, String positiveLabel,
                             DialogInterface.OnClickListener positiveCallback) {
        disp_choice(c, title, message, positiveLabel, positiveCallback,null, null);
        messageList.getLast().type = MessageType.CONFIRM;
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
        if (c == null) {
            Log.e("Warning", "No context for disp_error-method");
        }
        if (errorTitle == null) {
            Log.e("Warning", "No error title");
        }
        if (errorMsg == null) {
            Log.e("Warning", "No error message");
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(c);

        final MessageEntity entity = new MessageEntity();

        alert.setMessage
                (
                        errorMsg
                )
                .setPositiveButton("Continue", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                messageList.remove(entity);
                                dialog.dismiss();
                            }
                        }
                )
                .setTitle(errorTitle)
                .create();

        entity.dialog = alert.show();
        entity.title = errorTitle;
        entity.message = errorMsg;
        entity.type = MessageType.ERROR;

        messageList.add(entity);

    }



    public static void disp_wait (Context c, String title, String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(c);

        final MessageEntity entity = new MessageEntity();

        alert.setMessage
                (
                        msg
                )
                //.setIcon(R.drawable.)
                .setTitle(title).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                messageList.remove(entity);
            }
        }).create();

        entity.dialog = alert.show();
        entity.title = title;
        entity.message = msg;
        entity.type = MessageType.WAIT;

        messageList.add(entity);


    }


    /**
     * Download message
     * @param c
     * @param dwTitle
     * @param dwMessage
     */
    public static void disp_download (Context c, String dwTitle, String dwMessage) {
        AlertDialog.Builder alert = new AlertDialog.Builder(c);

        final MessageEntity entity = new MessageEntity();
        alert.setMessage
                (
                        dwMessage
                )
                .setIcon(R.drawable.download)
                .setTitle(dwTitle).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                messageList.remove(entity);
            }
        })
                .create();


        entity.dialog = alert.show();
        entity.title = dwTitle;
        entity.message = dwTitle;
        entity.type = MessageType.DOWNLOAD;

        messageList.add(entity);

    }


    public static void disp_success (Context c, String title, String msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(c);

        final MessageEntity entity = new MessageEntity();
        alert.setMessage
                    (
                            msg
                    )
                    //.setIcon(R.drawable.bonuspack_bubble)
                    .setTitle(title).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                messageList.remove(entity);
            }
        }).create();

        entity.dialog = alert.show();
        entity.title = title;
        entity.message = msg;
        entity.type = MessageType.SUCCESS;

        messageList.add(entity);
    }



    public static void disp_error_asynch(final Context c, final String errorTitle, final String errorMsg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                if (c == null) {
                    Log.e("Warning", "No context for disp_error-method");
                }
                if (errorTitle == null) {
                    Log.e("Warning", "No error title");
                }
                if (errorMsg == null) {
                    Log.e("Warning", "No error message");
                }

                AlertDialog.Builder alert = new AlertDialog.Builder(c);

                final MessageEntity entity = new MessageEntity();

                alert.setMessage
                        (
                                errorMsg
                        )
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        messageList.remove(entity);
                                        dialog.dismiss();
                                    }
                                }
                        )
                        .setTitle(errorTitle)
                        .create();

                entity.dialog = alert.show();
                entity.title = errorTitle;
                entity.message = errorMsg;
                entity.type = MessageType.ERROR;

                messageList.add(entity);


            }
        });



    }


    static Handler handler = new Handler();

    public static void disp_error_handled(final Context c, final String title, final String message) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                Message.disp_error_asynch(c, title, message);
            }
        });
    }

    public static void show_pending_messages(Context ctx){

        LinkedList<MessageEntity> list = messageList;
        messageList = new LinkedList<>();

        Iterator<MessageEntity> iter = list.iterator();

        // todo: weird exception when using similiartrajfunnelalgorithm

        if (list == null) {
            return;
        }
        if (list.size() == 0) {
            return;
        }


        while (iter.hasNext()) {
            MessageEntity e = iter.next();

            if(e.dialog != null){
                e.dialog.dismiss();
                e.dialog = null;
            }

            switch(e.type){
                case ERROR:
                    disp_error(ctx, e.title, e.message);
                    break;
                case SUCCESS:
                    disp_success(ctx, e.title, e.message);
                    break;
                case WAIT:
                    disp_wait(ctx, e.title, e.message);
                    break;
                case DOWNLOAD:
                    disp_download(ctx, e.title, e.message);
                    break;
                case CONFIRM:
                    disp_confirm(ctx, e.title, e.message,
                            ((MessageEntityHandlers)e).positiveLabel,
                            ((MessageEntityHandlers)e).positiveCallback);
                    break;
                case CHOICE:
                    disp_choice(ctx, e.title, e.message,
                            ((MessageEntityHandlers)e).positiveLabel,
                            ((MessageEntityHandlers)e).positiveCallback,
                            ((MessageEntityHandlers)e).negativeLabel,
                            ((MessageEntityHandlers)e).negativeCallback);
                    break;
            }

            iter.remove();
        }
    }
}

enum MessageType{
    ERROR,
    SUCCESS,
    WAIT,
    DOWNLOAD,
    CONFIRM,
    CHOICE
}

class MessageEntity{
    protected String title;
    protected String message;
    protected MessageType type;

    AlertDialog dialog = null;
}

class MessageEntityHandlers extends MessageEntity{
    DialogInterface.OnClickListener positiveCallback = null;
    String positiveLabel = null;
    DialogInterface.OnClickListener negativeCallback = null;
    String negativeLabel = null;
}


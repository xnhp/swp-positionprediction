package project.software.uni.positionprediction.util;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import project.software.uni.positionprediction.R;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * The place to come to to get yourself some permissions.
 * Sample Usage:
 *  PermissionManager.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.dialog_permission_storage_text, (AppCompatActivity) context);
 * See https://developer.android.com/training/permissions/requesting#java
 */
public class PermissionManager implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final String loggingTag = "PermissionManager";
    private static final String loggingPrefix = loggingTag + ": ";

    private static final int PERM_REQ_MAP = 1;

    /**
     * This Method tells if a certain permission is granted
     * @param permission Permission to ask for
     * @param context the app context
     * @return true if the permission is granted
     */
    public static boolean hasPermission(String permission, Context context){
        return (ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED);
    }

    /**
     * This Method requests a specific Permission
     * @param permission Permission to ask for
     * @param message The id of the String Resource to be displayed in the alert box
     * @param activity The activity that asks for the Permission
     * TODO: What happens if permission cant be obtained? (i.e. user declines)
     * TODO: When executing a task that triggers this, does executing that task have to be tried \
     *       again after the permission was granted?
     */
    public static void requestPermission(final String permission, int message, final AppCompatActivity activity){

        Log.d(loggingTag, "Permission for " + permission + " requested");

        // check if the permission is already granted
        if(!hasPermission(permission, activity)) {

            Log.d(loggingTag, "permission not yet granted");

            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
                Log.d(loggingTag, "showing rationale");
                // show alert that explains why the Permission is needed
                AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle(activity.getResources().getString(R.string.dialog_permission_title))
                        .setMessage(activity.getResources().getString(message))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(loggingTag, "requesting permission after rationale");
                                // request the needed Permission
                                ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }else {
                Log.d(loggingTag, "requesting permission without rationale");
                // request the needed Permission
                // We leave the parameter requestCode at 0 even for requests for different rights
                // because as of now we have no need for being able to tell where permission
                // requests come from.
                ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
            }
        } else {
            Log.d(loggingTag, "permission for " + permission + " already granted");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            // use requestCode 0 for "dont care"
            case 0: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PERMISSION_GRANTED) {
                        Log.d(loggingTag, "A permission was granted: " + permissions[i]);
                    }
                    if (grantResults[i] == PERMISSION_DENIED) {
                        Log.d(loggingTag, "A permission was denied: " + permissions[i]);
                    }
                }
            }
            // might be extended in the future
        }
    }
}

package project.software.uni.positionprediction.util;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import project.software.uni.positionprediction.R;

/**
 * Created by simon on 11.05.18.
 */

public class PermissionManager {

    /**
     * This Method tells if a certain permission is granted
     * @param permission Permission to ask for
     * @param context the app context
     * @return true if the permission is granted
     */
    public static boolean hasPermission(String permission, Context context){
        return (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * This Method requests a specific Permission
     * @param permission Permission to ask for
     * @param message The id of the String Resource to be displayed in the alert box
     * @param activity The activity that asks for the Permission
     * TODO: What happens if permission cant be obtained?
     * TODO: When executing a task that triggers this, does executing that task have to be tried
     *       again after the permission was granted?
     */
    public static void requestPermission(final String permission, int message, final AppCompatActivity activity){

        // check if the permission is already granted
        if(ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){

                // show alert that explains why the Permission is needed
                AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle(activity.getResources().getString(R.string.dialog_permission_title))
                        .setMessage(activity.getResources().getString(message))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }else {
                // request the needed Permission
                ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
            }
        }
    }

}

package project.software.uni.positionprediction.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.datatype.Bird;
import project.software.uni.positionprediction.datatype.Study;
import project.software.uni.positionprediction.movebank.SQLDatabase;
import project.software.uni.positionprediction.util.PermissionManager;

public class BirdSelect extends AppCompatActivity {

    private static Context context;

    private Button buttonSelect = null;
    private Button buttonOpenMap = null;

    private LinearLayout scrollViewLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird_select);

        scrollViewLayout = findViewById(R.id.birdselect_scrollview);

        buttonSelect = findViewById(R.id.birdselect_button_select);

        buttonOpenMap = findViewById(R.id.birdselect_button_openmap);

        final BirdSelect birdSelect = this;

        // QUESTION: Why is this here and not in other activities?
        BirdSelect.context = getApplicationContext();

        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buttonIntent =  new Intent(birdSelect, Settings.class);
                startActivity(buttonIntent);
            }
        });

        buttonOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionManager.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, birdSelect) &&
                        PermissionManager.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION, birdSelect)) {
                    Intent mapIntent = new Intent(birdSelect, OSM.class);
                    startActivity(mapIntent);
                    return;
                }
                if(!PermissionManager.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, birdSelect)) {
                    PermissionManager.requestPermission(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            R.string.dialog_permission_storage_text,
                            PermissionManager.PERMISSION_STORAGE,
                            birdSelect);
                }else {
                    if (!PermissionManager.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION, birdSelect)) {
                        PermissionManager.requestPermission(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                R.string.dialog_permission_finelocation_text,
                                PermissionManager.PERMISSION_FINE_LOCATION,
                                birdSelect);
                    }
                }

            }
        });


        // request the studies that are already in the database
        Study studies[] = SQLDatabase.getInstance(this).getStudies();
        fillStudiesList(studies);


        new Thread(new Runnable() {
            @Override
            public void run() {

                // update the studies in the database2911059
                SQLDatabase.getInstance(birdSelect).updateStudiesSync();

                SQLDatabase.getInstance(birdSelect).updateBirdDataSync(2911040, 2911059);

                // update the study list
                final Study studies[] = SQLDatabase.getInstance(birdSelect).getStudies();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        fillStudiesList(studies);
                    }
                });

                Bird bird = SQLDatabase.getInstance(birdSelect).getBirdData(2911040, 2911059);

            }
        }).start();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionManager.PERMISSION_STORAGE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(!PermissionManager.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION, this))
                    {
                        PermissionManager.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                                R.string.dialog_permission_finelocation_text,
                                PermissionManager.PERMISSION_FINE_LOCATION,
                                this);
                    }else{
                        Intent mapIntent = new Intent(this, OSM.class);
                        startActivity(mapIntent);
                    }
                }

                else {
                    // TODO: dispaly error message for permissions
                }
                return;
            }
            case PermissionManager.PERMISSION_FINE_LOCATION:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(PermissionManager.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, this)){
                        Intent mapIntent = new Intent(this, OSM.class);
                        startActivity(mapIntent);
                    }
                }

                else {
                    // TODO: dispaly error message for permissions
                }
                return;
        }
    }

    public void fillStudiesList(Study[] studies){

        for(int i = 0; i < studies.length; i++){
            TextView textView = new TextView(this);
            textView.setText(studies[i].name);
            scrollViewLayout.addView(textView);
        }

    }

    public static Context getAppContext() {
        return BirdSelect.context;
    }

}

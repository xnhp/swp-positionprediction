package project.software.uni.positionprediction.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.algorithm.AlgorithmExtrapolationExtended;
import project.software.uni.positionprediction.datatype.Bird;
import project.software.uni.positionprediction.datatype.Location3D;
import project.software.uni.positionprediction.datatype.Study;
import project.software.uni.positionprediction.movebank.SQLDatabase;
import project.software.uni.positionprediction.util.PermissionManager;

public class BirdSelect extends AppCompatActivity {

    private static Context context;

    private Button buttonSelect = null;
    private Button buttonOpenMap = null;
    private Button buttonOpenCesium = null;

    private EditText editTextSearch = null;

    private LinearLayout scrollViewLayout = null;

    private final static int BIRD_SELECT = 1;
    private final static  int STUDY_SELECT = 2;

    private int state;
    private int selectedStudy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird_select);

        scrollViewLayout = findViewById(R.id.birdselect_scrollview);

        editTextSearch = findViewById(R.id.birdselect_edittext_search);

        buttonSelect = findViewById(R.id.birdselect_button_select);

        buttonOpenMap = findViewById(R.id.birdselect_button_openmap);

        buttonOpenCesium = findViewById(R.id.birdselect_button_opencesium);

        state = STUDY_SELECT;

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

        buttonOpenCesium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cesiumIntent =  new Intent(birdSelect, Cesium.class);
                startActivity(cesiumIntent);
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(state == STUDY_SELECT)
                    fillStudiesList(SQLDatabase.getInstance(birdSelect).searchStudie(
                            editTextSearch.getText().toString()));
                else if(state == BIRD_SELECT)
                    fillBirdsList(SQLDatabase.getInstance(birdSelect).searchBird(
                            selectedStudy,
                            editTextSearch.getText().toString()
                    ));
            }
        });


        // request the studies that are already in the database
        Study studies[] = SQLDatabase.getInstance(this).getStudies();
        fillStudiesList(studies);


        new Thread(new Runnable() {
            @Override
            public void run() {

                // update the studies in the database
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

    public void fillStudiesList(final Study[] studies){

        scrollViewLayout.removeAllViews();

        for(int i = 0; i < studies.length; i++){
            TextView textView = new TextView(this);
            textView.setPadding(50, 50, 50, 50);
            textView.setText(studies[i].name);

            final int index = i;

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    studySelected(studies[index]);
                    state = BIRD_SELECT;
                    selectedStudy = studies[index].id;
                    editTextSearch.setText("");
                }
            });

            scrollViewLayout.addView(textView);
        }

        scrollViewLayout.invalidate();

    }

    public void studySelected(final Study study){
        Bird birds[] = SQLDatabase.getInstance(this).getBirds(study.id);
        if(birds.length > 0) fillBirdsList(birds);

        final BirdSelect birdSelect = this;

        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLDatabase.getInstance(birdSelect).updateBirdsSync(study.id);
                final Bird birds[] = SQLDatabase.getInstance(birdSelect).getBirds(study.id);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(birds.length > 0) fillBirdsList(birds);
                        else {
                            // TODO: dispaly warning (no accessable birds for study)
                        }
                    }
                });
            }
        }).start();
    }

    public void fillBirdsList(final Bird birds[]){

        scrollViewLayout.removeAllViews();

        for(int i = 0; i < birds.length; i++){
            TextView textView = new TextView(this);
            textView.setPadding(50, 50, 50, 50);
            if(birds[i].getNickName() == null){
                textView.setText(birds[i].getId() + "");
            }
            else {
                textView.setText(birds[i].getNickName() + " (" + birds[i].getId() + ")");
            }

            final int index = i;


            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBirdSelected(birds[index]);
                }
            });

            scrollViewLayout.addView(textView);
        }

        scrollViewLayout.invalidate();
    }

    private void onBirdSelected(final Bird bird){

        final BirdSelect birdSelect = this;

        Intent showOn2DMap = new Intent(this, OSM.class);
        showOn2DMap.putExtra("bird", bird);
        startActivity(showOn2DMap);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                SQLDatabase.getInstance(birdSelect).updateBirdData(bird.getStudyId(), bird.getId());
//
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
////                        AlgorithmExtrapolationExtended algo = new AlgorithmExtrapolationExtended(birdSelect);
////                        LinkedList<Location3D> list = algo.predict(null, null, bird.getStudyId(), bird.getId());
////                        Log.e("Result", ""+ list.get(0).getLoc_long() );
//                    }
//                });
//            }
//        }).start();

    }

    public static Context getAppContext() {
        return BirdSelect.context;
    }

    @Override
    public void onBackPressed() {
        switch(state){
            case STUDY_SELECT:
                this.finish();
                break;
            case BIRD_SELECT:
                final BirdSelect birdSelect = this;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        fillStudiesList(SQLDatabase.getInstance(birdSelect).getStudies());
                    }
                });
                editTextSearch.setText("");
                state = STUDY_SELECT;
                break;
            default:
                super.onBackPressed();
        }
    }

}

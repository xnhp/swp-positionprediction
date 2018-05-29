package project.software.uni.positionprediction.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.datatype.Study;
import project.software.uni.positionprediction.movebank.CSVParser;
import project.software.uni.positionprediction.movebank.MovebankConnector;
import project.software.uni.positionprediction.movebank.RequestHandler;
import project.software.uni.positionprediction.movebank.SQLDatabase;

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
                //Log.e("Movebank: ", MovebankConnector.getInstance(getAppContext()).getBirdData(22390461, 102937685));
            }
        });

        buttonOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(birdSelect, OSM.class);
                startActivity(mapIntent);
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

                // update the study list
                Study studies[] = SQLDatabase.getInstance(birdSelect).getStudies();
                fillStudiesList(studies);
            }
        }).start();

    }

    public void fillStudiesList(Study[] studies){



    }

    public static Context getAppContext() {
        return BirdSelect.context;
    }

}

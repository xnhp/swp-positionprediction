package project.software.uni.positionprediction.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.movebank.MovebankConnector;

public class BirdSelect extends AppCompatActivity {

    private static Context context;

    //private Button buttonSelect = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird_select);

        Button buttonSelect = findViewById(R.id.birdselect_button_select);
        Button buttonOpenMap = findViewById(R.id.birdselect_button_openmap);

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
                Intent mapIntent = new Intent(birdSelect, OSM.class);
                startActivity(mapIntent);
            }
        });



        MovebankConnector connector = MovebankConnector.getInstance();
        String result = connector.getBirdData(22390461, 102937685);

    }

    public static Context getAppContext() {
        return BirdSelect.context;
    }

}

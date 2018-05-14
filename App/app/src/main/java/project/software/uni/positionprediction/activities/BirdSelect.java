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

    private Button buttonSelect = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird_select);

        buttonSelect = findViewById(R.id.birdselect_button_select);

        final BirdSelect birdSelect = this;

        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buttonIntent =  new Intent(birdSelect, Settings.class);
                startActivity(buttonIntent);
            }
        });

        BirdSelect.context = getApplicationContext();


        MovebankConnector connector = MovebankConnector.getInstance();
        String result = connector.getBirdData(22390461, 102937685);

    }

    public static Context getAppContext() {
        return BirdSelect.context;
    }

}

package project.software.uni.positionprediction.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import project.software.uni.positionprediction.R;

public class OSM extends AppCompatActivity {

    private Button buttonSettings;
    private Button buttonPredict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osm);

        buttonSettings = findViewById(R.id.navigation_button_settings);
        buttonPredict = findViewById(R.id.osm_button_predict);

        final OSM osm = this;

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buttonIntent =  new Intent(osm, Settings.class);
                startActivity(buttonIntent);
            }
        });

        buttonPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent buttonIntent =  new Intent(osm, Prediction.class);
                startActivity(buttonIntent);
            }
        });
    }
}

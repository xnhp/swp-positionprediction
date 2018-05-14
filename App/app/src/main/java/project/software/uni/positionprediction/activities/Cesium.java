package project.software.uni.positionprediction.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import project.software.uni.positionprediction.R;

public class Cesium extends AppCompatActivity {

    private Button buttonSettings = null;
    private Button buttonPredict = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cesium);

        buttonSettings = findViewById(R.id.navigation_button_settings);
        buttonPredict = findViewById(R.id.cesium_button_predict);

        final Cesium cesium = this;

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buttonIntent =  new Intent(cesium, Settings.class);
                startActivity(buttonIntent);
            }
        });

        buttonPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buttonIntent =  new Intent(cesium, Prediction.class);
                startActivity(buttonIntent);
            }
        });
    }
}

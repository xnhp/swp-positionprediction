package project.software.uni.positionprediction.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import project.software.uni.positionprediction.R;

public class Prediction extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        Button predictButton = findViewById(R.id.prediction_button_predict);

        final Prediction prediction = this;
        
        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prediction.finish();
            }
        });
    }
}

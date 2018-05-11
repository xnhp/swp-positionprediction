package project.software.uni.positionprediction.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import project.software.uni.positionprediction.R;

public class Prediction extends AppCompatActivity {

    private Button predictButton = findViewById(R.id.prediction_button_predict);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        final Prediction prediction = this;

        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prediction.finish();
            }
        });
    }
}

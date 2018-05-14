package project.software.uni.positionprediction.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import project.software.uni.positionprediction.R;

public class Settings extends AppCompatActivity {

    private Button buttonSave = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        buttonSave = findViewById(R.id.settings_button_save);

        final Settings settings = this;

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.finish();
            }
        });

    }
}

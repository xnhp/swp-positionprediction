package project.software.uni.positionprediction.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import project.software.uni.positionprediction.R;

public class Settings extends AppCompatActivity {

    private Button buttonSave = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        Spinner dropdown_alg = findViewById(R.id.spinner_alg);
        String[] items = new String[]{"Algorithm 1", "Algorithm 2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown_alg.setAdapter(adapter);

        Spinner dropdown_vis = findViewById(R.id.spinner_vis);
        String[] items_vis = new String[]{"Vis 1", "Vis 2"};
        ArrayAdapter<String> adapter_vis = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_vis);
        dropdown_vis.setAdapter(adapter);

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

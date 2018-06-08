package project.software.uni.positionprediction.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.util.ErrorMsg;
import project.software.uni.positionprediction.util.XML;

public class Settings extends AppCompatActivity {

    // Components
    private Button buttonSave = null;
    private SeekBar seekbar_past = null;
    private SeekBar seekbar_future = null;
    private EditText text_past = null;
    private EditText text_future = null;

    // Other
    private XML xml = new XML();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        // Define Dropdown for algorithms
        Spinner dropdown_alg = findViewById(R.id.spinner_alg);
        String[] items = new String[]{"Algorithm 1", "Algorithm 2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown_alg.setAdapter(adapter);

        // Define dropdown for Visualization
        Spinner dropdown_vis = findViewById(R.id.spinner_vis);
        String[] items_vis = new String[]{"Vis 1", "Vis 2"};
        ArrayAdapter<String> adapter_vis = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_vis);
        dropdown_vis.setAdapter(adapter);

        // Define Buttons
        buttonSave = findViewById(R.id.settings_button_save);
        seekbar_past = findViewById(R.id.seekbar_past);
        seekbar_future = findViewById(R.id.seekbar_future);
        text_past = findViewById(R.id.text_alg);
        text_future = findViewById(R.id.text_vis);


        final Settings settings = this;

        // Save Button Listener
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.finish();
            }
        });

        // SeekbarListener (Algorithm)
        seekbar_past.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                   @Override
                   public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                       xml.setHours_past((double) progress);
                       seekbar_past.setProgress(progress);
                       text_past.setText(""+progress);
                   }

                   @Override
                   public void onStartTrackingTouch(SeekBar seekBar) {
                       // TODO
                   }

                   @Override
                   public void onStopTrackingTouch(SeekBar seekBar) {
                       // TODO
                   }
        });

        // SeekbarListener (Visualization)
        seekbar_future.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        xml.setHours_fut((double) progress);
                        seekbar_future.setProgress(progress);
                        text_future.setText(""+progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // TODO
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // TODO
                    }
        });


        text_past.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // TODO
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // TODO
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        xml.setHours_past(Integer.getInteger(s.toString()));
                    }
        });

        text_future.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // TODO
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // TODO
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // TODO
                    }
        });

    }
}

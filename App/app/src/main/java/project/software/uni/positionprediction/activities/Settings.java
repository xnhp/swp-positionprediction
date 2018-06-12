package project.software.uni.positionprediction.activities;

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
import android.widget.TextView;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.controllers.ModeController;
import project.software.uni.positionprediction.osm.OSMCacheControl;
import project.software.uni.positionprediction.util.Message;
import project.software.uni.positionprediction.util.XML;

import static android.text.TextUtils.isDigitsOnly;

public class Settings extends AppCompatActivity {

    // Components
    private Button buttonSave = null;
    private Button buttonClearCache = null;
    private SeekBar seekbar_past = null;
    private SeekBar seekbar_future = null;
    private EditText text_past = null;
    private EditText text_future = null;
    private Spinner spinner_alg = null;
    private Spinner spinner_vis = null;

    // Other
    private XML xml = new XML();
    Message m = new Message();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Settings settings = this;

        // retrieve a final reference so we can access it in click handlers
        final OSMCacheControl osmCacheControl = ModeController.getInstance(settings).osmCacheControl;

        setContentView(R.layout.activity_settings);


        spinner_alg = findViewById(R.id.spinner_alg);
        spinner_vis = findViewById(R.id.spinner_vis);
        buttonSave = findViewById(R.id.settings_button_save);
        buttonClearCache = findViewById(R.id.settings_button_clearcache);
        seekbar_past = findViewById(R.id.seekbar_past);
        seekbar_future = findViewById(R.id.seekbar_future);
        text_past = findViewById(R.id.text_past);
        text_future = findViewById(R.id.text_future);


        // Define Dropdown for algorithms
        String[] items_alg = new String[]{"Algorithm 1", "Algorithm 2"};
        ArrayAdapter<String> adapter_alg = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_alg);
        spinner_alg.setAdapter(adapter_alg);

        // Define dropdown for Visualization
        String[] items_vis = new String[]{"Vis 1", "Vis 2"};
        ArrayAdapter<String> adapter_vis = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_vis);
        spinner_vis.setAdapter(adapter_vis);


        // get cache size and display it in the UI
        updateCacheSize();


        // Clear Cache Button Listener
        buttonClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("cache management", "cache clearing triggered");
                osmCacheControl.clearCache();
                updateCacheSize();
            }
        });

        // Save Button Listener
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.finish();
            }
        });


        // SeekbarListener (Past)
        seekbar_past.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                   @Override
                   public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                       if (fromUser) {
                           xml.setHours_past(progress);
                           text_past.setText(""+progress);
                       }
                   }

                   @Override
                   public void onStartTrackingTouch(SeekBar seekBar) {
                   }

                   @Override
                   public void onStopTrackingTouch(SeekBar seekBar) {
                   }
        });


        // TextListener (Past)
        text_past.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int input_as_int;
                String input_as_str = s.toString();

                boolean isNumber = isDigitsOnly(input_as_str);

                if (isNumber) {
                    if (input_as_str.length() == 0) {
                        input_as_int = 0;
                    } else {
                        input_as_int = Integer.parseInt(s.toString());
                    }

                    xml.setHours_past(input_as_int);

                    int max = seekbar_past.getMax();
                    input_as_int = input_as_int > max ? max : input_as_int;

                    seekbar_past.setProgress(input_as_int);



                } else {
                    m.disp_error(settings, "Wrong format", "You can only use numbers!", false);
                }

            }
        });

        // SeekbarListener (Future)
        seekbar_future.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            xml.setHours_fut(progress);
                            text_future.setText(""+progress);
                        }
                    }


                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
        });


        // TextListener (Future)
        text_future.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int input_as_int;
                        String input_as_str = s.toString();

                        boolean isNumber = isDigitsOnly(input_as_str);

                        if (isNumber) {
                            if (input_as_str.length() == 0) {
                                input_as_int = 0;
                            } else {
                                input_as_int = Integer.parseInt(s.toString());
                            }

                            xml.setHours_past(input_as_int);

                            int max = seekbar_future.getMax();
                            input_as_int = input_as_int > max ? max : input_as_int;

                            seekbar_future.setProgress(input_as_int);



                        } else {
                            m.disp_error(settings, "Wrong format", "You can only use numbers!", false);
                        }
                    }

        });
    }

    public void onResume() {
        updateCacheSize();
        super.onResume();
    }

    private void updateCacheSize() {
        TextView cacheSizeTextview = findViewById(R.id.osmCacheSize);
        String cacheSize = android.text.format.Formatter.formatShortFileSize(this,
                ModeController.getInstance(this).osmCacheControl.getCacheSize());
        cacheSizeTextview.setText("Current cache size is: " + cacheSize);
        Log.i("cache", "cache info updated to " + cacheSize);
    }
}

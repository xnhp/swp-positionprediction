package project.software.uni.positionprediction.activities;

import android.content.DialogInterface;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.osm.OSMCacheControl;
import project.software.uni.positionprediction.movebank.SQLDatabase;
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
    private Button delete_btn = null;
    private CheckBox checkBox = null;

    // Other
    private XML xml = new XML();
    Message msg = new Message();
    SQLDatabase db = SQLDatabase.getInstance(this);

    // this is a field because we need to be able to access it in click handlers.
    OSMCacheControl osmCacheControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        osmCacheControl = OSMCacheControl.getInstance(this);

        final Settings settings = this;

        setContentView(R.layout.activity_settings);
        final Context c = this;


        xml.readFile(c);

        spinner_alg = findViewById(R.id.spinner_alg);
        spinner_vis = findViewById(R.id.spinner_vis);
        buttonSave = findViewById(R.id.settings_button_save);
        buttonClearCache = findViewById(R.id.settings_button_clearcache);
        seekbar_past = findViewById(R.id.seekbar_past);
        seekbar_future = findViewById(R.id.seekbar_future);
        text_past = findViewById(R.id.text_past);
        text_future = findViewById(R.id.text_future);
        delete_btn = findViewById(R.id.delete_btn);
        checkBox = findViewById(R.id.checkbox);

        // Set values correct
        if (xml.getHours_past() == -1) {
            seekbar_past.setProgress(seekbar_past.getMax());
            text_past.setText("All");
            checkBox.setChecked(true);
        } else {
            seekbar_past.setProgress(xml.getHours_past());
            text_past.setText(""+xml.getHours_past());
        }

        seekbar_future.setProgress(xml.getHours_fut());
        text_future.setText(""+xml.getHours_fut());



        // Define Dropdown for algorithms
        Class[] items_alg = xml.getAlgorithms();
        ArrayAdapter<String> adapter_alg = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, asStringArray(items_alg));
        spinner_alg.setAdapter(adapter_alg);
        spinner_alg.setSelection(xml.getUsed_alg());

        // Define dropdown for Visualization
        Class[] items_vis = xml.getVisualizations();
        ArrayAdapter<String> adapter_vis = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, asStringArray(items_vis));
        spinner_vis.setAdapter(adapter_vis);
        spinner_vis.setSelection(xml.getUsed_vis());


        // check cache size and display a note to the user if its large
        // todo: check on startup of app?
        checkCacheSize();
        // get cache size and display it in the UI
        updateCacheSize();

        // Clear Cache Button Listener
        buttonClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearCacheWithUI();
            }
        });

        // Save Button Listener
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xml.writeFile(c);
                OSM_new.setSettingsChanged();
                settings.finish();
            }
        });

        // SeekbarListener (Past)
        seekbar_past.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                   @Override
                   public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                       if (fromUser) {
                           xml.setHours_past(progress);
                           text_past.setText("" + progress);
                           checkBox.setChecked(false);
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
                    Log.e("isNumber ", ""+input_as_str);
                    if (input_as_str.length() == 0) {
                        input_as_int = 0;
                    } else {

                        // Catch if its larger than integer
                        try {
                            input_as_int = Integer.parseInt(s.toString());
                        } catch (NumberFormatException e) {
                            msg.disp_error(c, "Input error",
                                    "You reached the limit of an Integer. " +
                                            "Please click 'Use all data' to use more date");
                            text_past.setText("0");
                            input_as_int = 0;
                        }
                    }

                    xml.setHours_past(input_as_int);

                    int max = seekbar_past.getMax();
                    input_as_int = input_as_int > max ? max : input_as_int;

                    seekbar_past.setProgress(input_as_int);



                } else if (!checkBox.isChecked()){
                    msg.disp_error(settings, "Wrong format", "You can only use numbers!");
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

                            xml.setHours_fut(input_as_int);

                            int max = seekbar_future.getMax();
                            input_as_int = input_as_int > max ? max : input_as_int;

                            seekbar_future.setProgress(input_as_int);



                        } else {
                            msg.disp_error(settings, "Wrong format", "You can only use numbers!");
                        }
                    }

        });

        spinner_alg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Algorithm changed", "ID " + position);
                xml.setUsed_alg(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        spinner_vis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Visualization changed", "ID " + position);
                xml.setUsed_vis(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });




        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.dropAllData();
                msg.disp_success(c, "Deleted", "All data where deleted successfully");
            }
        });


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    xml.setHours_past(-1);
                    seekbar_past.setProgress(seekbar_past.getMax());
                    text_past.setText("All");
                } else {
                    //xml.setHours_past(seekbar_past.getMax());
                    text_past.setText("0");

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
        String cacheSize = osmCacheControl.getCacheSizeReadable(this);
        cacheSizeTextview.setText(getString(R.string.settings_cache_size_label) + ":  " + cacheSize);
        Log.i("cache", "cache info updated to " + cacheSize);
    }

    // check if cache is "large" (see OSMCacheControl)
    // if so, display a notice to the user
    private void checkCacheSize() {
        if (osmCacheControl.isCacheTooLarge()) {
            clearCacheWithUI();
        }
    }

    /**
     * 1. shows dialog box for confirmation
     * 2. clears the cache
     * 3. updates the displayed number in the UI
     */
    private void clearCacheWithUI() {
        String title = getString(R.string.settings_cache_size_prompt_title);
        String format = getString(R.string.settings_cache_size_prompt_text);
        String text = String.format(format, osmCacheControl.getCacheSizeReadable(this));
        String positiveLabel = getString(R.string.settings_cache_size_prompt_confirm);
        Message m = new Message();
        m.disp_confirm(this, title, text, positiveLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                osmCacheControl.clearCache();
                updateCacheSize();
            }
        });
    }







    // Other methods
    public String[] asStringArray(Class[] array) {
        int n = array.length;
        String r_array[] = new String[n];
        for (int i = 0; i<n; i++) {
            r_array[i] = array[i].getSimpleName();
        }
        return r_array;
    }





}

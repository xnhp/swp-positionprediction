package project.software.uni.positionprediction.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.movebank.CSVParser;
import project.software.uni.positionprediction.movebank.MovebankConnector;
import project.software.uni.positionprediction.movebank.RequestHandler;
import project.software.uni.positionprediction.movebank.SQLDatabase;

public class BirdSelect extends AppCompatActivity {

    private static Context context;

    private Button buttonSelect = null;
    private Button buttonOpenMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird_select);

        buttonSelect = findViewById(R.id.birdselect_button_select);

        buttonOpenMap = findViewById(R.id.birdselect_button_openmap);

        final BirdSelect birdSelect = this;

        // QUESTION: Why is this here and not in other activities?
        BirdSelect.context = getApplicationContext();

        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buttonIntent =  new Intent(birdSelect, Settings.class);
                startActivity(buttonIntent);
                //Log.e("Movebank: ", MovebankConnector.getInstance(getAppContext()).getBirdData(22390461, 102937685));
            }
        });

        buttonOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapIntent = new Intent(birdSelect, OSM.class);
                startActivity(mapIntent);
            }
        });

        MovebankConnector.getInstance(this).getStudies(new RequestHandler() {
            @Override
            public void handleResponse(String response) {
                if(response == null){

                } else{
                    String data[][] = CSVParser.parseColumnsRows(response);
                    String table[][] = CSVParser.getColumns(data, new String[]{"name", "id", "i_am_owner", "timestamp_end", "timestamp_start"});

                    for(int i = 1; i < table.length; i++){
                        SQLDatabase.getInstance(birdSelect).queryWrite(
                                "INSERT OR IGNORE INTO studies (name, id, i_am_owner, timestamp_end, timestamp_start) VALUES ('"
                                        + table[0][i] + "', "
                                        + table[1][i] + ", "
                                        + (table[2][i].equals("false") ? "0" : "1") + ", "
                                        + (table[3][i].equals("") ? "NULL" : table[3][i]) + ", "
                                        + (table[4][i].equals("") ? "NULL" : table[4][i]) + ")");
                    }

                }


            }
        });

    }

    public static Context getAppContext() {
        return BirdSelect.context;
    }

}

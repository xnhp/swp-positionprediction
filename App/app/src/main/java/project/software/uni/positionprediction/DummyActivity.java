package project.software.uni.positionprediction;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import project.software.uni.positionprediction.movebank.MovebankConnector;

public class DummyActivity extends AppCompatActivity {

    private static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);
        DummyActivity.context = getApplicationContext();


        MovebankConnector connector = MovebankConnector.getInstance();
        String result = connector.getBirdData(22390461, 102937685);


    }

    public static Context getAppContext() {
        return DummyActivity.context;
    }


}

package project.software.uni.positionprediction.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import project.software.uni.positionprediction.BuildConfig;
import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.algorithms_new.AlgorithmExtrapolationExtended;
import project.software.uni.positionprediction.algorithms_new.AlgorithmSimilarTrajectory;
import project.software.uni.positionprediction.datatypes_new.HttpStatusCode;
import project.software.uni.positionprediction.datatypes_new.Request;
import project.software.uni.positionprediction.datatypes_new.Trajectory;
import project.software.uni.positionprediction.movebank.MovebankConnector;
import project.software.uni.positionprediction.util.XML;
import project.software.uni.positionprediction.visualisation_new.StyledLineSegment;
import project.software.uni.positionprediction.visualisation_new.StyledPoint;

public class Login extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    RelativeLayout layout;

    private XML xml = new XML();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        layout = findViewById(R.id.login_background);
        layout.getBackground().setAlpha(getResources().getInteger(R.integer.background_alpha));

        // Define default algorithms and visualization
        Class algorithms[] = new Class[]{
                AlgorithmExtrapolationExtended.class,
                AlgorithmSimilarTrajectory.class
        };
        xml.setAlgorithms(algorithms);

        Class visualizations[] = new Class[]{
                Trajectory.class,
                StyledPoint.class,
                StyledLineSegment.class
        };
        xml.setVisualizations(visualizations);


        editTextUsername = (EditText)findViewById(R.id.login_edittext_username);
        editTextPassword = (EditText)findViewById(R.id.login_edittext_password);

        buttonLogin = (Button)findViewById(R.id.login_button_login);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(  editTextUsername.getText().toString(),
                        editTextPassword.getText().toString() );
            }
        });

        xml.readFile(this);

        // try login with saved user data
        if(xml.getMovebank_user() != null && xml.getMovebank_password() != null){
            login(xml.getMovebank_user(), xml.getMovebank_password());
        }

    }

    private void login(final String username, final String password){

        final Login login = this;

        if(BuildConfig.DEBUG){
            if(username.equals("") && password.equals("")){

                MovebankConnector.getInstance(this).setLogin(
                        getResources().getString(R.string.movebank_user),
                        getResources().getString(R.string.movebank_password));

                Intent buttonIntent =  new Intent(login, BirdSelect.class);
                startActivity(buttonIntent);
                return;
            }
        }

        if(password.equals("") || username.equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(login);
            builder.setMessage(R.string.login_field_missing)
                    .setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            builder.create().show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = MovebankConnector.getInstance(login).checkUser(username, password);

                int status = request.getResponseStatus();

                Log.e("status", status + "");

                if(status == HttpStatusCode.OK){
                    MovebankConnector.getInstance(login).checkUser(username, password);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Intent buttonIntent =  new Intent(login, BirdSelect.class);
                            startActivity(buttonIntent);
                            xml.setMovebank_user(username);
                            xml.setMovebank_password(password);
                            xml.writeFile(login);
                            finish();
                        }
                    });

                    Log.e("creds", "valid");
                } else if(status == HttpStatusCode.FORBIDDEN) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            layout.setBackgroundResource(R.drawable.stork_animation);
                            AnimationDrawable animation = (AnimationDrawable) layout.getBackground();
                            animation.setAlpha(getResources().getInteger(R.integer.background_alpha));

                            animation.stop();
                            animation.start();

                            AlertDialog.Builder builder = new AlertDialog.Builder(login);
                            builder.setMessage(R.string.login_wrong_creds_warning)
                                    .setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                            builder.create().show();
                        }
                    });
                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(login);
                            builder.setMessage(R.string.login_failed_text)
                                    .setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                            builder.create().show();
                        }
                    });
                }
            }
        }).start();

    }
}

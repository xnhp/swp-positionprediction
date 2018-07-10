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
import project.software.uni.positionprediction.algorithms.AlgorithmExtrapolationExtended;
import project.software.uni.positionprediction.algorithms.AlgorithmSimilarTrajectory;
import project.software.uni.positionprediction.datatypes.HttpStatusCode;
import project.software.uni.positionprediction.datatypes.Request;
import project.software.uni.positionprediction.datatypes.Trajectory;
import project.software.uni.positionprediction.movebank.MovebankConnector;
import project.software.uni.positionprediction.util.LoadingIndicator;
import project.software.uni.positionprediction.util.XML;
import project.software.uni.positionprediction.visualisation.StyledLineSegment;
import project.software.uni.positionprediction.visualisation.StyledPoint;

public class Login extends AppCompatActivity {

    private EditText editTextUsername = null;
    private EditText editTextPassword = null;
    private Button buttonLogin = null;
    RelativeLayout layout = null;

    LoadingIndicator loadingIndicator = null;

    private XML xml = new XML();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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


        this.layout = findViewById(R.id.login_background);
        this.layout.getBackground().setAlpha(getResources().getInteger(R.integer.background_alpha));
        this.editTextUsername = (EditText)findViewById(R.id.login_edittext_username);
        this.editTextPassword = (EditText)findViewById(R.id.login_edittext_password);

        this.buttonLogin = (Button)findViewById(R.id.login_button_login);

        this.loadingIndicator = LoadingIndicator.getInstance();

        final Login login = this;

        this.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingIndicator.show(login);
                login(  editTextUsername.getText().toString(),
                        editTextPassword.getText().toString() );
            }
        });

        xml.readFile(this);

        // try login with saved user data
        if(xml.getMovebank_user() != null && xml.getMovebank_password() != null){
            this.editTextUsername.setText(xml.getMovebank_user());
            this.editTextPassword.setText(xml.getMovebank_password());
            loadingIndicator.show(this);
            login(xml.getMovebank_user(), xml.getMovebank_password());
        }

    }

    /**
     * This Method trys to login with the given credentials
     * @param username the username to login with
     * @param password the password to login with
     */
    private void login(final String username, final String password){

        final Login login = this;

        // Use standarf-Password in Debug-Mode if nothing is typed in
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

        // check weather the user typed in anything
        if(password.equals("") || username.equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(login);
            loadingIndicator.hide();
            builder.setMessage(R.string.login_field_missing)
                    .setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            builder.create().show();
            return;
        }

        // stard new Thread for the synchronous network-Request
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = MovebankConnector.getInstance(login).checkUser(username, password);

                int status = request.getResponseStatus();

                loadingIndicator.hide();

                if(status == HttpStatusCode.OK){

                    // everything ok, credentials valid
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

                    Log.i("creds", "valid");
                } else if(status == HttpStatusCode.FORBIDDEN) {

                    // invalid credentials
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

                    // something went wrong (unknown error).
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

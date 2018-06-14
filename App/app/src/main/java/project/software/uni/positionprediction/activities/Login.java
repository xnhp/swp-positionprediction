package project.software.uni.positionprediction.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import project.software.uni.positionprediction.BuildConfig;
import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.datatype.HttpStatusCode;
import project.software.uni.positionprediction.datatype.Request;
import project.software.uni.positionprediction.movebank.MovebankConnector;

public class Login extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = (EditText)findViewById(R.id.login_edittext_username);
        editTextPassword = (EditText)findViewById(R.id.login_edittext_password);

        buttonLogin = (Button)findViewById(R.id.login_button_login);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });


    }

    void login(){

        final String username = editTextUsername.getText().toString();
        final String password = editTextPassword.getText().toString();

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
                        }
                    });

                    Log.e("creds", "valid");
                } else if(status == HttpStatusCode.FORBIDDEN) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
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

package project.software.uni.positionprediction.movebank;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Utility for interactions with the Movebank API.
 *
 * Example usage:
 *     MovebankConnector connector = MovebankConnector.getInstance();
 *     String result = connector.getBirdData(22390461, 102937685);
 *
 * TODO: Warnings?
 */
public class MovebankConnector {

    private static MovebankConnector instance;
    private MovebankRequest movebankRequest;

    private MovebankConnector(Context context){
        movebankRequest = new MovebankRequest(context);
    }

    public static MovebankConnector getInstance(Context context) {

        if(instance == null){
            instance = new MovebankConnector(context);
        }

        return instance;
    }



    public void getBirdData(int studyID, int indivID, RequestHandler requestHandler){

        String typeAttr = "entity_type=event";
        String studyAttr = format("study_id=%d", studyID);
        String individualAttr = format("individual_id=%d", indivID);

        String attributes = format("%s&%s&%s", typeAttr, studyAttr, individualAttr);

        movebankRequest.requestDataAsync(attributes, requestHandler, requestHandler);

    }

    public void getBirdData(int studyID, int indivID, Date start, Date end, RequestHandler requestHandler){

        String typeAttr = "entity_type=event";
        String studyAttr = format("study_id=%d", studyID);
        String indivAttr = format("individual_id=%d", indivID);
        String startAttr = format("timestamp_start=%d%n", start.getTime());
        String endAttr = format("timestamp_end=%d%n", end.getTime());


        String attributes = format("%s&%s&%s&%s&%s", typeAttr, studyAttr, indivAttr, startAttr, endAttr);

        movebankRequest.requestDataAsync(attributes, requestHandler, requestHandler);
    }


    public void getStudies(RequestHandler requestHandler){

        String attributes = "entity_type=study";

        movebankRequest.requestDataAsync(attributes, requestHandler, requestHandler);
    }


    public void getBirds(int studyId, RequestHandler requestHandler){
        String typeAttr = "entity_type=individual";
        String studyAttr = format("study_id=%d", studyId);

        String attributes = format("%s&%s", typeAttr, studyAttr);

        movebankRequest.requestDataAsync(attributes, requestHandler, requestHandler);
    }


    /*public boolean changeUser(String username, String password){
        //TODO attributes,
        String usernameOld = movebankRequest.getUsername();
        String passwordOld = movebankRequest.getPassword();

        movebankRequest.setUserCreds(username, password);

        boolean isValid = movebankRequest.isUserCredsValid();

        if(!isValid){
            movebankRequest.setUserCreds(usernameOld, passwordOld);
        }

        return isValid;
    }*/

    /**
     * Formats a string with explicit US locale.
     * Not specifying the locale throws a warning.
     * @param fmt Format string
     * @param params Parameters for string formatting
     * @return
     */
    private String format(String fmt, Object... params) {
        return String.format(Locale.US, fmt, params);
    }


}

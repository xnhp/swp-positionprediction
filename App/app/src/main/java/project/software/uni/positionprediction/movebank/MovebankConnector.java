package project.software.uni.positionprediction.movebank;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.Date;
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

    /**
     * Stock response listener.
     * TODO: rethink this.
     */
    private Response.Listener<String> printResponse = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            System.out.println(response);
        }
    };
    
    private Response.ErrorListener printError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println(error);
        }
    };

    private MovebankConnector(){
        movebankRequest = new MovebankRequest();
    }


    public static MovebankConnector getInstance() {

        if(instance == null){
            instance = new MovebankConnector();
        }

        return instance;
    }



    public String getBirdData(int studyID, int indivID){

        String typeAttr = "entity_type=event";
        String studyAttr = format("study_id=%d", studyID);
        String individualAttr = format("individual_id=%d", indivID);

        String attributes = format("%s&%s&%s", typeAttr, studyAttr, individualAttr);

        return movebankRequest.requestData(attributes, printResponse, printError);
    }

    public String getBirdData(int studyID, int indivID, Date start, Date end){

        String typeAttr = "entity_type=event";
        String studyAttr = format("study_id=%d", studyID);
        String indivAttr = format("individual_id=%d", indivID);
        String startAttr = format("timestamp_start=%d%n", start.getTime());
        String endAttr = format("timestamp_end=%d%n", end.getTime());


        String attributes = format("%s&%s&%s&%s&%s", typeAttr, studyAttr, indivAttr, startAttr, endAttr);

        return movebankRequest.requestData(attributes, printResponse, printError);
    }


    public String getStudies(){

        String attributes = "entity_type=study";

        return movebankRequest.requestData(attributes, printResponse, printError);
    }


    public String getBirds(int studyId){
        String typeAttr = "entity_type=individual";
        String studyAttr = format("study_id=%d", 2911040);

        String attributes = format("%s&%s", typeAttr, studyAttr);

        return movebankRequest.requestData(attributes, printResponse, printError);
    }

    /*
    public boolean changeUser(String username, String password){
        //TODO attributes
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

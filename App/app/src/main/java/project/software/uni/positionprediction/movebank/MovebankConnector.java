package project.software.uni.positionprediction.movebank;

import android.content.Context;

import java.util.Date;
import java.util.Locale;

import project.software.uni.positionprediction.datatypes_new.HttpStatusCode;
import project.software.uni.positionprediction.datatypes_new.Request;

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


    /**
     * This Method requests the data of the bird with the given study and individual id
     * @param studyID the study id of the bird
     * @param indivID the individual id of the bird
     * @param requestHandler the callback handler for the request
     */
    public void getBirdData(int studyID, int indivID, RequestHandler requestHandler){

        String typeAttr = "entity_type=event";
        String studyAttr = format("study_id=%d", studyID);
        String individualAttr = format("individual_id=%d", indivID);

        String attributes = format("%s&%s&%s", typeAttr, studyAttr, individualAttr);

        movebankRequest.requestDataAsync(attributes, requestHandler);

    }

    /**
     * This Method requests the data of the bird with the given study and individual id
     * @param studyID the study id of the bird
     * @param indivID the individual id of the bird
     * @return the returned CSV File as String
     */
    public Request getBirdDataSync(int studyID, int indivID){

        String typeAttr = "entity_type=event";
        String studyAttr = format("study_id=%d", studyID);
        String individualAttr = format("individual_id=%d", indivID);

        String attributes = format("%s&%s&%s", typeAttr, studyAttr, individualAttr);

        return movebankRequest.requestDataSync(attributes);

    }

    /**
     * This Method requests the data of the bird with the given study and individual id in the given time period
     * @param studyID the study id of the bird
     * @param indivID the individual id of the bird
     * @param start the start date
     * @param end the end date
     * @param requestHandler the callback handler for the request
     */
    public void getBirdData(int studyID, int indivID, Date start, Date end, RequestHandler requestHandler){

        String typeAttr = "entity_type=event";
        String studyAttr = format("study_id=%d", studyID);
        String indivAttr = format("individual_id=%d", indivID);
        String startAttr = format("timestamp_start=%d%n", start.getTime());
        String endAttr = format("timestamp_end=%d%n", end.getTime());


        String attributes = format("%s&%s&%s&%s&%s", typeAttr, studyAttr, indivAttr, startAttr, endAttr);

        movebankRequest.requestDataAsync(attributes, requestHandler);
    }

    /**
     * This Method requests the data of the bird with the given study and individual id in the given time period
     * @param studyID the study id of the bird
     * @param indivID the individual id of the bird
     * @param start the start date
     * @param end the end date
     * @return the returned CSV File as String
     */
    public Request getBirdDataSync(int studyID, int indivID, Date start, Date end){

        String typeAttr = "entity_type=event";
        String studyAttr = format("study_id=%d", studyID);
        String indivAttr = format("individual_id=%d", indivID);
        String startAttr = format("timestamp_start=%d%n", start.getTime());
        String endAttr = format("timestamp_end=%d%n", end.getTime());


        String attributes = format("%s&%s&%s&%s&%s", typeAttr, studyAttr, indivAttr, startAttr, endAttr);

        return movebankRequest.requestDataSync(attributes);
    }



    /**
     * This Method requests a list of all the the current user available studies
     * @param requestHandler The callback handler for the request
     */
    public void getStudies(RequestHandler requestHandler){
        String attributes = "entity_type=study";
        movebankRequest.requestDataAsync(attributes, requestHandler);
    }

    /**
     * This Method requests a list of all the the current user available studies.
     * Don't call this Method from the main Thread!
     *
     * @return the returned CSV File as String
     */
    public Request getStudiesSync(){
        String attributes = "entity_type=study";
        return movebankRequest.requestDataSync(attributes);
    }


    /**
     * This Method requests the birds for a given study
     * @param studyId the study id to get the birds for
     * @param requestHandler The callback handler for the request
     */
    public void getBirds(int studyId, RequestHandler requestHandler){
        String typeAttr = "entity_type=individual";
        String studyAttr = format("study_id=%d", studyId);

        String attributes = format("%s&%s", typeAttr, studyAttr);

        movebankRequest.requestDataAsync(attributes, requestHandler);
    }

    /**
     * This Method requests the birds for a given study
     * @param studyId the study id to get the birds for
     * @return the returned CSV File as String
     */
    public Request getBirdsSync(int studyId){
        String typeAttr = "entity_type=individual";
        String studyAttr = format("study_id=%d", studyId);

        String attributes = format("%s&%s", typeAttr, studyAttr);

        return movebankRequest.requestDataSync(attributes);
    }


    public Request checkUser(String username, String password){

        String usernameOld = movebankRequest.getUsername();
        String passwordOld = movebankRequest.getPassword();

        movebankRequest.setUserCreds(username, password);

        boolean isValid = false;

        Request request = getStudiesSync();
        if(request.getResponseStatus() == HttpStatusCode.OK){
            isValid = true;
        }

        movebankRequest.setUserCreds(usernameOld, passwordOld);

        return request;
    }

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

    public void setLogin(String username, String password){
        movebankRequest.setUserCreds(username, password);
    }


}

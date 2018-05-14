package project.software.uni.positionprediction.movebank;

import java.util.Date;

public class MovebankConnector {

    private static MovebankConnector instance;
    private MovebankRequest movebankRequest;


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
        String studyAttr = String.format("study_id=%d", studyID);
        String individualAttr = String.format("individual_id=%d", indivID);

        String attributes = String.format("%s&%s&%s", typeAttr, studyAttr, individualAttr);

        return movebankRequest.requestData(attributes);
    }

    public String getBirdData(int studyID, int indivID, Date start, Date end){

        String typeAttr = "entity_type=event";
        String studyAttr = String.format("study_id=%d", studyID);
        String indivAttr = String.format("individual_id=%d", indivID);
        String startAttr = String.format("timestamp_start=%d%n", start.getTime());
        String endAttr = String.format("timestamp_end=%d%n", end.getTime());


        String attributes = String.format("%s&%s&%s&%s&%s", typeAttr, studyAttr, indivAttr, startAttr, endAttr);

        return movebankRequest.requestData(attributes);
    }


    public String getStudies(){

        String attributes = "entity_type=study";

        return movebankRequest.requestData(attributes);
    }


    public String getBirds(int studyId){
        String typeAttr = "entity_type=individual";
        String studyAttr = String.format("study_id=%d", 2911040);

        String attributes = String.format("%s&%s", typeAttr, studyAttr);

        return movebankRequest.requestData(attributes);
    }

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
    }


}

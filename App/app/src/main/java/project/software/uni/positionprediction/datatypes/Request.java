package project.software.uni.positionprediction.datatypes;

/**
 * Created by simon on 02.06.18.
 */

public class Request {

    private int id;
    private int responseStatus;
    private String response;

    public int getId(){
        return id;
    }

    public int getResponseStatus(){
        return responseStatus;
    }

    public String getResponse(){
        return response;
    }

    public void setResponse(String response){
        this.response = response;
    }

    public void setResponseStatus(int responseStatus){
        this.responseStatus = responseStatus;
    }

    public Request(int id){
        this.id = id;
        this.responseStatus = 0;
        this.response = null;
    }

}

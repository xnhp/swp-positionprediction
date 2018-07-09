package project.software.uni.positionprediction.datatypes;

/**
 * Created by simon on 29.05.18.
 */

public class BirdData {

    private int individualId;
    private int studyId;

    private Locations trackingPoints;


    public BirdData(int studyId, int individualId, Locations trackingPoints){
        this.studyId = studyId;
        this.individualId = individualId;
        this.trackingPoints = trackingPoints;
    }

    public BirdData(int studyId, int individualId){
        this.studyId = studyId;
        this.individualId = individualId;
        this.trackingPoints = null;
    }

    public Locations getTrackingPoints(){
        return trackingPoints;
    }

    public int getIndividualId(){
        return individualId;
    }

    public int getStudyId(){
        return studyId;
    }

}

package project.software.uni.positionprediction.datatype;

/**
 * Created by simon on 29.05.18.
 */

public class BirdData {

    private int individualId;
    private int studyId;

    private TrackingPoint[] trackingPoints;


    public BirdData(int studyId, int individualId, TrackingPoint[] trackingPoints){
        this.studyId = studyId;
        this.individualId = individualId;
        this.trackingPoints = trackingPoints;
    }

    public BirdData(int studyId, int individualId){
        this.studyId = studyId;
        this.individualId = individualId;
        this.trackingPoints = null;
    }

    public TrackingPoint[] getTrackingPoints(){
        return trackingPoints;
    }

    public int getIndividualId(){
        return individualId;
    }

    public int getStudyId(){
        return studyId;
    }

}

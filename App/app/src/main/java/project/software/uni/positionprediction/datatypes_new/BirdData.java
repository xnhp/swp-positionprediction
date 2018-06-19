package project.software.uni.positionprediction.datatypes_new;

import project.software.uni.positionprediction.datatypes_new.TrackedLocation;

/**
 * Created by simon on 29.05.18.
 */

public class BirdData {

    private int individualId;
    private int studyId;

    private TrackedLocation[] trackingPoints;


    public BirdData(int studyId, int individualId, TrackedLocation[] trackingPoints){
        this.studyId = studyId;
        this.individualId = individualId;
        this.trackingPoints = trackingPoints;
    }

    public BirdData(int studyId, int individualId){
        this.studyId = studyId;
        this.individualId = individualId;
        this.trackingPoints = null;
    }

    public TrackedLocation[] getTrackingPoints(){
        return trackingPoints;
    }

    public int getIndividualId(){
        return individualId;
    }

    public int getStudyId(){
        return studyId;
    }

}

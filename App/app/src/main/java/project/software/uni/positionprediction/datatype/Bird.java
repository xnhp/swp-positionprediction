package project.software.uni.positionprediction.datatype;

/**
 * Created by simon on 02.06.18.
 */

public class Bird {

    private int id;
    private String nickName;
    private int studyId;

    public Bird(int id, int studyId, String nickName){
        this.id = id;
        this.studyId = studyId;
        this.nickName = nickName;
    }

    public int getId(){
        return id;
    }

    public int getStudyId(){
        return studyId;
    }

    public String getNickName(){
        return nickName;
    }

}

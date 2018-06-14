package project.software.uni.positionprediction.datatype;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Only contains superficial information for working with Birds in the program.
 * Actual tracking data is stored in the database
 */

// TODO "Parcelable" might be better?
// cf http://sohailaziz05.blogspot.com/2012/04/passing-custom-objects-between-android.html
public class Bird implements Serializable {

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

    public String toString() {
        return "(Bird: " + this.nickName + " | indivID " + this.id + " | studyID " + this.studyId + " )";
    }

}

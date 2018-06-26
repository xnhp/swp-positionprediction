package project.software.uni.positionprediction.datatypes_new;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

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
    private boolean favorite;
    private Date lastUpdated;

    public Bird(int id, int studyId, String nickName){
        this.id = id;
        this.studyId = studyId;
        this.nickName = nickName;
        this.favorite = false;
    }

    public Bird(int id, int studyId, String nickName, boolean favorite, Date lastUpdated){
        this.id = id;
        this.studyId = studyId;
        this.nickName = nickName;
        this.favorite = favorite;
        this.lastUpdated = lastUpdated;
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

    public boolean isFavorite(){
        return favorite;
    }

    public Date getDateLastUpdated(){
        return lastUpdated;
    }

}

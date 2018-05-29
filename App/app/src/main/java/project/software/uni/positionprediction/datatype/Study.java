package project.software.uni.positionprediction.datatype;

import android.support.annotation.NonNull;

/**
 * Created by simon on 28.05.18.
 */

public class Study implements Comparable<Study>{

    public String name;

    public int id;

    @Override
    public int compareTo(@NonNull Study o) {
        if(id < o.id) return -1;
        else if(id > o.id) return 1;
        return 0;
    }

}

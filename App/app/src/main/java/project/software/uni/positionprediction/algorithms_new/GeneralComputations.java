package project.software.uni.positionprediction.algorithms_new;

import android.content.Context;
import android.util.Log;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import project.software.uni.positionprediction.datatypes_new.Location;
import project.software.uni.positionprediction.datatypes_new.Trajectory;

public class GeneralComputations {

    public GeneralComputations(){
    }


    public double getAngleVariance(Trajectory data) {

        DescriptiveStatistics angles = new DescriptiveStatistics();

        for (int i = 1; i<data.size(); i++) {
            Location loc_a = data.getLocation(i-1);
            Location loc_b = data.getLocation(i);
            double alpha = loc_a.getAngle(loc_b);
            angles.addValue( data.getLocation(i).getAlt() );
        }

        double var = angles.getVariance();
        Log.e("Variance of Angle", "" + var);
        return var;
    }



}

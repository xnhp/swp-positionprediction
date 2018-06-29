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
            Log.d("getAngleVariance", ""+alpha);
            angles.addValue( data.getLocation(i).getLat() );
        }

        double var = angles.getVariance();

        if (var == 0) {
            Log.e("Error", "Computation of Variance probably went wrong!");
        }


        Log.d("uncertainty", ""+var);
        return var;
    }



}

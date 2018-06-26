package project.software.uni.positionprediction.algorithms_new;

import android.content.Context;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import project.software.uni.positionprediction.datatypes_new.Trajectory;

public class GeneralComputations {

    public GeneralComputations(){
    }


    public double getAngleVariance(Trajectory data) {

        DescriptiveStatistics d_long   = new DescriptiveStatistics();
        DescriptiveStatistics d_lat    = new DescriptiveStatistics();
        DescriptiveStatistics d_height = new DescriptiveStatistics();

        for (int i = 0; i<data.size(); i++) {
            d_long.addValue( data.getLocation(i).getLon() );
            d_lat.addValue( data.getLocation(i).getLat() );
            d_height.addValue( data.getLocation(i).getAlt() );
        }

        double mean_long   = d_long.getMean();
        double mean_lat    = d_lat.getMean();
        double mean_height = d_height.getMean();

        double sum_long   = 0;
        double sum_lat    = 0;
        double sum_height = 0;

        for (int i = 0; i<data.size(); i++) {
            sum_long   += Math.pow( data.getLocation(i).getLon() - mean_long ,  2);
            sum_lat    += Math.pow( data.getLocation(i).getLat() - mean_lat,    2);
            sum_height += Math.pow( data.getLocation(i).getAlt() - mean_height, 2);
        }

        double var_long   = sum_long   / data.size();
        double var_lat    = sum_lat    / data.size();
        double var_height = sum_height / data.size();

        // TODO angle

        return 0;
    }



}

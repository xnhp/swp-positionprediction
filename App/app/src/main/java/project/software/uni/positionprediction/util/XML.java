package project.software.uni.positionprediction.util;

import java.util.LinkedList;

import project.software.uni.positionprediction.algorithm.AlgorithmSimilarTrajectory;

public class XML {

    private static int hours_past;
    private static int hours_fut;
    private static Class[] algorithms;
    private static Class[] visualizations;
    private static int used_alg;
    private static int used_vis;

    public XML() {
    }

    public XML(int hours_past, int hours_fut, Class[] algorithms, Class[] visualizations, int used_alg, int used_vis, LinkedList<String> downloaded_studies) {
        this.hours_past = hours_past;
        this.hours_fut = hours_fut;
        this.algorithms = algorithms;
        this.visualizations = visualizations;
        this.used_alg = used_alg;
        this.used_vis = used_vis;
    }


    public int getHours_past() {
        return hours_past;
    }

    public void setHours_past(int hours_past) {
        this.hours_past = hours_past;
    }

    public int getHours_fut() {
        return hours_fut;
    }

    public void setHours_fut(int hours_fut) {
        this.hours_fut = hours_fut;
    }

    public Class[] getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(Class[] algorithms) {
        this.algorithms = algorithms;
    }

    public Class[] getVisualizations() {
        return visualizations;
    }

    public void setVisualizations(Class[] visualizations) {
        this.visualizations = visualizations;
    }

    public int getUsed_alg() {
        return used_alg;
    }

    public void setUsed_alg(int used_alg) {
        this.used_alg = used_alg;
    }

    public int getUsed_vis() {
        return used_vis;
    }

    public void setUsed_vis(int used_vis) {
        this.used_vis = used_vis;
    }


}

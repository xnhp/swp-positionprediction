package project.software.uni.positionprediction.util;

import java.util.LinkedList;

import project.software.uni.positionprediction.algorithm.AlgorithmSimilarTrajectory;

public class XML {

    private int hours_past;
    private int hours_fut;
    private LinkedList<Class> algorithms;
    private LinkedList<Class> visualizations;
    private Class used_alg;
    private Class used_vis;
    private LinkedList<String> downloaded_studies;


    public XML() {

    }

    public XML(int hours_past, int hours_fut, LinkedList<Class> algorithms, LinkedList<Class> visualizations, Class used_alg, Class used_vis, LinkedList<String> downloaded_studies) {
        this.hours_past = hours_past;
        this.hours_fut = hours_fut;
        this.algorithms = algorithms;
        this.visualizations = visualizations;
        this.used_alg = used_alg;
        this.used_vis = used_vis;
        this.downloaded_studies = downloaded_studies;
    }

    public Class getUsed_alg() {
        return used_alg;
    }

    public void setUsed_alg(Class used_alg) {
        this.used_alg = used_alg;
    }

    public Class getUsed_vis() {
        return used_vis;
    }

    public void setUsed_vis(Class used_vis) {
        this.used_vis = used_vis;
    }

    public double getHours_past() {
        return hours_past;
    }

    public void setHours_past(int hours_past) {
        this.hours_past = hours_past;
    }

    public double getHours_fut() {
        return hours_fut;
    }

    public void setHours_fut(int hours_fut) {
        this.hours_fut = hours_fut;
    }

    public LinkedList<Class> getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(LinkedList<Class> algorithms) {
        this.algorithms = algorithms;
    }

    public LinkedList<Class> getVisualizations() {
        return visualizations;
    }

    public void setVisualizations(LinkedList<Class> visualizations) {
        this.visualizations = visualizations;
    }

    public LinkedList<String> getDownloaded_studies() {
        return downloaded_studies;
    }

    public void setDownloaded_studies(LinkedList<String> downloaded_studies) {
        this.downloaded_studies = downloaded_studies;
    }
}

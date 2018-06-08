package project.software.uni.positionprediction.util;

import java.util.LinkedList;

public class XML {

    private double hours_past;
    private double hours_fut;
    private LinkedList<Class> algorithms;
    private LinkedList<Class> visualizations;
    private LinkedList<String> downloaded_studies;

    public XML(double hours_past, double hours_fut, LinkedList<Class> algorithms, LinkedList<Class> visualizations, LinkedList<String> downloaded_studies) {
        this.hours_past = hours_past;
        this.hours_fut = hours_fut;
        this.algorithms = algorithms;
        this.visualizations = visualizations;
        this.downloaded_studies = downloaded_studies;
    }

    public double getHours_past() {
        return hours_past;
    }

    public void setHours_past(double hours_past) {
        this.hours_past = hours_past;
    }

    public double getHours_fut() {
        return hours_fut;
    }

    public void setHours_fut(double hours_fut) {
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

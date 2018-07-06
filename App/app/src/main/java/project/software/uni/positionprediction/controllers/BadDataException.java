package project.software.uni.positionprediction.controllers;

public class BadDataException extends Exception {
    public final int percentage_bad_data;

    public BadDataException(int percentage_bad_data) {
        this.percentage_bad_data = percentage_bad_data;
    }
}

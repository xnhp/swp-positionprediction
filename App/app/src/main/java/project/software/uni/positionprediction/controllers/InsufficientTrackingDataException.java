package project.software.uni.positionprediction.controllers;

class InsufficientTrackingDataException extends Exception {
    private final String message;

    public InsufficientTrackingDataException(String s) {
        this.message = s;
    }
}

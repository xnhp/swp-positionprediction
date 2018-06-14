package project.software.uni.positionprediction.visualisation;

public enum StyleTrajectory {

    // todo: define style  in XML file instead of enum ?
    // todo: data type for colors. TJ: Available in Android

    // predicted data
    // --------------

    predPointCol("#ff0077"), // pink
    predLineCol("#e28a16"),  // orange
    predPointRadius("20"),


    // past data
    // ---------

    pastPointCol("#9116e2"), // purple
    pastLineCol("#1668e2"), // blue
    pastPointRadius("15"),


    // linkage between the two of them
    // -------------------------------

    connectingLineColor("#f7f300"); // yellow


    private final String value;

    StyleTrajectory(String value) {
        this.value = value;
    }

    public String asString() {
        return value;
    }

    public int asInt() {
        return Integer.parseInt(value);
    }
}

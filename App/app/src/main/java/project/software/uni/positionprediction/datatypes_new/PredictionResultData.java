package project.software.uni.positionprediction.datatypes_new;

import java.util.ArrayList;
import java.util.Map;

import project.software.uni.positionprediction.util.Shape;

public class PredictionResultData {
    Map<Shape, ArrayList<Locations>> data;

    public PredictionResultData(){
        /* syntax demo :
        data.put(Shape.TRAJECTORY, new ArrayList<Locations>());
        data.get(Shape.TRAJECTORY).add(new Trajectory());
        */
    }

    public void setData(Map<Shape, ArrayList<Locations>> data) {
        this.data = data;
    }

    public Map<Shape, ArrayList<Locations>> getData() {
        return data;
    }
}

package project.software.uni.positionprediction.datatypes_new;

import java.util.HashMap;
import java.util.Map;

import project.software.uni.positionprediction.util.Shape;

public class PredictionResultData {


    // ---------------
    // CLASS VARIABLES
    // ---------------

    private Map<Shape, Collection<? extends Locations>> data = new HashMap<>();


    // ------------
    // CONSTRUCTORS
    // ------------

    public PredictionResultData(){}


    public PredictionResultData(Locations locs){

        if(locs instanceof Trajectory){
            this.data.put(Shape.TRAJECTORY, new Collection<Trajectory>((Trajectory) locs));
        } else if(locs instanceof Cloud){
            this.data.put(Shape.CLOUD, new Collection<Cloud>((Cloud) locs));
        }

    }

    public PredictionResultData(Collection<? extends Locations> locs){

        if(locs.get(0) instanceof Trajectory){
            this.data.put(Shape.TRAJECTORY, locs);
        } else if(locs.get(0) instanceof Cloud){
            this.data.put(Shape.CLOUD, locs);
        }

    }


    public void add(Locations locs){
        /* There is a bug in here. This method is only needed fot more complex  prediction algorithms
        if(locs instanceof Trajectory){
            if (this.data.containsKey(Shape.TRAJECTORY)) {
                ((Collection<Trajectory>) this.getData().get(Shape.TRAJECTORY)).add((Trajectory) locs);
            } else {
                this.data.put(Shape.TRAJECTORY, new Collection<Trajectory>(locs));
            }
        } else if(locs instanceof Cloud) {
            if (this.data.containsKey(Shape.CLOUD)) {
                ((Collection<Cloud>) this.getData().get(Shape.CLOUD)).add((Cloud) locs);
            } else {
                this.data.put(Shape.CLOUD, new Collection<Cloud>(locs));
            }
        }
        */
    }

    public void add(Collection<? extends Locations> locs){
        // This method is only needed fot more complex  prediction algorithms
    }


    // -------------------
    // GETTERS AND SETTERS
    // -------------------

    public void setData(Map<Shape, Collection<? extends Locations>> data) {
        this.data = data;
    }

    public Map<Shape, Collection<? extends Locations>> getData() {
        return data;
    }


}

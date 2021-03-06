package project.software.uni.positionprediction.datatypes;

import android.util.Log;

public class PredictionResultData {


    // ---------------
    // CLASS VARIABLES
    // ---------------

    private Collections<EShape, Shape> shapes = new Collections();
    private String message;


    // ------------
    // CONSTRUCTORS
    // ------------

    public PredictionResultData(){}

    public PredictionResultData(Shape shape){

        if(shape instanceof Trajectory){
            Log.i("predictionResultData", "Is instance of Trajectory");
            Collection<Trajectory> c = new Collection(shape);
            Log.i("predictionResultData","collection size: " + c.size());
            this.shapes.put(EShape.TRAJECTORY, c);
        } else if(shape instanceof Cloud){
            this.shapes.put(EShape.CLOUD, new Collection(shape));
        }

    }

    public PredictionResultData(Collection<? extends Shape> shapes){
        if (shapes != null) {
            if(shapes.get(0) instanceof Trajectory){
                this.shapes.put(EShape.TRAJECTORY, new Collection(shapes));
            } else if(shapes.get(0) instanceof Cloud){
                this.shapes.put(EShape.CLOUD, new Collection(shapes));
            }
        }
    }


    public void add(Shape shape){
        /* There is a bug in here. This method is only needed fot more complex  prediction algorithms
        if(locs instanceof TrajectoryVis){
            if (this.data.containsKey(EShape.TRAJECTORY)) {
                ((Collection<TrajectoryVis>) this.getData().get(EShape.TRAJECTORY)).add((TrajectoryVis) locs);
            } else {
                this.data.put(EShape.TRAJECTORY, new Collection<TrajectoryVis>(locs));
            }
        } else if(locs instanceof Cloud) {
            if (this.data.containsKey(EShape.CLOUD)) {
                ((Collection<Cloud>) this.getData().get(EShape.CLOUD)).add((Cloud) locs);
            } else {
                this.data.put(EShape.CLOUD, new Collection<Cloud>(locs));
            }
        }
        */
    }

    public void add(Collection<? extends Shape> shapes){
        // This method is only needed fot more complex  prediction algorithms
    }

    public Collections<EShape,Shape> getShapes() {
        return shapes;
    }


    // -------------------
    // GETTERS AND SETTERS
    // -------------------

/*
    public void setData(Map<EShape, Collection<? extends Locations>> data) {
        this.data = data;
    }

    public Map<EShape, Collection<? extends Locations>> getData() {
        return data;
    }
*/

}

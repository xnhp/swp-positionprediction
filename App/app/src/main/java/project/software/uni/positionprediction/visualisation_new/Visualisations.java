package project.software.uni.positionprediction.visualisation_new;

import org.osmdroid.util.BoundingBox;

import project.software.uni.positionprediction.datatypes_new.Collection;
import project.software.uni.positionprediction.datatypes_new.Collections;
import project.software.uni.positionprediction.datatypes_new.EShape;

public class Visualisations extends Collections<EShape, Visualisation> {

    // ------------
    // CONSTRUCTORS
    // ------------

    public Visualisations(){ super();}


    public Visualisations(Visualisation vis){

        if(vis instanceof TrajectoryVis){
            this.put(EShape.TRAJECTORY, new Collection<TrajectoryVis>((TrajectoryVis) vis));
        } else if(vis instanceof CloudVis){
            this.put(EShape.CLOUD, new Collection<CloudVis>((CloudVis) vis));
        }

    }

    public Visualisations(Collection<? extends Visualisation> vis){

        if(vis.get(0) instanceof TrajectoryVis){
            this.put(EShape.TRAJECTORY, vis);
        } else if(vis.get(0) instanceof CloudVis){
            this.put(EShape.CLOUD, vis);
        }

    }


    public void add(Visualisation vis){
        if(vis instanceof TrajectoryVis){
            this.put(EShape.TRAJECTORY, new Collection<>((TrajectoryVis) vis));
        } else if(vis instanceof CloudVis){
            this.put(EShape.CLOUD, new Collection<>((CloudVis) vis));
        }
    }

    public void add(Collection<? extends Geometry> vis){ }

    public BoundingBox getBoungingBox(){
        BoundingBox result = null;
        // todo: for(Visualization vis : this){
        //     result.concat(vis.getBoundigBox);
        // }
        return result;
    }

}

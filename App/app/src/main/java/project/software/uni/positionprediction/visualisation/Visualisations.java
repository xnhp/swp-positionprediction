package project.software.uni.positionprediction.visualisation;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.osmdroid.util.BoundingBox;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import project.software.uni.positionprediction.datatypes.Collection;
import project.software.uni.positionprediction.datatypes.Collections;
import project.software.uni.positionprediction.datatypes.EShape;

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
    
    // (bm) i know this is bad, will make this nicer when
    // i have the time.
    private BoundingBox boundingBoxResult;

    /*
        todo: this might be a better way to iterate over the map:
         for (Entry<EShape, Collection<? extends Visualisation>> entry : this.entrySet()) { ... }
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public BoundingBox getBoundingBox(){
        /* does not work like this
        // todo: for(Visualization vis : this){
        //     result.concat(vis.getBoundigBox);
        // }
        */

        // alternative approach:
        boundingBoxResult = null; // this is a field because
        // inside a consumer we can neither modify external local variables
        // nor return a value

        // iterate over all visualisations in this object
        // (which is a map of visualisation types to visualisation collections)
        // cf https://docs.oracle.com/javase/8/docs/api/java/util/function/BiConsumer.html
        this.forEach(new BiConsumer<EShape, Collection<? extends Visualisation>>() {

            @Override
            public void accept(EShape eShape, Collection<? extends Visualisation> visualisations) {
                if (eShape == EShape.TRAJECTORY) {
                    // if shape of visualisation is trajectory,
                    // iterate over each trajectory and expand BB
                    visualisations.forEach(new Consumer<Visualisation>() {
                        @Override
                        public void accept(Visualisation visualisation) {
                            if (boundingBoxResult == null) {
                                boundingBoxResult = visualisation.getBoundingBox();
                                Log.i("Visualisations", "initiating boundingbox");
                            } else {
                                Log.i("Visualisations", "expanding boundingbox");
                                boundingBoxResult.concat(visualisation.getBoundingBox());
                            }
                        }
                    });
                } else if (eShape == EShape.CLOUD) {
                    // TODO
                    Log.e("Visualisations", "not implemented yet");
                }
                Log.i("Visualisations", eShape.toString() + " " + visualisations.toString());
            }
        });

        return boundingBoxResult;
    }

}

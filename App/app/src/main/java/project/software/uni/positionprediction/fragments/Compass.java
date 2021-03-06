package project.software.uni.positionprediction.fragments;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.controllers.PredictionWorkflow;
import project.software.uni.positionprediction.util.BearingListener;
import project.software.uni.positionprediction.util.BearingProvider;

public class Compass extends Fragment {

    private ImageView compassArrow = null;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        String logTag = "Compass";
        Log.i(logTag, "onCreateView");
        View view =  inflater.inflate(R.layout.fragment_compass, container, false);

        compassArrow = view.findViewById(R.id.compassArrow);

        // note that this depends on the arrow being available in the layout.
        initBearingProvider();

        return view;
    }


    private void rotateArrow(float angle){
        // arrow graphic points leftwards.
        // angle of 0 means that we are facing our target.
        angle -= 90;

        compassArrow.setPivotX(compassArrow.getWidth()/2);
        compassArrow.setPivotY(compassArrow.getHeight()/2);
        compassArrow.setRotation(angle);

        /*// another way, but doesnt allow rotation around
        // center that easily
        Matrix matrix = new Matrix();
        compassArrow.setScaleType(ImageView.ScaleType.MATRIX);
        matrix.postRotate(angle, 100f, 100f);
        compassArrow.setImageMatrix(matrix);*/

        /*// yet another way but but is animation *from* some angle to some angle
        Animation rotateAnimation = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setDuration(100);
        compassArrow.startAnimation(rotateAnimation);*/
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initBearingProvider() {

        if (PredictionWorkflow.vis_pred == null) return;

        double targetLat = PredictionWorkflow.vis_pred.getBoundingBox().getCenterLatitude();
        double targetLon = PredictionWorkflow.vis_pred.getBoundingBox().getCenterLongitude();
        Location targetLocation = new Location("target location");
        targetLocation.setLatitude(targetLat);
        targetLocation.setLongitude(targetLon);


        BearingProvider provider = BearingProvider.getInstance();
        provider.registerBearingUpdates(this.context, targetLocation, new BearingListener() {
            @Override
            public void onBearingChanged(float newBearing) {
                rotateArrow(newBearing);
            }

            @Override
            public void onProviderEnabled() {

            }

            @Override
            public void onProviderDisabled() {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

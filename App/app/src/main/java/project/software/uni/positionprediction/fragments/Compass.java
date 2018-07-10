package project.software.uni.positionprediction.fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.util.BearingListener;
import project.software.uni.positionprediction.util.BearingProvider;

public class Compass extends Fragment {

    private final String logTag = "Compass";
    private ImageView compassArrow = null;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
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


    private void initBearingProvider() {

        // todo: fetch location from prediction here
        // (do this after merge from master so i have it
        //  available)
        Location targetLocation = new Location("foo");
        targetLocation.setLatitude(48.856614); // somewhere in
        targetLocation.setLongitude(2.352222); // paris

        BearingProvider provider = BearingProvider.getInstance();
        provider.registerBearingUpdates(this.context, targetLocation, new BearingListener() {
            @Override
            public void onBearingChanged(float newBearing) {
                rotateArrow(newBearing);
            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

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
        // todo: disable sensor listeners
    }

    @Override
    public void onResume() {
        super.onResume();
        // todo: reenable sensor listeners
    }
}

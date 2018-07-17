package project.software.uni.positionprediction.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import project.software.uni.positionprediction.R;
import project.software.uni.positionprediction.activities.Cesium;
import project.software.uni.positionprediction.activities.OSM;


public class FloatingMapButtons extends android.support.v4.app.Fragment {

    private final String logTag = "FloatingMapButtons";

    private floatingMapButtonsClickListener clickListener;
    private FloatingActionButton showLocBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.i(logTag, "onCreateView");
        // tmpl: return super.onCreateView(inflater, container, savedInstanceState);
        View view =  inflater.inflate(R.layout.fragment_floatingmapbuttons, container, false);


        view.findViewById(R.id.fab_showData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onShowDataClick();
            }
        });

        view.findViewById(R.id.fab_showPred).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onShowPredClick();
            }
        });

        showLocBtn = view.findViewById(R.id.fab_showLoc);
        showLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // note that this class' toggleShowLocBtn is called by the listener.
                // this is because these two closely related things (actual functionality and
                // UI indication) shouldnt be without any link. If one fails,
                // the other should fail too.
                clickListener.onShowLocClick();
            }
        });

        view.findViewById(R.id.fab_switchMode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onSwitchModeClick();
            }
        });

        view.findViewById(R.id.fab_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onRefreshClick();
            }
        });

        if(getActivity() instanceof OSM){
            ((FloatingActionButton)view.findViewById(R.id.fab_switchMode))
                    .setImageResource(R.drawable.outdoor);
        }else if(getActivity() instanceof Cesium){
            ((FloatingActionButton)view.findViewById(R.id.fab_switchMode))
                    .setImageResource(R.drawable.home);
        }

        return view;
    }

    /**
     * Called by the click listener in the hosting activity.
     * @param state true if follow location is enabled
     */
     public void toggleShowLocBtn(boolean state) {
         // toggle icon that is displayed.
         // toggling of the actual functionality of following
         // the location is handled in the listener
         if (showLocBtn == null) {
             Log.e("FloatingMapButtons", "No button field to toggle");
             return;
         }
         if (state) {
             showLocBtn.setImageResource(R.drawable.location_on);
         } else {
             showLocBtn.setImageResource(R.drawable.location);
         }
     }

    /**
     * In the documentation [1], there are two main approaches described for
     * Fragment/Fragment or Fragment/Activity coordination:
     * 1.) Use a ViewModel that serves data [2]
     * 2.) Define a callback interface that the hosting activity must implement
     *      and a way to ensure that it really does so.
     * I decided to do the latter because that seems closer to what we actually
     * want to do here: WHEN button is clicked THEN in activity, do something
     *
     * [1] https://developer.android.com/guide/components/fragments
     * [2] https://developer.android.com/topic/libraries/architecture/viewmodel
     */
    public interface floatingMapButtonsClickListener {
        void onShowDataClick();
        void onShowPredClick();
        void onShowLocClick();
        void onSwitchModeClick();
        void onRefreshClick();
    }

    /**
     * This method is called when the system adds the Fragment to the Activity.
     * Here, we can ensure that the activity (`context`) implements the interface
     * by trying to cast it.
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            clickListener = (floatingMapButtonsClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement listener");
        }
    }



    @Override
    public void onPause() {
        super.onPause();
    }
}

package project.software.uni.positionprediction.osm;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions;
import org.osmdroid.views.overlay.simplefastpoint.StyledLabelledGeoPoint;

import java.util.ArrayList;
import java.util.List;

import project.software.uni.positionprediction.controllers.PredictionWorkflow;
import project.software.uni.positionprediction.util.GeoDataUtils;
import project.software.uni.positionprediction.visualisation.IVisualisationAdapter;
import project.software.uni.positionprediction.visualisation.StyledLineSegment;
import project.software.uni.positionprediction.visualisation.StyledPoint;
import project.software.uni.positionprediction.visualisation.TrajectoryVis;

import static project.software.uni.positionprediction.controllers.PredictionWorkflow.vis_pred;

/**
 * Takes care of calling the correct methods to draw the visualisation on the map.
 * Merely *draws* what is specified in the Geometry obejct
 */
public class OSMDroidVisualisationAdapter extends IVisualisationAdapter {

    public OSMDroidMap map;
    public OSMDroidMap getMap() {return map;}

    // padding to edges of map when zooming/panning
    // to view something on the map.
    // e.g. for zoomToBoundingBox
    private int zoomPadding = 15;

    @Override
    public void linkMap (Object mapView) {
        if (mapView instanceof OSMDroidMap) {
            this.map = (OSMDroidMap) mapView;
        } else {
            MapInitException e = new MapInitException();
            e.printStackTrace();
        }
    }


    public void setMapCenter(GeoPoint center) {
        map.setCenter(center);
    }


    @Override
    public void clear() {
        // OverlayManager is an instance of List<Overlay>
        // todo: compass also is overlay
        map.mapView.getOverlayManager().clear();
        map.mapView.invalidate();
    }

    /**
     * Zooms/Pans the map so that both past and prediction
     * visualisation are within view
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void showVisualisation() {
        if (PredictionWorkflow.vis_past == null || PredictionWorkflow.vis_pred == null) {
            Log.e("FloatingMapButtons", "visualisation (past or pred) not available");
            return;
        }

        BoundingBox bb1 = PredictionWorkflow.vis_past.getBoundingBox();
        BoundingBox bb2 = PredictionWorkflow.vis_pred.getBoundingBox();
        BoundingBox targetBB = bb1.concat(bb2);

        map.mapView.invalidate();
        map.safeZoomToBoundingBox(
                this,
                targetBB,
                false,
                this.zoomPadding
        );
    }

    @Override
    public void showData() {
        if (PredictionWorkflow.vis_past == null) {
            Log.e("FloatingMapButtons", "no past vis available");
            return;
        }
        map.mapView.invalidate();
        map.safeZoomToBoundingBox(
                this,
                PredictionWorkflow.vis_past.getBoundingBox(),
                false,
                this.zoomPadding
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void showPrediction() {
        if (PredictionWorkflow.vis_pred== null) {
            Log.e("FloatingMapButtons", "no prediction visualisation available (yet?)");
            return;
        }
        BoundingBox mybb = vis_pred.getBoundingBox();
        // BoundingBox testbb = new BoundingBox(10, 10, 20, 20);
        // final BoundingBox testbb = new BoundingBox(44899816, 5020385,44899001,5019482);


        MapView mMapView = map.mapView;
        mMapView.invalidate();
        map.safeZoomToBoundingBox(this, mybb, false, this.zoomPadding);

    }


    @Override
    public void visualiseSingleTraj(TrajectoryVis vis) {
        // draw styled linesegments

        String start_color = "#2A4B6E";
        int i = 0;

        for (StyledLineSegment seg :
                vis.getLine().styledLineSegments) {
            // todo: note: this will create an "osmdroid overlay" for each segment.
            // this is potentially bad for performance
            List<GeoPoint> locs = new ArrayList<>();
            locs.add(GeoDataUtils.LocationToGeoPoint(seg.start));
            locs.add(GeoDataUtils.LocationToGeoPoint(seg.end));
            map.drawPolyLine(locs, getColor(start_color, i));
            i++;

        }


        // draw styled points
        // obtain a list of styled points of the type that osmdroid needs.
        OSMStyledPointList osmStyledPoints = new OSMStyledPointList();
        for (StyledPoint styledPoint :
                vis.getLine().styledPoints) {
            Paint pointStyle = new Paint();
            pointStyle.setColor(Color.parseColor(styledPoint.pointColor));
            osmStyledPoints.pts.add(
                    new StyledLabelledGeoPoint(
                            styledPoint.location.getLat(),
                            styledPoint.location.getLon(),
                            null,
                            pointStyle,
                            new Paint() // dont care about textStyle?
                            )
            );
        }

        SimpleFastPointOverlayOptions options = PointsOptions
                .getDefaultStyle()
                // has to be called first because the parent classes methods return the object in the
                // more general type
                .setAlgorithm(SimpleFastPointOverlayOptions.RenderingAlgorithm.MAXIMUM_OPTIMIZATION)
                .setRadius(15) // todo: this shouldnt apply?
                .setIsClickable(false) // true by default
                .setCellSize(15) // cf internal doc
                .setSymbol(SimpleFastPointOverlayOptions.Shape.CIRCLE)
                ;
        final SimpleFastPointOverlay overlay = new SimpleFastPointOverlay(osmStyledPoints, options);
        map.mapView.getOverlays().add(overlay);

        if(vis.hasFunnel()); //todo: draw polygon
    }

    private String getColor(String start_color, int i) {
        long color = Color.parseColor(start_color);
        long new_color = color+i;

        String ret = start_color;
        try{
            ret = "#" + Integer.toHexString((int) new_color);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }


}


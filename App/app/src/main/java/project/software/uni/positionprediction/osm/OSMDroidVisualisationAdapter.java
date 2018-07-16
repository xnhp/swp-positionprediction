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
import project.software.uni.positionprediction.datatypes.Collection;
import project.software.uni.positionprediction.datatypes.Location;
import project.software.uni.positionprediction.datatypes.Locations;
import project.software.uni.positionprediction.util.GeoDataUtils;
import project.software.uni.positionprediction.visualisation.IVisualisationAdapter;
import project.software.uni.positionprediction.visualisation.Polyline;
import project.software.uni.positionprediction.visualisation.PredTrajectoryStyle;
import project.software.uni.positionprediction.visualisation.StyledLineSegment;
import project.software.uni.positionprediction.visualisation.StyledPoint;
import project.software.uni.positionprediction.visualisation.TrajectoryVis;

import static project.software.uni.positionprediction.controllers.PredictionWorkflow.vis_pred;
import static project.software.uni.positionprediction.util.GeoDataUtils.LocationToGeoPoint;
import static project.software.uni.positionprediction.visualisation.PredTrajectoryStyle.lineCol;

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
        // todo: does this also clear the location marker?

        // need to this, which will tell the locationOverlay
        // to stop following the location
        // because if it is cleared from the overlay list
        // , it will still work to follow the location
        map.disableFollowLocation();

        map.mapView.getOverlayManager().clear();
        map.mapView.invalidate();

        map.enableLocationOverlay();
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

        // 1.) DRAW FUNNEL

        if(vis.hasFunnel()){
            // draw fake funnel for testing

            Locations coords_right = new Locations();
            Locations coords_left = new Locations();
            for(StyledPoint point : vis.getLine().styledPoints) {
                Location loc = point.location;
                Location right = new Location(loc.getLat()+0.000, loc.getLon()+0.000);
                coords_right.add(right);
                Location left = new Location(loc.getLat()-0.000, loc.getLon()-0.000);
                coords_left.add(left);
            }
            //List<Integer> aList = new ArrayList<>();

            // assemble polygon coordinates clockwise
            Locations funnel = new Locations(Collection.add(coords_left, coords_right.reverse()));

            List<GeoPoint> funnelPoints = new ArrayList<>();
            for(Location loc : funnel){
                Log.i("OSM adapter", "funnel: " + loc.toString());
                GeoPoint point = LocationToGeoPoint(loc);
                new GeoPoint(0.0, 0.0);
                funnelPoints.add(point);
            }


            List<GeoPoint> funnelTest = new ArrayList<>();
            funnelTest.add(new GeoPoint(-0.7, -89));
            funnelTest.add(new GeoPoint(0.2, -91));
            funnelTest.add(new GeoPoint(-1.4,-90));


            List<GeoPoint> funnelCoords = new ArrayList<>();
            for(Location loc : vis.getFunnel().locations){
                Log.i("osmDroidAdapter", "funnel coord: " + loc.toString());
                funnelCoords.add(LocationToGeoPoint(loc));
            }

            if(true/*vis.hasFunnel()*/){
                Log.i("osmDroidAdapter", "fake funnel drawn");
                /*map.drawPolygon(
                        funnelTest,
                        PredTrajectoryStyle.lineCol,
                        PredTrajectoryStyle.lineCol,
                        PredTrajectoryStyle.funnelOpacity
                );
                */
                map.drawPolygon(funnelCoords, lineCol, lineCol, PredTrajectoryStyle.funnelOpacity);
            };
        };


        // 2.) DRAW LINE

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

    @Override
    public void drawTrajectoryConnection(Polyline pline) {
        ArrayList<GeoPoint> locs = new ArrayList<GeoPoint>();
        locs.add(GeoDataUtils.LocationToGeoPoint(pline.locations.get(0)));
        locs.add(GeoDataUtils.LocationToGeoPoint(pline.locations.get(1)));
        map.drawPolyLine(
                locs,
                // this is fairly superfluous, i know, but still better
                // to reuse the concept than write something different.
                pline.styledLineSegments.get(0).lineColor
        );
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


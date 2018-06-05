package project.software.uni.positionprediction.osm;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.NonNull;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions;
import org.osmdroid.views.overlay.simplefastpoint.StyledLabelledGeoPoint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import project.software.uni.positionprediction.util.GeoDataUtils;
import project.software.uni.positionprediction.visualisation.IVisualisationAdapter;
import project.software.uni.positionprediction.visualisation.SingleTrajectoryVis;
import project.software.uni.positionprediction.visualisation.StyledLineSegment;
import project.software.uni.positionprediction.visualisation.StyledPoint;

/**
 * Takes care of calling the correct methods to draw the visualisation on the map.
 * Merely *draws* what is specified in the Visualisation obejct
 */
public class OSMDroidVisualisationAdapter implements IVisualisationAdapter {

    private OSMDroidMap map;

    public void linkMap (Object mapView) {
        if (mapView instanceof OSMDroidMap) {
            this.map = (OSMDroidMap) mapView;
        } else {
            InvalidMapViewException e = new InvalidMapViewException();
            e.printStackTrace();
        }
    }

    @Override
    public void visualiseSingleTraj(SingleTrajectoryVis vis) {

        //map.draw(points, vis.defaultLineColor, vis.defaultPointColor);
        
        // 1.) draw styled polylines
        for (StyledLineSegment seg :
                vis.styledLineSegments) {
            // todo: note: this will create an "osmdroid overlay" for each segment.
            // this is potentially bad for performance
            List<GeoPoint> locs = new ArrayList<>();
            locs.add(GeoDataUtils.Location3DToGeoPoint(seg.start));
            locs.add(GeoDataUtils.Location3DToGeoPoint(seg.end));
            map.drawPolyLine(locs, seg.lineColor);
        }


        // 2.) draw styled points
        // obtain a list of styled points of the type that osmdroid needs.
        OSMStyledPointList osmStyledPoints = new OSMStyledPointList();
        for (StyledPoint styledPoint :
                vis.styledPoints) {
            Paint pointStyle = new Paint();
            pointStyle.setColor(Color.parseColor(styledPoint.pointColor));
            osmStyledPoints.pts.add(
                    new StyledLabelledGeoPoint(
                            styledPoint.location.getLoc_lat(),
                            styledPoint.location.getLoc_long(),
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
    }

}

class OSMStyledPointList implements SimpleFastPointOverlay.PointAdapter{

    ArrayList<IGeoPoint> pts = new ArrayList<>();

    // apparently StyledLabelledGeoPoint fits into IGeoPoint

    @Override
    public int size() {
        return pts.size();
    }

    @Override
    public IGeoPoint get(int i) {
        return (IGeoPoint) pts.get(i);
    }

    @Override
    public boolean isLabelled() {
        return false;
    }

    @Override
    public boolean isStyled() {
        return true;
    }

    @NonNull
    @Override
    public Iterator<IGeoPoint> iterator() {
        return pts.iterator();
    }
}
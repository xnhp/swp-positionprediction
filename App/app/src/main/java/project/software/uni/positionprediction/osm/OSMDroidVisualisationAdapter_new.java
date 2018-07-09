package project.software.uni.positionprediction.osm;

import android.graphics.Color;
import android.graphics.Paint;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions;
import org.osmdroid.views.overlay.simplefastpoint.StyledLabelledGeoPoint;

import java.util.ArrayList;
import java.util.List;

import project.software.uni.positionprediction.util.GeoDataUtils;
import project.software.uni.positionprediction.visualisation.IVisualisationAdapter;
import project.software.uni.positionprediction.visualisation.StyledLineSegment;
import project.software.uni.positionprediction.visualisation.StyledPoint;
import project.software.uni.positionprediction.visualisation.TrajectoryVis;

/**
 * Takes care of calling the correct methods to draw the visualisation on the map.
 * Merely *draws* what is specified in the Geometry obejct
 */
public class OSMDroidVisualisationAdapter_new extends IVisualisationAdapter {

    public OSMDroidMap map;
    public OSMDroidMap getMap() {return map;}

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
    public void panToBoundingBox(BoundingBox boundingBox) {
        map.setCenter(boundingBox.getCenterWithDateLine());

        // TJ: Not working (OSM issue discussed on stackoverflow)
        // map.mapView.zoomToBoundingBox(boundingBox, false);
    }


    @Override
    public void clear() {
        // OverlayManager is an instance of List<Overlay>
        // todo: compass also is overlay
        map.mapView.getOverlayManager().clear();
        map.mapView.invalidate();
    }


    @Override
    public void visualiseSingleTraj(TrajectoryVis vis) {
        // draw styled linesegments
        for (StyledLineSegment seg :
                vis.getLine().styledLineSegments) {
            // todo: note: this will create an "osmdroid overlay" for each segment.
            // this is potentially bad for performance
            List<GeoPoint> locs = new ArrayList<>();
            locs.add(GeoDataUtils.LocationToGeoPoint(seg.start));
            locs.add(GeoDataUtils.LocationToGeoPoint(seg.end));
            map.drawPolyLine(locs, seg.lineColor);
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


}


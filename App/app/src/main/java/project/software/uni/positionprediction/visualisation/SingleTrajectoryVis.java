package project.software.uni.positionprediction.visualisation;

import java.util.ArrayList;
import java.util.Iterator;

import project.software.uni.positionprediction.datatype.Location;
import project.software.uni.positionprediction.datatype.Locations;

/* a single trajectory merely consists of a series of points */
public class SingleTrajectoryVis extends Visualisation {


        // todo: only keep either locations or styledPoints
        // todo: this might be pulled up into a common ancestor "TrajectoryVis" but im not
        // sure yet how we will do it for multiple trajectories
        public final ArrayList<StyledPoint> styledPoints = new ArrayList<>();
        public final ArrayList<StyledLineSegment> styledLineSegments = new ArrayList<>();
        // why this?
        // because: if points will be styled individually, *at some point* we need to fetch
        // the colour for each point.
        // better do it here once than twice for osm and cesium.
        // why style points/line segments individually?
        // because: 1) to distinguish prediction vis from past tracks vis
        //          2) to visualise which data is more recent in past tracks (see benny's screenshot)


        public SingleTrajectoryVis(Locations locations) {
            super(locations);
        }

        public SingleTrajectoryVis(Locations pts, String pointColor, String lineColor, int pointRadius) {
            super(pts);
            makeStyledObjects(pointColor, lineColor, pointRadius);
        }


        // todo: rewrite
        private void makeStyledObjects(String pointColor, String lineColor, int pointRadius) {
            Iterator<Location> it = locations.iterator();
            Location prev = locations.get(0);
            while(it.hasNext()) {
                Location next = it.next();
                this.styledPoints.add(new StyledPoint(next, pointColor, pointRadius));
                if (prev != next) {
                    this.styledLineSegments.add(new StyledLineSegment(prev, next, lineColor));
                }
                prev = next;
            }
        }



        /**
         * Connects most recent point of a with first point of by a coloured line.
         * Note: this method does not call makeStyledObjects.
         * @param a First trajectory
         * @param b Second trajectory
         * @param connectingLineColor Colour for connecting line
         * @return
         */
        // todo: data type for colors
        public static SingleTrajectoryVis concat(SingleTrajectoryVis a, SingleTrajectoryVis b, String connectingLineColor) {
            // add all points
            SingleTrajectoryVis c = new SingleTrajectoryVis(a.locations);
            c.locations.addAll(b.locations);
            c.styledPoints.addAll(a.styledPoints);
            c.styledPoints.addAll(b.styledPoints);
            // add all line segments of a
            c.styledLineSegments.addAll(a.styledLineSegments);
            // add a connecting line segment from the last point in a to the first point in b
            StyledPoint last = a.styledPoints.get(a.styledPoints.size()-1);
            StyledPoint first = b.styledPoints.get(0);
            c.styledLineSegments.add(new StyledLineSegment(last.location, first.location, connectingLineColor));
            // add all line segments of b
            c.styledLineSegments.addAll(b.styledLineSegments);
            return c;
        }
}

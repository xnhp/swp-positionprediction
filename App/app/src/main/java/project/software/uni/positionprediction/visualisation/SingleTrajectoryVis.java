package project.software.uni.positionprediction.visualisation;

import project.software.uni.positionprediction.datatype.Locations2D;

/* a single trajectory merely consists of a series of points */
public class SingleTrajectoryVis extends Visualisation {

        // Graphic attributes
        public String lineColor = "red";
        public String pointColor = "red";
        public int pointRadius = 15;

        // Constructor
        public SingleTrajectoryVis(Locations2D locations) {super(locations);}

}

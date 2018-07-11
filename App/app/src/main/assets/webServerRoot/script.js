
/* Cesium initialisation */
var viewer = new Cesium.Viewer('cesiumContainer',{
    navigationHelpButton: false,
    timeline: false
});

// it seems like there is no zoom event.
// however, there are moveStart and moveEnd
// which are called on zoom.
// use moveEnd in case zoom is not instantaneous
// viewer.camera.moveEnd.addEventListener(function() { 
//     // get camera's height above ground in meters
//     alt = getCameraHeightAboveGround;
//     console.log(alt);
// });


// as of right now, this is not needed
// committing it because it may be needed in the future.
function getCameraHeightAboveGround() {
    var ellipsoid = viewer.scene.mapProjection.ellipsoid;
    var cartographic = new Cesium.Cartographic();
    var cartesian = new Cesium.Cartesian3();
    var camera = viewer.scene.camera;
    ellipsoid.cartesianToCartographic(camera.positionWC, cartographic);
    return cartographic.height;
}

/*
    Visualise a single trajectory.
    Note that past and prediction visualisation
    is called independently
*/
function visualiseSingleTraj(jsonData) {
    var json = JSON.parse(jsonData);

    // draw tracking points of single trajectory
    for(var i = 0; i < json.polyline.styled_points.length; i++) {
        var that = json.polyline.styled_points[i]
        var lon = that.location.lon;
        var lat = that.location.lat;
        // assume alt of 0 if not set
        var alt = typeof that.location.alt == 'undefined' ? 0 : that.location.alt;
        var color = Cesium.Color.fromCssColorString(that.point_color);
        var radius = that.point_radius;
        createPoint(lon, lat, alt, radius, color);
    }

    // draw line segments between tracking points
    // these are individual polylines because potentially,
    // each can have its own style
    json.polyline.styled_line_segments.forEach(seg => {
        // assume alt of 0 if not set
        seg.start.alt = typeof seg.start.alt == 'undefined' ? 0 : seg.start.alt;
        viewer.entities.add({
            polyline: {
                positions: Cesium.Cartesian3.fromDegreesArrayHeights([
                    seg.start.lon,
                    seg.start.lat,
                    seg.start.alt,
                    seg.end.lon,
                    seg.end.lat,
                    seg.end.alt
                ]),
                width: 2,
                material : Cesium.Color.fromCssColorString(seg.line_color)
            }
        })
    });

    viewer.zoomTo(viewer.entities);
}

function setCenter(jsonData) {
    // ...
}

/*
    expects a bounding box as a json string
        [
            "north": lat,
            "south": lat,
            ...
        ]
*/
function panToPredPoint(jsonData) {
    data = JSON.parse(jsonData);



    // todo
    // VisualisationWorkFlow and VisAdapter are written so that
    // both past and pred visualisation come through the same
    // methods (for instance visualiseSingleTraj)
    // so based on that, there is no distinction between past
    // and pred vis.
    //
    // However, a visualisationAdapter saves the visualisations
    // in a field.
    //
    // So, this should be implemented on the java-side:
    // get the position to zoom to (as bounding box? sample code cf [1]),
    // then call a JS method with that position.
    //
    // [1] https://gis.stackexchange.com/questions/239180/cesium-camera-setview-to-rectangle-clipping-the-rectangle
}

function panToBoundingBox() {
    // todo
    // this doesnt work correctly right now because the user
    // location marker also is an entity.
    // ccf comments in panToPredPoint
    // viewer.zoomTo(viewer.entities);
}



// reference to a cesium entity that marks the users location
var locationIndicator;

// pan to location indicated by locationIndicator
// triggered by click on floating map button
function panToUserLoc() {
    viewer.zoomTo(locationIndicator);
}

// updates `locationIndicator` to the given position
// returns the updated entity.
// triggered when android recevies a location update
function updateLocation(jsonData) {
    data = JSON.parse(jsonData);
    console.log(data);
    if (typeof locationIndicator == 'undefined') {
        // create, draw sphere and save reference to it
        console.log("foo");
        locationIndicator = createPoint(
            data.lon,
            data.lat,
            0,
            15,
            Cesium.Color.BLUE
        );
    } else { // there already is a location indicator,
             // update its position
        locationIndicator.position = Cesium.Cartesian3.fromDegrees(
            data.lon,
            data.lat,
            0
        );
    }
    return locationIndicator;
}


/*
    Graphical Primitives
*/
// draw a simple sphere
// note that this sphere can be referenced by a name attribute
// or js reference. So, we dont have to draw a new sphere
// each time. Hence the method name.
// for param color, expects something like Cesium.Color.BLUE
// returns the created sphere entity
function createSphere(lon, lat, alt, color, radius) {
    var cesCol;
    var sphere = viewer.entities.add({
        position: Cesium.Cartesian3.fromDegrees(lon, lat, alt),
        ellipsoid : {
            radii : new Cesium.Cartesian3(radius, radius, radius),
            material : color
        }
    });
    return sphere;
}

/*
    Create and add an anonymous "Point".
    cf https://cesiumjs.org/tutorials/Visualizing-Spatial-Data/#points-billboards-and-labels

    Note that Points are different from 3D objects.
    Main characteristic is that points stay fixed in screen
    pixel size, that is, their screen size does not change
    on zoom.

    As far as I can see this already the case of polylines
*/
function createPoint(lon, lat, alt, radius, color){
      return viewer.entities.add({
        position : Cesium.Cartesian3.fromDegrees(lon, lat, alt),
        point : {
            pixelSize : radius,
            color : color
        }
    });
}

function createFlatEllipse(lon, lat, alt, id) {
    return viewer.entities.add({
        position: Cesium.Cartesian3.fromDegrees(lon, lat, alt),
        id : id,
        ellipse : {
            semiMinorAxis : 1000.0,
            semiMajorAxis : 1000.0,
            height: 8000,
            material : Cesium.Color.fromCssColorString('#E28A16'),
            outline : true
            }
        });
}


// konstanz 47.677950, 9.173238

// var myPoint = createPoint(47.677950, 9.173238);
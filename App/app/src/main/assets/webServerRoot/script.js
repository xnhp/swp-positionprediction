
/* Cesium initialisation */
var viewer = new Cesium.Viewer('cesiumContainer');

// to differ between past and prediction data points, 0 means past is not done yet
var close11 = 0;

/*
    These functions are called from Java
*/
function visualiseSingleTraj(jsonData) {

    var json = JSON.parse(jsonData);
    console.log("received data", json);

    // visualise the prediction data point (after past data points are done)
    if(close11 === 1) {
        var pointPred = viewer.entities.add({
        position: Cesium.Cartesian3.fromDegrees(json.polyline.styled_points[0].location.lon, json.polyline.styled_points[0].location.lat, 8000),
        id : 'pointPred',
        name : 'prediction point',
        ellipse : {
            semiMinorAxis : 1000.0,
            semiMajorAxis : 1000.0,
            height: 8000,
            material : Cesium.Color.fromCssColorString('#E28A16'),
            outline : true
            }
        });

        if(json.hasOwnProperty("funnel")){
            visualiseFunnel(json.funnel);
        }
    }

    // visualise the past data points and the lines between the points
    if(close11 === 0) {
        for(var i = 0; i < json.polyline.styled_points.length; i++) {

            var pointsPast = viewer.entities.add({
            position: Cesium.Cartesian3.fromDegrees(json.polyline.styled_points[i].location.lon, json.polyline.styled_points[i].location.lat, 5000),
            name : 'past points',
            ellipse : {
                semiMinorAxis : 1000.0,
                semiMajorAxis : 1000.0,
                height: 5000,
                material : Cesium.Color.fromCssColorString('#9116E2'),
                outline : true
                }
            });

        }

        for(var i = 1; i < json.polyline.styled_points.length; i++) {
            var polylinesPast = viewer.entities.add({
                polyline : {
                    positions : Cesium.Cartesian3.fromDegreesArrayHeights([json.polyline.styled_points[i-1].location.lon, json.polyline.styled_points[i-1].location.lat, 4900,
                                                                    json.polyline.styled_points[i].location.lon, json.polyline.styled_points[i].location.lat, 4900]),
                width : 2,
                material : Cesium.Color.fromCssColorString('#1668E2')
            }});
        }

        // 1 means past is done
        close11 = 1;
    }

    viewer.zoomTo(viewer.entities);
}

function visualiseFunnel(json){
    var points = [];
    for(var i = 0; i < json.length; i++){
        points.push(json[i].lon);
        points.push(json[i].lat);
    }

    var funnel = viewer.entities.add({
        name : 'predFunnel',
        polygon : {
            hierarchy : Cesium.Cartesian3.fromDegreesArray(points),
            height : 5000,
            material : Cesium.Color.GREEN.withAlpha(0.5),
            outline : false
        }
    });
}

function visualiseMultipleClounds(jsonData){

    var json = JSON.parse(jsonData);
    console.log("received data", json);

    for(var i = 0; i < json.clouds.length; i++){
        visualiseClound(json.clouds[i]);
    }

}

function visualiseSingleCloud(jsonData){

    var json = JSON.parse(jsonData);
    console.log("received data", json);

    visualiseClound(json);

}


function visualiseClound(json){

    for(var i = 0; i < json.points.length; i++){
        var pointsPast = viewer.entities.add({
            position: Cesium.Cartesian3.fromDegrees(json.points.lon[i], json.points.lat[i], 5000),
            name : 'cloudPoints',
            ellipse : {
                semiMinorAxis : 1000.0,
                semiMajorAxis : 1000.0,
                height: 5001,
                material : Cesium.Color.RED,
                outline : false
            }
        });
    }

    var hull = [];

    for(var j = 0; j < json.hull.length; j++){
        hull.push(json.hull[j].lon);
        hull.push(json.hull[j].lat);
    }

    var cloud = viewer.entities.add({
        name : 'cloudHull',
        polygon : {
            hierarchy : Cesium.Cartesian3.fromDegreesArray(hull),
            height : 5000,
            material : Cesium.Color.GREEN.withAlpha(0.5),
            outline : false
        }
    });

}

function setCenter(jsonData) {
    // ...
}

function panToPredPoint() {
    viewer.zoomTo(viewer.entities.getById('pointPred'));
}

function panToBoundingBox() {
    viewer.zoomTo(viewer.entities);
}

function updateLocation(jsonData) {
    console.log("received new location");
    console.log(jsonData);
}




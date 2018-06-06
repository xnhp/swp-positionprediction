var viewer = new Cesium.Viewer('cesiumContainer');

function getRandomAmountPoints() {
    return Math.floor(Math.random() * (7 - 3 + 1)) + 3;
}

function getRandomLongitude() {
    return Math.random() * (9.299999 - 9.000000) + 9.000000;
}

function getRandomLatitude() {
    return Math.random() * (47.899999 - 47.650000) + 47.650000;
}

function getRandomHeight() {
    return Math.floor(Math.random() * (2000 - 50 + 1)) + 50;
}

var longitudes = [];
var latitudes = [];
var heights = [];
var points = [];
var colors = [];

var amountPoints = getRandomAmountPoints();

for(var i = 0; i < amountPoints; i++) {
    longitudes[i] = getRandomLongitude();
    latitudes[i] = getRandomLatitude();
    heights[i] = getRandomHeight();
    
    if(i === amountPoints - 1){
        colors[i] = Cesium.Color.WHITE;
    }
    else {
        colors[i] = Cesium.Color.RED;
    }
    
    points[i] = viewer.entities.add({
    position: Cesium.Cartesian3.fromDegrees(longitudes[i], latitudes[i], heights[i]),
    name : 'circle with height and outline',
    ellipse : {
        semiMinorAxis : 100.0,
        semiMajorAxis : 100.0,
        height: heights[i],
        material : colors[i],
        outline : true
        }
    });
}

for(i = 0; i < amountPoints - 1; i++){
    viewer.entities.add({
    polyline : {
        positions : Cesium.Cartesian3.fromDegreesArrayHeights([longitudes[i], latitudes[i], heights[i],
                                                        longitudes[i+1], latitudes[i+1], heights[i+1]]),
    width : 3,
    material : Cesium.Color.BLACK
    }});
}

viewer.zoomTo(viewer.entities);
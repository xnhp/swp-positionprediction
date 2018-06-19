var viewer = new Cesium.Viewer('cesiumContainer');

var longitudes = [];
var latitudes = [];
var altitudes = [];
var points = [];
var colors = [];

var amountPoints = injectedObject.getAmountPoints();

for(var i = 0; i < amountPoints; i++) {
    longitudes.push(injectedObject.getLongitudes(i));
    latitudes.push(injectedObject.getLatitudes(i));
    altitudes.push(injectedObject.getAltitudes(i));
}

for(var i = 0; i < amountPoints; i++) {

    points[i] = viewer.entities.add({
    position: Cesium.Cartesian3.fromDegrees(longitudes[i], latitudes[i], altitudes[i]),
    name : 'circle with height and outline',
    ellipse : {
        semiMinorAxis : 500.0,
        semiMajorAxis : 500.0,
        height: altitudes[i],
        material : Cesium.Color.RED,
        outline : true
        }
    });
}

viewer.zoomTo(viewer.entities);

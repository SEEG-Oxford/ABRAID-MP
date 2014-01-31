// Initalise map at "map" div
var map1 = L.map('DEMap', {
    attributionControl:false
}).setView([51.505, -0.09],2);
map1.setMaxBounds([ [-89,-179], [89,179] ]);

// Add the base layer  with WMS GET request
var geoServerUrl = 'http://map1.zoo.ox.ac.uk/geoserver/Explorer/wms';
var baseLayer1 = L.tileLayer.wms(geoServerUrl, {
    layers: ['Explorer:countryborders'],
    format: 'image/png',
    reuseTiles: true
}).addTo(map1);

var map2 = L.map('OPMap', {
    attributionControl:false
}).setView([51.505, -0.09],2);
map2.setMaxBounds([ [-89,-179], [89,179] ]);

var baseLayer2 = L.tileLayer.wms(geoServerUrl, {
    layers: ['Explorer:countryborders'],
    format: 'image/png',
    reuseTiles: true
}).addTo(map2);

$("#DEMap").click(function() {
    $("#OPMap").parent().slideToggle();
});
$("#OPMap").click(function() {
    $("#DEMap").parent().slideToggle();
});
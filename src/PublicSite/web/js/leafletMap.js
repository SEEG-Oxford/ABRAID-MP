// Initialise map at "map" div
var map = L.map('map', {
    attributionControl: false
}).setView([51.505, -0.09], 4);
map.setMaxBounds([ [-89,-179], [89, 179] ]);

// Add the base layer with WMS GET request
var geoServerUrl = 'http://map1.zoo.ox.ac.uk/geoserver/Explorer/wms';
var baseLayer = L.tileLayer.wms(geoServerUrl, {
    layers: ['Explorer:countryborders'],
    format: 'image/png',
    reuseTiles: true
}).addTo(map);
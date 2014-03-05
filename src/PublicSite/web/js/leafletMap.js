/**
 * JS file for adding Leaflet map and layers.
 * Copyright (c) 2014 University of Oxford
 */

// Initialise map at "map" div
var map = L.map('map', {
    attributionControl: false,
    zoomControl: false,
    zoomsliderControl: true,
    maxBounds: [ [-89,-179], [89, 179] ],
    maxZoom:10,
    minZoom:3
}).setView([51.505, -0.09], 4);

// Add the simplified shapefile base layer with WMS GET request from localhost GeoServer
var geoServerUrl = 'http://localhost:8081/geoserver/abraid/wms';
var baseLayer = L.tileLayer.wms(geoServerUrl, {
    layers: ['abraid:simplified_base_layer'],
    format: 'image/png',
    reuseTiles: true
}).addTo(map);

// Add disease occurrence points to map
// First add empty geoJson layer to map, with styling options, then get JSON data using an AJAX request
var locationLayer = L.geoJson([], {
    pointToLayer: locationLayerPoint
}).addTo(map);

function locationLayerPoint(feature, latlng) {
    return L.circleMarker(latlng, {
        stroke: false,
        fill: true,
        fillOpacity: 0.8,
        radius: 4
    });
}

// Get the GeoJSON Feature Collection and add the data to the geoJson layer
// TODO: Get DiseaseOccurrence GeoJSON from HUDL's web service, instead of abraid database location table from localhost GeoServer
$.getJSON('http://localhost:8081/geoserver/abraid/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=abraid:location&outputFormat=json', function(featureCollection) {
    locationLayer.addData(featureCollection);
});

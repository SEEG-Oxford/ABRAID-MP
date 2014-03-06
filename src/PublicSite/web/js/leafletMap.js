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
    pointToLayer: locationLayerPoint,
    style: locationLayerStyle
}).addTo(map);

// Define a circle, instead of the default leaflet marker, with listeners for mouse events
function locationLayerPoint(feature, latlng) {
    return L.circleMarker(latlng).on({
        mouseover: highlightFeature,
        mouseout: resetHighlight,
        click: selectFeature
    });
}

// Define default style for unselected points
function locationLayerStyle() { return {
    stroke: false,
    fill: true,
    color:'#9e1e71',
    fillOpacity: 0.8,
    radius: 4
};
}

// Reset to default style when a point is unselected (by clicking anywhere else on the map)
map.on('click', function() { locationLayer.setStyle(locationLayerStyle); });

// Change a point's colour on roll-over
function highlightFeature() {
    $("#source").innerHTML = feature.properties.name;
    this.setStyle({ color:'#84a872' });
}

// Return to default colour
function resetHighlight() {
    this.setStyle({ color:'#9e1e71'});
}

// Reset previous point to default style, so only one point can be selected at a time, and make selected point larger
function selectFeature() {
    locationLayer.setStyle(locationLayerStyle);
    this.setStyle({
        color:'#84a872',
        stroke: true,
        radius: 7
    });
}

// Get the GeoJSON Feature Collection and add the data to the geoJson layer
// TODO: Get DiseaseOccurrence GeoJSON from HUDL's web service, instead of abraid database location table from localhost GeoServer
$.getJSON('http://localhost:8081/geoserver/abraid/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=abraid:location&outputFormat=json', function(featureCollection) {
    locationLayer.addData(featureCollection);
});

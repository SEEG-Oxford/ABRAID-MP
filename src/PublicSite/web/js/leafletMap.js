/**
 * JS file for adding Leaflet map and layers.
 * Copyright (c) 2014 University of Oxford
 */
(function () {
    // Initialise map at "map" div
    var map = L.map('map', {
        attributionControl: false,
        zoomControl: false,
        zoomsliderControl: true,
        maxBounds: [
            [-89, -179],
            [89, 179]
        ],
        maxZoom: 10,
        minZoom: 3
    }).fitWorld();

    // Add the simplified shapefile base layer with WMS GET request from localhost GeoServer
    var geoServerUrl = 'http://localhost:8081/geoserver/abraid/wms';
    L.tileLayer.wms(geoServerUrl, {
        layers: ['abraid:simplified_base_layer'],
        format: 'image/png',
        reuseTiles: true
    }).addTo(map);

    // Global colour variables
    var defaultColour = '#bb619b';
    var highlightColour = '#9e1e71';
    var strokeColour = '#c478a9';

    // Define default style for unselected points
    var locationLayerStyle = {
        stroke: false,
        fill: true,
        color: strokeColour,
        fillColor: defaultColour,
        fillOpacity: 0.8,
        radius: 8
    };

    // Change a point's colour on roll-over
    function highlightFeature() {
        this.setStyle({
            stroke: true,
            color: highlightColour });
    }

    // Return to default colour
    function resetHighlight() {
        this.setStyle({ color: defaultColour});
    }

    // Change the point's colour and size when clicked
    function selectFeature(marker) {
        // Reset style of all other points, so only one point is selected at a time
        locationLayer.setStyle(locationLayerStyle);
        marker.setStyle({
            stroke: false,
            fillColor: highlightColour,
            radius: 11
        });
    }

    // Update the information panel when a point is selected
    function updateInformationPanel(properties) {
        $('#datapointInfoTitle').hide();
        $('#locationText').text(properties.name);
        $('#locationDiv').show();
    }

    function resetInformationPanel() {
        $('#datapointInfoTitle').show();
        $('#locationDiv').hide();
    }

    // Define a circle, instead of the default leaflet marker, with listeners for mouse events
    function locationLayerPoint(feature, latlng) {
        return L.circleMarker(latlng).on({
            mouseover: highlightFeature,
            mouseout: resetHighlight,
            click: function () {
                selectFeature(this);
                updateInformationPanel(feature.properties);
            }
        });
    }

    // Add disease occurrence points to map
    // First add empty geoJson layer to map, with defined styling options, then get JSON data using an AJAX request
    var locationLayer = L.geoJson([], {
        pointToLayer: locationLayerPoint,
        style: locationLayerStyle
    }).addTo(map);

    // Get the GeoJSON Feature Collection and add the data to the geoJson layer
    // TODO: Get DiseaseOccurrence GeoJSON from HUDL's web service, instead of abraid database location table from localhost GeoServer
    $.getJSON('http://localhost:8081/geoserver/abraid/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=abraid:location&outputFormat=json', function (featureCollection) {
        locationLayer.addData(featureCollection);
        map.fitBounds(locationLayer.getBounds());
    });

    // Reset to default style when a point is unselected (by clicking anywhere else on the map)
    // And clear the information box
    map.on('click', function () {
        resetInformationPanel();
        locationLayer.setStyle(locationLayerStyle);
    });
}());
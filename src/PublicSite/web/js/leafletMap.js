/**
 * JS file for adding Leaflet map and layers.
 * Copyright (c) 2014 University of Oxford
 */

var LeafletMap = (function () {
    'use strict';

    // Initialise map at "map" div
    var map = L.map('map', {
        attributionControl: false,
        zoomControl: false,
        zoomsliderControl: true,
        maxBounds: [ [-89, -179], [89, 179] ],
        maxZoom: 10,
        minZoom: 3,
        animate: true
    }).fitWorld();

    // Add the simplified shapefile base layer with WMS GET request
    L.tileLayer.wms(wmsUrl, {
        layers: ['abraid:simplified_base_layer'],
        format: 'image/png',
        reuseTiles: true
    }).addTo(map);

    // Global colour variables
    var defaultColour = '#bb619b';      // Lighter pink/red
    var highlightColour = '#9e1e71';    // The chosen pink/red compatible with colourblindness
    var strokeColour = '#c478a9';       // Darker pink/red

    // Define default style for unselected points
    var diseaseOccurrenceLayerStyle = {
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
            color: highlightColour
        });
    }

    // Return to default colour
    function resetHighlight() {
        this.setStyle({
            stroke: false,
            color: defaultColour
        });
    }

    // Change the point's colour and size when clicked
    function selectFeature(marker) {
        // First, reset the style of all other points on layer, so only one point is animated as selected at a time
        diseaseOccurrenceLayer.setStyle(diseaseOccurrenceLayerStyle);
        marker.setStyle({
            stroke: false,
            fillColor: highlightColour,
            radius: 11
        });
    }

    // Define a circle, instead of the default leaflet marker, with listeners for mouse events
    function diseaseOccurrenceLayerPoint(feature, latlng) {
        return L.circleMarker(latlng).on({
            mouseover: highlightFeature,
            mouseout: resetHighlight,
            click: function () {
                selectedPointViewModel.selectedPoint(feature);
                selectFeature(this);
            }
        });
    }

    // Add disease occurrence points to map
    // First add empty geoJson layer to map, with defined styling options, then get JSON data using an AJAX request
    var diseaseOccurrenceLayer = L.geoJson([], {
        pointToLayer: diseaseOccurrenceLayerPoint,
        style: diseaseOccurrenceLayerStyle
    }).addTo(map);

    // Reset to default style when a point is unselected (by clicking anywhere else on the map)
    map.on('click', function () {
        selectedPointViewModel.clearSelectedPoint();
        diseaseOccurrenceLayer.setStyle(diseaseOccurrenceLayerStyle);
    });

    return {
        diseaseOccurrenceLayer : diseaseOccurrenceLayer,
        map : map
    };
}());

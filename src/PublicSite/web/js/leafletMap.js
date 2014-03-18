/**
 * JS file for adding Leaflet map and layers.
 * Copyright (c) 2014 University of Oxford
 */
'use strict';

var LeafletMap = (function () {
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
                DataValidationViewModels.selectedPointViewModel.selectedPoint(feature);
                selectFeature(this);
            }
        });
    }

    // Define a cluster icon, so that nearby occurrences are grouped into one marker.
    // Text displays the number of points in the cluster. Styling in separate CSS file.
    function clusterLayerPoint(cluster) {
        return new L.DivIcon({
            html: '<div><span>' + cluster.getChildCount() + '</span></div>',
            className: 'marker-cluster',
            iconSize: new L.Point(40, 40)
        });
    }

    // Add disease occurrence points to map
    // First define styling options of the geoJson layer to which data (the feature collection) will be added later via AJAX request
    var diseaseOccurrenceLayer = L.geoJson([], {
        pointToLayer: diseaseOccurrenceLayerPoint,
        style: diseaseOccurrenceLayerStyle
    });

    // Add this geoJson layer to the styled markerClusterGroup layer, so that nearby occurrences are grouped
    var clusterLayer = L.markerClusterGroup({
        maxClusterRadius: 10,
        polygonOptions: {
            color: highlightColour,
            weight: 2,
            fillColor: defaultColour,
            dashArray: 3
        },
        spiderfyDistanceMultiplier: 2,
        iconCreateFunction: clusterLayerPoint
    }).addLayer(diseaseOccurrenceLayer).addTo(map);

    // Reset to default style when a point is unselected (by clicking anywhere else on the map)
    function resetSelectedPoint() {
        DataValidationViewModels.selectedPointViewModel.clearSelectedPoint();
        diseaseOccurrenceLayer.setStyle(diseaseOccurrenceLayerStyle);
    }
    map.on('click', resetSelectedPoint);
    clusterLayer.on('clusterclick', resetSelectedPoint);

    function updateDiseaseLayer(diseaseId) {
        clusterLayer.clearLayers();
        diseaseOccurrenceLayer.clearLayers();
        // Add the new feature collection to the clustered layer, and zoom to its bounds
        $.getJSON(baseUrl + 'datavalidation/diseases/' + diseaseId + '/occurrences', function (featureCollection) {
            clusterLayer.addLayer(diseaseOccurrenceLayer.addData(featureCollection));
            map.fitBounds(diseaseOccurrenceLayer.getBounds());
        });
    }

    return {
        updateDiseaseLayer: updateDiseaseLayer
    };
}());

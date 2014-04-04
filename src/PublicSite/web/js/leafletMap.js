/**
 * JS file for adding Leaflet map and layers.
 * Copyright (c) 2014 University of Oxford
 */
var LeafletMap = (function (L, $, DataValidationViewModels, wmsUrl, loggedIn) {
    'use strict';

    // Initialise map at "map" div
    var map = L.map('map', {
        attributionControl: false,
        zoomControl: false,
        zoomsliderControl: true,
        maxBounds: [ [-89, -179], [89, 179] ],
        maxZoom: 7,
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
    var defaultColour = '#c478a9';      // Lighter pink/red
    var highlightColour = '#9e1e71';    // The chosen pink/red compatible with colourblindness
    var strokeColour = '#ce8eb8';       // Darker pink/red

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
        resetLayerStyle();
        if (loggedIn) {
            marker.setStyle({
                stroke: false,
                fillColor: highlightColour,
                radius: 13
            });
        } else {
            marker._icon.classList.add('marker-question-selected');
        }
    }

    // Define a circle, instead of the default leaflet marker, with listeners for mouse events
    function diseaseOccurrenceLayerPoint(feature, latlng) {
        var marker;
        if (loggedIn) {
            marker = L.circleMarker(latlng).on({
                mouseover: highlightFeature,
                mouseout: resetHighlight
            });
        } else {
            marker = L.marker(latlng, { icon:
                L.divIcon({
                    html: '<div><span>?</span></div>',
                    className: 'marker-question',
                    iconAnchor: [10, 10]
                })
            });
        }
        marker.on({
            click: function () {
                DataValidationViewModels.selectedPointViewModel.selectedPoint(feature);
                selectFeature(this);
            }
        });
        return marker;
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

    // Map from the id of a point (or feature) to its marker layer, for use in removeReviewedPoint.
    var layerMap = {};
    // Add disease occurrence points to map
    // First define styling options of the geoJson layer to which data (the feature collection) will be added later via AJAX request
    var diseaseOccurrenceLayer = L.geoJson([], {
        pointToLayer: diseaseOccurrenceLayerPoint,
        style: diseaseOccurrenceLayerStyle,
        onEachFeature: function (feature, layer) {
            layer.on('add', function () {
                layerMap[feature.id] = layer;
            });
        }
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
        spiderfyDistanceMultiplier: 1.5,
        iconCreateFunction: clusterLayerPoint
    }).addLayer(diseaseOccurrenceLayer).addTo(map);


    // Reset the style of all markers on diseaseOccurrenceLayer
    function resetLayerStyle() {
        if (loggedIn) {
            diseaseOccurrenceLayer.setStyle(diseaseOccurrenceLayerStyle);
        } else {
            Object.keys(layerMap).forEach(function (key) {
                layerMap[key]._icon.classList.remove('marker-question-selected');
            });
        }
    }

    // Reset to default style when a point is unselected (by clicking anywhere else on the map)
    function resetSelectedPoint() {
        DataValidationViewModels.selectedPointViewModel.clearSelectedPoint();
        resetLayerStyle();
    }

    map.on('click', resetSelectedPoint);
    clusterLayer.on('clusterclick', resetSelectedPoint);

    // Add the new feature collection to the clustered layer, and zoom to its bounds
    function switchDiseaseLayer(diseaseId) {
        clusterLayer.clearLayers();
        diseaseOccurrenceLayer.clearLayers();
        var geoJsonRequestUrl = "";
        if (loggedIn) {
            geoJsonRequestUrl = baseUrl + 'datavalidation/diseases/' + diseaseId + '/occurrences';
        } else {
            geoJsonRequestUrl = baseUrl + 'static/defaultDiseaseOccurrences.json';
        }
        $.getJSON(geoJsonRequestUrl, function (featureCollection) {
            if (featureCollection.features.length != 0) {
                clusterLayer.addLayer(diseaseOccurrenceLayer.addData(featureCollection));
                map.fitBounds(diseaseOccurrenceLayer.getBounds());
            } else {
                map.fitWorld();
            }
        });
    }

    function removeReviewedPoint(id) {
        clusterLayer.clearLayers();
        diseaseOccurrenceLayer.removeLayer(layerMap[id]);
        clusterLayer.addLayer(diseaseOccurrenceLayer);
    }

    return {
        switchDiseaseLayer: switchDiseaseLayer,
        removeReviewedPoint: removeReviewedPoint
    };
}(L, jQuery, DataValidationViewModels, wmsUrl, loggedIn));
